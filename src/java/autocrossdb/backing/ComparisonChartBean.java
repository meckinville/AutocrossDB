/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package autocrossdb.backing;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.primefaces.event.ItemSelectEvent;
import org.primefaces.model.chart.Axis;
import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.CategoryAxis;
import org.primefaces.model.chart.LineChartModel;
import org.primefaces.model.chart.LineChartSeries;

/**
 *
 * @author rmcconville
 */
@ManagedBean(name="comparisonChart")
@SessionScoped
public class ComparisonChartBean implements Serializable
{
    private String driver1;
    private String driver2;
    private String type;
    private boolean showAverage;
    
    private double chartMax;
    private double chartMin;
    
    private Date startDate;
    private Date endDate;
    
    private LineChartModel lineModel;
    private DateFormat chartFormat;
    private List<String> selectedDrivers;
    
    private String d1TotalEvents;
    private String d2TotalEvents;
    private String d1RawWins;
    private String d2RawWins;
    private String d1RawDiff;
    private String d2RawDiff;
    private String d1PaxWins;
    private String d2PaxWins;
    private String d1PaxDiff;
    private String d2PaxDiff;
    
    private String dialogHeader;
    private String dialogText;
    private String d1DialogName;
    private String d2DialogName;
    private double d1DialogStat;
    private double d2DialogStat;
    private double d1DialogDiff;
    private double d2DialogDiff;
    private double avgDialogStat;
    private double avgDialogDiff1;
    private double avgDialogDiff2;

    
    @PersistenceContext
    private EntityManager em;
    
    @PostConstruct
    public void init()
    {
        chartFormat = new SimpleDateFormat("MM/dd/yyyy");
        selectedDrivers = new ArrayList<String>();
        Calendar now = Calendar.getInstance();
        endDate = now.getTime();
        now.set(Calendar.MONTH, now.get(Calendar.MONTH)-8);
        startDate = now.getTime();
        lineModel = new LineChartModel();
        lineModel.setTitle("Comparison Chart");
        lineModel.setLegendPosition("ne");
        lineModel.setAnimate(true);
        lineModel.setExtender("lineChartExtender");
        Axis yAxis = lineModel.getAxis(AxisType.Y);
        yAxis.setMin(0);
        yAxis.setMax(1);
        yAxis.setLabel("Run Time");
        LineChartSeries series = new LineChartSeries();
        series.set("Event Date", 50.5);
        lineModel.addSeries(series);
        CategoryAxis xAxis = new CategoryAxis("Events");
        xAxis.setTickAngle(-50);
        lineModel.getAxes().put(AxisType.X, xAxis);
        lineModel.setZoom(true);
        
    }

    public List<String> completeDriverText(String query)
    {
        List<String> results = new ArrayList<String>();
        List<String> driverList = em.createQuery("SELECT distinct(r.runDriverName) FROM Runs r ", String.class).getResultList();
        for(int x = 0; x < driverList.size(); x++)
        {
            if(driverList.get(x).contains(query.toUpperCase()))
            {
                results.add(driverList.get(x));
            }
        }
        return results;
    }
    
    public void itemSelect(ItemSelectEvent event)
    {
        Map<Object,Number> d1Map = lineModel.getSeries().get(0).getData();
        Map<Object,Number> d2Map = lineModel.getSeries().get(1).getData();
        
        Object[] d1Keys = d1Map.keySet().toArray();
        Object[] d2Keys = d2Map.keySet().toArray();
        
        dialogHeader = d1Keys[event.getItemIndex()].toString();
        
        d1DialogName = lineModel.getSeries().get(0).getLabel();
        d2DialogName = lineModel.getSeries().get(1).getLabel();
        
        d1DialogStat = d1Map.get(d1Keys[event.getItemIndex()]).doubleValue();
        d2DialogStat = d2Map.get(d2Keys[event.getItemIndex()]).doubleValue();
        
        d1DialogDiff = d1DialogStat - d2DialogStat;
        d1DialogDiff = (Math.round(d1DialogDiff * 1000d)) / 1000d;
        d2DialogDiff = d2DialogStat - d1DialogStat;
        d2DialogDiff = (Math.round(d2DialogDiff * 1000d)) / 1000d;
        
        if(showAverage)
        {
            Map<Object,Number> avgMap = lineModel.getSeries().get(2).getData();
            Object[] avgKeys = avgMap.keySet().toArray();
            avgDialogStat = Math.round(avgMap.get(avgKeys[event.getItemIndex()]).doubleValue() * 1000d) / 1000d;
            avgDialogDiff1 = Math.round((d1DialogStat - avgDialogStat) * 1000d) / 1000d;
            avgDialogDiff2 = Math.round((d2DialogStat - avgDialogStat) * 1000d) / 1000d;
        }
    }
    
