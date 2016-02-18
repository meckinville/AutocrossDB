/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package autocrossdb.backing;

import autocrossdb.entities.Events;
import autocrossdb.entities.Runs;
import autocrossdb.component.ChartStatistic;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.primefaces.model.chart.Axis;
import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.CategoryAxis;
import org.primefaces.model.chart.ChartSeries;
import org.primefaces.model.chart.LineChartModel;
import org.primefaces.model.chart.LineChartSeries;

/**
 *
 * @author rmcconville
 */
@ManagedBean(name="comparisonChart")
@ViewScoped
public class ComparisonChartBean implements Serializable
{
    private String driver1;
    private String driver2;
    private String type;
    private Date startDate;
    private Date endDate;
    private LineChartModel lineModel;
    private DateFormat chartFormat;
    private List<String> selectedDrivers;
    private List<ChartStatistic> statistics;

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
        lineModel.getAxes().put(AxisType.X, new CategoryAxis("Events"));
        driver1 = "Driver 1";
        driver2 = "Driver 2";
    }

    public List<String> completeDriverText(String query)
    {
        List<String> results = new ArrayList<String>();
        List<String> driverList = em.createQuery("SELECT distinct(r.runDriverName) FROM Runs r ", String.class).getResultList();
        for(int x = 0; x < driverList.size(); x++)
        {
            if(driverList.get(x).startsWith(query.toUpperCase()))
            {
                results.add(driverList.get(x));
            }
        }
        return results;
    }
    
    public void drawLineChart(List<Object[]> query, String chartTitle, double min, double max, int tickInterval, String yTitle)
    {
        for(String selectedDriver : selectedDrivers)
        {
            LineChartSeries series = new LineChartSeries();
            series.setLabel(selectedDriver);
            for(Object[] event : query)
            {
                if(event[2].equals(selectedDriver) && event[3] != null)
                {
                    event[3] = Double.valueOf(event[3].toString());
                    if((double)event[3] > max)
                    {
                        max = (double)event[3];
                    }
                    if((double)event[3] < min)
                    {
                        min = (double)event[3];
                    }
                    series.set(event[1] + " " + chartFormat.format(event[0]), (double)event[3]);
                }
            }
            lineModel.addSeries(series);            
        }
        
        if(max < 4)
        {
            max = 4;
        }
        min -= 3;
        if(min < 0)
        {
            min = 0;
        }
        
        Axis yAxis = lineModel.getAxis(AxisType.Y);
        min = Math.floor(min);
        max = Math.ceil(max);
        if((max - min) % tickInterval != 0)
        {
            while((max - min) % tickInterval != 0)
            {
                max++;
            }
        }
        yAxis.setTickInterval(String.valueOf(tickInterval));
        yAxis.setTickCount( ( ( (int)max - (int)min ) / (int)tickInterval ) + 1);
        yAxis.setLabel(yTitle);
        yAxis.setMin(min);
        yAxis.setMax(max+1);
        lineModel.setTitle(chartTitle);
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
            
            if(type.equals("raw"))
            {
                List<Object[]> query = em.createNamedQuery("Runs.findCommonEventsForDriversRaw", Object[].class).setParameter("startDate", startDate).setParameter("endDate", endDate).setParameter("driverList", selectedDrivers).getResultList();
                drawLineChart(query, "Raw Time Comparison", 100, 0, 3, "Raw Time");
            }
            else if(type.equals("pax"))
            {
                List<Object[]> query = em.createNamedQuery("Runs.findCommonEventsForDriversPax", Object[].class).setParameter("startDate", startDate).setParameter("endDate", endDate).setParameter("driverList", selectedDrivers).getResultList();
                drawLineChart(query, "Pax Time Comparison", 100, 0, 3, "Pax Time");
            }
            else
            {
                List<Object[]> query = em.createNamedQuery("Runs.findCommonEventsForDriversCones", Object[].class).setParameter("startDate", startDate).setParameter("endDate", endDate).setParameter("driverList", selectedDrivers).getResultList();
                drawLineChart(query, "Cones Comparison", 0, 4, 2, "Cones");
            }
            drawStatisticsTable();
            selectedDrivers.clear();
        }
    }
    
    public void drawStatisticsTable()
    {
        if(!driver1.equals("") && driver1 != null && !driver2.equals("") && driver2 != null && !driver1.equals("Driver 1") && !driver2.equals("Driver 2"))
        {
            statistics = new ArrayList<ChartStatistic>();
            String v1;
            String v2;           
            
            //total events attended over time period
            List<Object[]> query = em.createQuery("SELECT count(r.runDriverName) from Runs r where r.runDriverName = :name AND r.runEventUrl.eventDate > :begin AND r.runEventUrl.eventDate < :end and r.runNumber = 1").setParameter("name", driver1).setParameter("begin", startDate).setParameter("end", endDate).getResultList();
            v1 = String.valueOf(query.get(0));
            query = em.createQuery("SELECT count(r.runDriverName) from Runs r where r.runDriverName = :name AND r.runEventUrl.eventDate > :begin AND r.runEventUrl.eventDate < :end and r.runNumber = 1").setParameter("name", driver2).setParameter("begin", startDate).setParameter("end", endDate).getResultList();
            v2 = String.valueOf(query.get(0));
            statistics.add(new ChartStatistic("Total Events Attended in this Time",v1,v2));
            
            //head to head raw wins
            query = em.createQuery("SELECT min(a.runTime), a.runDriverName, a.runEventUrl.eventUrl from Runs a, Runs b where a.runDriverName in :driverList AND b.runDriverName in :driverList AND a.runDriverName != b.runDriverName AND a.runOffcourse = 'N' AND b.runOffcourse = 'N' AND a.runEventUrl.eventUrl = b.runEventUrl.eventUrl and a.runEventUrl.eventDate > :begin AND a.runEventUrl.eventDate < :end GROUP BY a.runEventUrl.eventUrl, a.runDriverName ORDER BY a.runEventUrl.eventUrl, min(a.runTime)").setParameter("driverList", selectedDrivers).setParameter("begin", startDate).setParameter("end", endDate).getResultList();
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
            v1 = String.valueOf(d1Wins);
            v2 = String.valueOf(d2Wins);
            statistics.add(new ChartStatistic("Head to Head Raw Wins",v1,v2));
            
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
            
            statistics.add(new ChartStatistic("Average Raw Differential", String.format("%.3f", d1Total /= eventsAttended), String.format("%.3f", d2Total /= eventsAttended)));
            
            //head to head pax wins
            query = em.createQuery("SELECT min(a.runPaxTime), a.runDriverName, a.runEventUrl.eventUrl from Runs a, Runs b where a.runDriverName in :driverList AND b.runDriverName in :driverList AND a.runDriverName != b.runDriverName AND a.runOffcourse = 'N' AND b.runOffcourse = 'N' AND a.runEventUrl.eventUrl = b.runEventUrl.eventUrl and a.runEventUrl.eventDate > :begin AND a.runEventUrl.eventDate < :end GROUP BY a.runEventUrl.eventUrl, a.runDriverName ORDER BY a.runEventUrl.eventUrl, min(a.runPaxTime)").setParameter("driverList", selectedDrivers).setParameter("begin", startDate).setParameter("end", endDate).getResultList();
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
            v1 = String.valueOf(d1Wins);
            v2 = String.valueOf(d2Wins);
            statistics.add(new ChartStatistic("Head to Head Pax Wins",v1,v2));
            
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
            
            statistics.add(new ChartStatistic("Average Pax Differential", String.format("%.3f", d1Total /= eventsAttended), String.format("%.3f", d2Total /= eventsAttended)));
            
            //total runs taken
            
            //events attended together
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

    public List<ChartStatistic> getStatistics() {
        return statistics;
    }

    public void setStatistics(List<ChartStatistic> statistics) {
        this.statistics = statistics;
    }

    
    
}
