/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package autocrossdb.backing;

import autocrossdb.entities.Events;
import autocrossdb.entities.Runs;
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
    
    public void drawChart()
    {
        if(type.equals("raw"))
        {
            drawRawComparison();
        }
        else if(type.equals("pax"))
        {
            drawPaxComparison();
        }
        else
        {
            drawConesComparison();
        }
    }
    
    public void drawRawComparison()
    {
        if(driver1.equals("") || driver1 == null || driver2.equals("") || driver2 == null)
        {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Must select 2 drivers."));
        }
        else
        {
            double max = 0;
            double min = 100;
            selectedDrivers.add(driver1);
            selectedDrivers.add(driver2);
            lineModel.getSeries().clear();
            for(String selectedDriver : selectedDrivers)
            {
                LineChartSeries series = new LineChartSeries();
                series.setLabel(selectedDriver);
                List<Object[]> writeList = em.createNamedQuery("Runs.findCommonEventsForDriversRaw", Object[].class).setParameter("startDate", startDate).setParameter("endDate", endDate).setParameter("driverList", selectedDrivers).getResultList();
                for(Object[] event : writeList)
                {
                    
                    if(event[2].equals(selectedDriver) && event[3] != null)
                    {
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
                Axis yAxis = lineModel.getAxis(AxisType.Y);
                min = Math.floor(min);
                max = Math.ceil(max);
                yAxis.setTickInterval("3");
                yAxis.setLabel("Raw Time");
                yAxis.setMin(min-1);
                yAxis.setMax(max+1);
                lineModel.setTitle("Raw Time Comparison");
            }
            selectedDrivers.clear();
        }
    }
    
    public void drawPaxComparison()
    {
        if(driver1.equals("") || driver1 == null || driver2.equals("") || driver2 == null)
        {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Must select 2 drivers."));
        }
        else
        {
            double max = 0;
            double min = 100;
            selectedDrivers.add(driver1);
            selectedDrivers.add(driver2);
            lineModel.getSeries().clear();
            for(String selectedDriver : selectedDrivers)
            {
                LineChartSeries series = new LineChartSeries();
                series.setLabel(selectedDriver);
                List<Object[]> writeList = em.createNamedQuery("Runs.findCommonEventsForDriversPax", Object[].class).setParameter("startDate", startDate).setParameter("endDate", endDate).setParameter("driverList", selectedDrivers).getResultList();
                for(Object[] event : writeList)
                {
                    if(event[2].equals(selectedDriver))
                    {
                        if(event[3] != null)
                        {
                            if((double)event[3] > max)
                            {
                                max = (double)event[3];
                            }
                            if((double)event[3] < min)
                            {
                                min = (double)event[3];
                            }
                            series.set(event[1] + " " + chartFormat.format(event[0]), (Number)event[3]);
                        }
                    }
                }
                lineModel.addSeries(series);
                Axis yAxis = lineModel.getAxis(AxisType.Y);
                min = Math.floor(min);
                max = Math.ceil(max);
                yAxis.setTickInterval("3");
                yAxis.setLabel("PAX Time");
                yAxis.setMin(min-1);
                yAxis.setMax(max+1);
                lineModel.setTitle("Pax Time Comparison");
                
            }
            selectedDrivers.clear();
            
        }
    }
    
    public void drawConesComparison()
    {
        if(driver1.equals("") || driver1 == null || driver2.equals("") || driver2 == null)
        {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Must select 2 drivers."));
        }
        else
        {
            long max = 1;
            selectedDrivers.add(driver1);
            selectedDrivers.add(driver2);
            lineModel.getSeries().clear();
            for(String selectedDriver : selectedDrivers)
            {
                LineChartSeries series = new LineChartSeries();
                series.setLabel(selectedDriver);
                List<Object[]> writeList = em.createNamedQuery("Runs.findCommonEventsForDriversCones", Object[].class).setParameter("startDate", startDate).setParameter("endDate", endDate).setParameter("driverList", selectedDrivers).getResultList();
                for(Object[] event : writeList)
                {
                    if(event[2].equals(selectedDriver))
                    {
                        series.set(event[1] + " " + chartFormat.format(event[0]), (long)event[3]);
                        if((long)event[3] > max)
                        {
                            max = (long)event[3];
                        }
                    }
                    
                }
                if(max < 4)
                {
                    max = 4;
                }
                else if(max % 4 != 0)
                {
                    max += 4 - (max % 4);
                }
                lineModel.addSeries(series);
                Axis yAxis = lineModel.getAxis(AxisType.Y);
                yAxis.setTickInterval("2");
                yAxis.setLabel("Cones");
                yAxis.setMin(0);
                yAxis.setMax(max);
                lineModel.setTitle("Cones Comparison");
            }
            selectedDrivers.clear();
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

    
    
}