    public void drawLineChart(List<Object[]> query, String chartTitle, int tickInterval, String yTitle)
    {
        
        Set<String> events = new TreeSet();
        for(String selectedDriver : selectedDrivers)
        {
            LineChartSeries series = new LineChartSeries();
            series.setLabel(selectedDriver);
            for(Object[] event : query)
            {
                events.add(event[4].toString());
                if(event[2].equals(selectedDriver) && event[3] != null)
                {
                    event[3] = Double.valueOf(event[3].toString());
                    if((double)event[3] > chartMax)
                    {
                        chartMax = (double)event[3];
                    }
                    if((double)event[3] < chartMin)
                    {
                        chartMin = (double)event[3];
                    }
                    series.set(event[1] + " " + chartFormat.format(event[0]), (double)event[3]);
                }
            }
            lineModel.addSeries(series);            
        }
        
        //add avg line
        if(showAverage)
        {
            if(yTitle.equals("Raw Time"))
            {
               lineModel.addSeries(getAverage("raw", events));

            }
            else if(yTitle.equals("Pax Time"))
            {
                lineModel.addSeries(getAverage("pax", events));
            }
            else
            {
                lineModel.addSeries(getAverage("cones", events));
            }
        }

        if(chartMax < 4)
        {
            chartMax = 4;
        }
        chartMin -= 3;
        if(chartMin < 0)
        {
            chartMin = 0;
        }
        
        Axis yAxis = lineModel.getAxis(AxisType.Y);
        chartMin = Math.floor(chartMin);
        chartMax = Math.ceil(chartMax);
        if((chartMax - chartMin) % tickInterval != 0)
        {
            while((chartMax - chartMin) % tickInterval != 0)
            {
                chartMax++;
            }
        }
        yAxis.setTickInterval(String.valueOf(tickInterval));
        yAxis.setTickCount( ( ( (int)chartMax - (int)chartMin ) / (int)tickInterval ) + 1);
        yAxis.setLabel(yTitle);
        yAxis.setMin(chartMin);
        yAxis.setMax(chartMax+1);
        lineModel.setTitle(chartTitle);
    }
    
