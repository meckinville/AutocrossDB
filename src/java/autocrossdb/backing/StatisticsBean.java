/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package autocrossdb.backing;

import autocrossdb.component.DriverStat;
import autocrossdb.component.Theme;
import autocrossdb.entities.Events;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author rmcconville
 */

@ManagedBean(name="statistics")
@SessionScoped
public class StatisticsBean implements Serializable
{
    private List<Theme> themes;
    
    @ManagedProperty(value="#{themeService}")
    private ThemeServiceBean themeService;
    
    List<DriverStat> drivers;
    List<DriverStat> filteredDrivers;

    private Date endDate;
    private Date startDate;
    
    private double progress;
    private double progressIncrement;

    @PersistenceContext
    EntityManager em;
    
    @PostConstruct
    public void init()
    {
        themes = themeService.getThemes();
        Calendar now = Calendar.getInstance();
        endDate = now.getTime();
        now.set(Calendar.MONTH, now.get(Calendar.MONTH)-8);
        startDate = now.getTime();
        progress = 0;
    }
    
    public void getStatistics()
    {
        List<Events> eventList = em.createQuery("SELECT e FROM Events e where e.eventDate > :startDate AND e.eventDate < :endDate ORDER BY e.eventDate asc").setParameter("endDate", endDate).setParameter("startDate", startDate).getResultList();
        
        LinkedHashMap<String, DriverStat> driverMap = new LinkedHashMap();
        progressIncrement = 50.0 / (double)eventList.size();
        for(Events e : eventList)
        {
            List<Object[]> rawList = em.createQuery("SELECT min(r.runTime), r.runDriverName From Runs r where r.runEventId = :event and r.runOffcourse = 'N' and r.runClassName.className != 'NS' group by r.runDriverName order by min(r.runTime) asc").setParameter("event", e).getResultList();
            List<Object[]> paxList = em.createQuery("SELECT min(r.runPaxTime), r.runDriverName From Runs r where r.runEventId = :event and r.runOffcourse = 'N' and r.runClassName.className != 'NS' group by r.runDriverName order by min(r.runPaxTime) asc").setParameter("event", e).getResultList();
            List<Object[]> conesList = em.createQuery("SELECT sum(r.runCones), r.runDriverName From Runs r where r.runEventId = :event and r.runClassName.className != 'NS' group by r.runDriverName order by sum(r.runCones) desc").setParameter("event", e).getResultList();
            
            //we iterate through all the runs for an event.
            //we have a hashmap that uses driver name as key.
            //the value of the hashmap is a DriverStat object which contains
            //a running value of the raw/pax/class percentiles. once we have
            //iterated through all the runs we will divide the running percentiles
            //by the number of events.
            for(int x = 0; x < rawList.size(); x++)
            {
                DriverStat tempStat = driverMap.get(rawList.get(x)[1].toString());
                
                if(tempStat == null)
                {
                    tempStat = new DriverStat(rawList.get(x)[1].toString());
                    if(x == 0)
                    {
                        tempStat.setRunningRawPercentile(tempStat.getRunningRawPercentile() + 100);
                        tempStat.setEventsAttended(tempStat.getEventsAttended() + 1);
                    }
                    else
                    {
                        tempStat.setRunningRawPercentile(tempStat.getRunningRawPercentile() + (100 - (new Double(x+1) / new Double(rawList.size()) * 100)));
                        tempStat.setEventsAttended(tempStat.getEventsAttended() + 1);
                    }
                    driverMap.put(rawList.get(x)[1].toString(), tempStat);
                }
                else
                {
                    if(x == 0)
                    {
                        tempStat.setRunningRawPercentile(tempStat.getRunningRawPercentile() + 100);
                        tempStat.setEventsAttended(tempStat.getEventsAttended() + 1);
                    }
                    else
                    {
                        tempStat.setRunningRawPercentile(tempStat.getRunningRawPercentile() + (100 - (new Double(x+1) / new Double(rawList.size()) * 100)));
                        tempStat.setEventsAttended(tempStat.getEventsAttended() + 1);
                    }
                }
            }
            
            for(int x = 0; x < paxList.size(); x++)
            {
                DriverStat tempStat = driverMap.get(paxList.get(x)[1].toString());
                
                if(x == 0)
                {
                    tempStat.setRunningPaxPercentile(tempStat.getRunningPaxPercentile() + 100);
                }
                else
                {
                    tempStat.setRunningPaxPercentile(tempStat.getRunningPaxPercentile() + (100 - (new Double(x+1) / new Double(paxList.size()) * 100)));
                }
            }
            
            for(int x = 0; x < conesList.size(); x++)
            {
                DriverStat tempStat = driverMap.get(conesList.get(x)[1].toString());
                
                //if the driver doesnt exist in the map yet it's because they were off course
                //for every single run in the runsList and paxList.
                if(tempStat == null)
                {
                    tempStat = new DriverStat(conesList.get(x)[1].toString());
                    tempStat.setRunningCones(tempStat.getRunningCones() + (long)conesList.get(x)[0]);
                }
                else
                {
                    tempStat.setRunningCones(tempStat.getRunningCones() + (long)conesList.get(x)[0]);
                }
                
            }
            
            progress += progressIncrement;
        }
        
        progressIncrement = 50.0 / (double)driverMap.keySet().size();
        drivers = new ArrayList();
        for(String key : driverMap.keySet())
        {
            DriverStat tempStat = driverMap.get(key);
            if(tempStat.getRunningRawPercentile() == 0)
            {
                tempStat.setRawPercentile(0);
            }
            else
            {
                tempStat.setRawPercentile(tempStat.getRunningRawPercentile() / tempStat.getEventsAttended());
            }
            
            if(tempStat.getRunningPaxPercentile() == 0)
            {
                tempStat.setPaxPercentile(0);
            }
            else
            {
                tempStat.setPaxPercentile(tempStat.getRunningPaxPercentile() / tempStat.getEventsAttended());
            }
            
            tempStat.setAverageCones(tempStat.getRunningCones() / new Double(tempStat.getEventsAttended()));
            drivers.add(tempStat);
            progress += progressIncrement;
        } 
        progress = 100;
   }
    
    public boolean filterTable(Object value, Object filter, Locale locale)
    {
        if(filter.toString().length() == 0)
        {
            return true;
        }
        int compared = 0;
        
        if(value instanceof Double)
        {
            compared = ((Comparable)value).compareTo(new Double(filter.toString()));
        }
        else if(value instanceof Integer)
        {
            compared = ((Comparable)value).compareTo(new Integer(filter.toString()));
        }
        else if(value instanceof String)
        {
            return value.toString().contains(filter.toString().toUpperCase());
        }
        return (compared >= 0);
    }
    
    public void onComplete()
    {
        progress = 0;
    }

    public List<DriverStat> getDrivers() {
        return drivers;
    }

    public void setDrivers(List<DriverStat> drivers) {
        this.drivers = drivers;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public List<DriverStat> getFilteredDrivers() {
        return filteredDrivers;
    }

    public void setFilteredDrivers(List<DriverStat> filteredDrivers) {
        this.filteredDrivers = filteredDrivers;
    }

    public double getProgress() {
        return (progress > 100 ? 100 : progress);
    }

    public void setProgress(double progress) {
        this.progress = progress;
    }

    public List<Theme> getThemes() {
        return themes;
    }

    public void setThemes(List<Theme> themes) {
        this.themes = themes;
    }

    public void setThemeService(ThemeServiceBean service)
    {
        this.themeService = service;
    }
    
}