    public LineChartSeries getAverage(String type, Set<String> events)
    {
        List<Object[]> averageQuery;
        LineChartSeries avgSeries = new LineChartSeries();
        if(type.equals("raw"))
        {
           avgSeries.setLabel("AVERAGE RAW");
           averageQuery = em.createQuery("SELECT min(r.runTime), r.runEventId.eventId, r.runEventId.eventDate, r.runEventId.eventLocation FROM Runs r where r.runOffcourse = 'N' and r.runEventId.eventId in :event group by r.runDriverName order by r.runEventId.eventDate ASC").setParameter("event", events).getResultList(); 
           Map<String, Object[]> avgMap = new LinkedHashMap();
           for(Object[] o : averageQuery)
           {
                if(avgMap.get(o[3].toString() + " " + chartFormat.format(o[2])) == null)
                {
                    Object[] value = { (double)o[0], 1 };
                    avgMap.put(o[3].toString() + " " + chartFormat.format(o[2]), value);
                }
                else
                {
                    Object[] value = avgMap.get(o[3].toString() + " " + chartFormat.format(o[2]));
                    value[0] = (double)value[0] + (double)o[0];
                    value[1] = (int)value[1] + 1;
                    avgMap.put(o[3].toString() + " " + chartFormat.format(o[2]), value);
                }
           }
           for(String key : avgMap.keySet())
            {
                Object[] value = avgMap.get(key);
                double newAvg = (double)value[0] / (int)value[1];
                if(newAvg > chartMax)
                {
                    chartMax = newAvg;
                }
                if(newAvg < chartMin)
                {
                    chartMin = newAvg;
                }
                avgSeries.set(key, newAvg);
            }
        }
        
        else if(type.equals("pax"))
        {
            avgSeries.setLabel("AVERAGE PAX");
            averageQuery = em.createQuery("SELECT min(r.runPaxTime), r.runEventId.eventId, r.runEventId.eventDate, r.runEventId.eventLocation FROM Runs r where r.runOffcourse = 'N' and r.runEventId.eventId in :event group by r.runDriverName order by r.runEventId.eventDate ASC").setParameter("event", events).getResultList(); 
            Map<String, Object[]> avgMap = new LinkedHashMap();
            for(Object[] o : averageQuery)
            {
                if(avgMap.get(o[3].toString() + " " + chartFormat.format(o[2])) == null)
                {
                    Object[] value = { (double)o[0], 1 };
                    avgMap.put(o[3].toString() + " " + chartFormat.format(o[2]), value);
                }
                else
                {
                    Object[] value = avgMap.get(o[3].toString() + " " + chartFormat.format(o[2]));
                    value[0] = (double)value[0] + (double)o[0];
                    value[1] = (int)value[1] + 1;
                    avgMap.put(o[3].toString() + " " + chartFormat.format(o[2]), value);
                }
            }
            
            for(String key : avgMap.keySet())
            {
                Object[] value = avgMap.get(key);
                double newAvg = (double)value[0] / (int)value[1];
                if(newAvg > chartMax)
                {
                    chartMax = newAvg;
                }
                if(newAvg < chartMin)
                {
                    chartMin = newAvg;
                }
                avgSeries.set(key, newAvg);
            }
        }

        else
        {
            avgSeries.setLabel("AVERAGE CONES");
            averageQuery = em.createQuery("SELECT sum(r.runCones), r.runEventId.eventId, r.runEventId.eventDate, r.runEventId.eventLocation FROM Runs r where r.runEventId.eventId in :event group by r.runDriverName order by r.runEventId.eventDate ASC").setParameter("event", events).getResultList(); 
            Map<String, Object[]> avgMap = new LinkedHashMap();
            for(Object[] o : averageQuery)
            {
                if(avgMap.get(o[3].toString() + " " + chartFormat.format(o[2])) == null)
                {
                    Object[] value = { (long)o[0], 1 };
                    avgMap.put(o[3].toString() + " " + chartFormat.format(o[2]), value);
                }
                else
                {
                    Object[] value = avgMap.get(o[3].toString() + " " + chartFormat.format(o[2]));
                    value[0] = (long)value[0] + (long)o[0];
                    value[1] = (int)value[1] + 1;
                    avgMap.put(o[3].toString() + " " + chartFormat.format(o[2]), value);
                }
            }
            
            for(String key : avgMap.keySet())
            {
                Object[] value = avgMap.get(key);
                double newAvg = new Double(value[0].toString()) / new Double(value[1].toString());
                if(newAvg > chartMax)
                {
                    chartMax = newAvg;
                }
                if(newAvg < chartMin)
                {
                    chartMin = newAvg;
                }
                avgSeries.set(key, newAvg);
            }
        }
        
        return avgSeries;
    }
    
    public void drawComparison()
    {
        if(driver1.equals("") || driver1 == null || driver2.equals("") || driver2 == null || driver1.equals("Driver 1") || driver2.equals("Driver 2"))
        {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Must select 2 drivers."));
        }
        else
        {
            selectedDrivers.add(driver1);
            selectedDrivers.add(driver2);
            lineModel.getSeries().clear();
            
            if(type.equalsIgnoreCase("raw"))
            {
                List<Object[]> query = em.createNamedQuery("Runs.findCommonEventsForDriversRaw", Object[].class).setParameter("startDate", startDate).setParameter("endDate", endDate).setParameter("driverList", selectedDrivers).getResultList();
                this.chartMax = 0;
                this.chartMin = 100;
                drawLineChart(query, "Raw Time Comparison", 3, "Raw Time");
            }
            else if(type.equalsIgnoreCase("pax"))
            {
                List<Object[]> query = em.createNamedQuery("Runs.findCommonEventsForDriversPax", Object[].class).setParameter("startDate", startDate).setParameter("endDate", endDate).setParameter("driverList", selectedDrivers).getResultList();
                this.chartMax = 0;
                this.chartMin = 100;
                drawLineChart(query, "Pax Time Comparison", 3, "Pax Time");
            }
            else
            {
                List<Object[]> query = em.createNamedQuery("Runs.findCommonEventsForDriversCones", Object[].class).setParameter("startDate", startDate).setParameter("endDate", endDate).setParameter("driverList", selectedDrivers).getResultList();
                this.chartMax = 0;
                this.chartMin = 4;
                drawLineChart(query, "Cones Comparison", 2, "Cones");
            }
            drawStatisticsTable();
            selectedDrivers.clear();
        }
    }
    
    public void drawStatisticsTable()
    {
        if(!driver1.equals("") && driver1 != null && !driver2.equals("") && driver2 != null && !driver1.equals("Driver 1") && !driver2.equals("Driver 2"))
        {
            //total events attended over time period
            List<Object[]> query = em.createQuery("SELECT count(r.runDriverName) from Runs r where r.runDriverName = :name AND r.runEventId.eventDate > :begin AND r.runEventId.eventDate < :end and r.runNumber = 1").setParameter("name", driver1).setParameter("begin", startDate).setParameter("end", endDate).getResultList();
            d1TotalEvents = String.valueOf(query.get(0));
            query = em.createQuery("SELECT count(r.runDriverName) from Runs r where r.runDriverName = :name AND r.runEventId.eventDate > :begin AND r.runEventId.eventDate < :end and r.runNumber = 1").setParameter("name", driver2).setParameter("begin", startDate).setParameter("end", endDate).getResultList();
            d2TotalEvents = String.valueOf(query.get(0));
            
            //head to head raw wins
            query = em.createQuery("SELECT min(a.runTime), a.runDriverName, a.runEventId.eventId from Runs a, Runs b where a.runDriverName in :driverList AND b.runDriverName in :driverList AND a.runDriverName != b.runDriverName AND a.runOffcourse = 'N' AND b.runOffcourse = 'N' AND a.runEventId.eventId = b.runEventId.eventId and a.runEventId.eventDate > :begin AND a.runEventId.eventDate < :end GROUP BY a.runEventId.eventId, a.runDriverName ORDER BY a.runEventId.eventId, min(a.runTime)").setParameter("driverList", selectedDrivers).setParameter("begin", startDate).setParameter("end", endDate).getResultList();
            int d1Wins = 0;
            int d2Wins = 0;
            for(int x = 0; x < query.size(); x+=2)
            {
                if(query.get(x)[1].equals(driver1))
                {
                    d1Wins++;
                }
                else if(query.get(x)[1].equals(driver2))
                {
                    d2Wins++;
                }
            }
            d1RawWins = String.valueOf(d1Wins);
            d2RawWins = String.valueOf(d2Wins);
            
            //average raw diff
            int eventsAttended = 0;
            double d1Total = 0;
            double d2Total = 0;
            for(int x = 0; x < query.size(); x+=2)
            {
                Object[] winner = query.get(x);
                Object[] loser = query.get(x+1);
                
                if(winner[1].equals(driver1))
                {
                    d1Total += (double)winner[0] - (double)loser[0];
                    d2Total += (double)loser[0] - (double)winner[0];
                }
                else
                {
                    
                    d1Total += (double)loser[0] - (double)winner[0];
                    d2Total += (double)winner[0] - (double)loser[0];
                }
                eventsAttended++;
            }
            
            d1RawDiff = String.format("%.3f", d1Total /= eventsAttended);
            d2RawDiff = String.format("%.3f", d2Total /= eventsAttended);
            //head to head pax wins
            query = em.createQuery("SELECT min(a.runPaxTime), a.runDriverName, a.runEventId.eventId from Runs a, Runs b where a.runDriverName in :driverList AND b.runDriverName in :driverList AND a.runDriverName != b.runDriverName AND a.runOffcourse = 'N' AND b.runOffcourse = 'N' AND a.runEventId.eventId = b.runEventId.eventId and a.runEventId.eventDate > :begin AND a.runEventId.eventDate < :end GROUP BY a.runEventId.eventId, a.runDriverName ORDER BY a.runEventId.eventId, min(a.runPaxTime)").setParameter("driverList", selectedDrivers).setParameter("begin", startDate).setParameter("end", endDate).getResultList();
            d1Wins = 0;
            d2Wins = 0;
            for(int x = 0; x < query.size(); x+=2)
            {
                if(query.get(x)[1].equals(driver1))
                {
                    d1Wins++;
                }
                else if(query.get(x)[1].equals(driver2))
                {
                    d2Wins++;
                }
            }
            d1PaxWins = String.valueOf(d1Wins);
            d2PaxWins = String.valueOf(d2Wins);
            
            //average pax diff
            eventsAttended = 0;
            d1Total = 0;
            d2Total = 0;
            for(int x = 0; x < query.size(); x+=2)
            {
                Object[] winner = query.get(x);
                Object[] loser = query.get(x+1);
                
                if(winner[1].equals(driver1))
                {
                    d1Total += (double)winner[0] - (double)loser[0];
                    d2Total += (double)loser[0] - (double)winner[0];
                }
                else
                {
                    
                    d1Total += (double)loser[0] - (double)winner[0];
                    d2Total += (double)winner[0] - (double)loser[0];
                }
                eventsAttended++;
            }
            
            d1PaxDiff = String.format("%.3f", d1Total /= eventsAttended);
            d2PaxDiff = String.format("%.3f", d2Total /= eventsAttended);
            //total runs taken
            
        }
    }
    
    
    public LineChartModel getLineModel() {
        return lineModel;
    }

    public void setLineModel(LineChartModel lineModel) {
        this.lineModel = lineModel;
    }
    
    public String getDriver1()
    {
        return this.driver1;
    }
    
    public void setDriver1(String driver1)
    {
        this.driver1 = driver1;
    }

    public String getDriver2() {
        return driver2;
    }

    public void setDriver2(String driver2) {
        this.driver2 = driver2;
    }

    
    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getD1TotalEvents() {
        return d1TotalEvents;
    }

    public void setD1TotalEvents(String d1TotalEvents) {
        this.d1TotalEvents = d1TotalEvents;
    }

    public String getD2TotalEvents() {
        return d2TotalEvents;
    }

    public void setD2TotalEvents(String d2TotalEvents) {
        this.d2TotalEvents = d2TotalEvents;
    }

    public String getD1RawWins() {
        return d1RawWins;
    }

    public void setD1RawWins(String d1RawWins) {
        this.d1RawWins = d1RawWins;
    }

    public String getD2RawWins() {
        return d2RawWins;
    }

    public void setD2RawWins(String d2RawWins) {
        this.d2RawWins = d2RawWins;
    }

    public String getD1RawDiff() {
        return d1RawDiff;
    }

    public void setD1RawDiff(String d1RawDiff) {
        this.d1RawDiff = d1RawDiff;
    }

    public String getD2RawDiff() {
        return d2RawDiff;
    }

    public void setD2RawDiff(String d2RawDiff) {
        this.d2RawDiff = d2RawDiff;
    }

    public String getD1PaxWins() {
        return d1PaxWins;
    }

    public void setD1PaxWins(String d1PaxWins) {
        this.d1PaxWins = d1PaxWins;
    }

    public String getD2PaxWins() {
        return d2PaxWins;
    }

    public void setD2PaxWins(String d2PaxWins) {
        this.d2PaxWins = d2PaxWins;
    }

    public String getD1PaxDiff() {
        return d1PaxDiff;
    }

    public void setD1PaxDiff(String d1PaxDiff) {
        this.d1PaxDiff = d1PaxDiff;
    }

    public String getD2PaxDiff() {
        return d2PaxDiff;
    }

    public void setD2PaxDiff(String d2PaxDiff) {
        this.d2PaxDiff = d2PaxDiff;
    }

    public String getDialogText() {
        return dialogText;
    }

    public void setDialogText(String dialogText) {
        this.dialogText = dialogText;
    }

    public String getDialogHeader() {
        return dialogHeader;
    }

    public void setDialogHeader(String dialogHeader) {
        this.dialogHeader = dialogHeader;
    }

    public String getD1DialogName() {
        return d1DialogName;
    }

    public void setD1DialogName(String d1DialogName) {
        this.d1DialogName = d1DialogName;
    }

    public String getD2DialogName() {
        return d2DialogName;
    }

    public void setD2DialogName(String d2DialogName) {
        this.d2DialogName = d2DialogName;
    }

    public double getD1DialogStat() {
        return d1DialogStat;
    }

    public void setD1DialogStat(double d1DialogStat) {
        this.d1DialogStat = d1DialogStat;
    }

    public double getD2DialogStat() {
        return d2DialogStat;
    }

    public void setD2DialogStat(double d2DialogStat) {
        this.d2DialogStat = d2DialogStat;
    }

    public double getD1DialogDiff() {
        return d1DialogDiff;
    }

    public void setD1DialogDiff(double d1DialogDiff) {
        this.d1DialogDiff = d1DialogDiff;
    }

    public double getD2DialogDiff() {
        return d2DialogDiff;
    }

    public void setD2DialogDiff(double d2DialogDiff) {
        this.d2DialogDiff = d2DialogDiff;
    }

    public double getAvgDialogStat() {
        return avgDialogStat;
    }

    public void setAvgDialogStat(double avgDialogStat) {
        this.avgDialogStat = avgDialogStat;
    }

    public double getAvgDialogDiff1() {
        return avgDialogDiff1;
    }

    public void setAvgDialogDiff1(double avgDialogDiff1) {
        this.avgDialogDiff1 = avgDialogDiff1;
    }

    public double getAvgDialogDiff2() {
        return avgDialogDiff2;
    }

    public void setAvgDialogDiff2(double avgDialogDiff2) {
        this.avgDialogDiff2 = avgDialogDiff2;
    }

    public boolean isShowAverage() {
        return showAverage;
    }

    public void setShowAverage(boolean showAverage) {
        this.showAverage = showAverage;
    }

    
}
