/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package autocrossdb.backing;

import autocrossdb.component.DriverStat;
import autocrossdb.entities.Events;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author rmcconville
 */

@ManagedBean(name="statistics")
@ViewScoped
public class StatisticsBean
{
    List<DriverStat> drivers;
    List<DriverStat> backupDrivers;
    
    private Date endDate;
    private Date startDate;
    
    private boolean eventFilter;
    private String eventFilterExpression;
    private int eventFilterAmount;
    
    private boolean rawFilter;
    private String rawFilterExpression;
    private int rawFilterAmount;
    
    private boolean paxFilter;
    private String paxFilterExpression;
    private int paxFilterAmount;
    
    private boolean conesFilter;
    private String conesFilterExpression;
    private int conesFilterAmount;
    
    private boolean filter = false;
    
    @PersistenceContext
    EntityManager em;
    
    @PostConstruct
    public void init()
    {
        Calendar now = Calendar.getInstance();
        endDate = now.getTime();
        now.set(Calendar.MONTH, now.get(Calendar.MONTH)-8);
        startDate = now.getTime();
    }
    
    public void getStatistics()
    {
        List<Events> eventList = em.createQuery("SELECT e FROM Events e where e.eventDate > :startDate AND e.eventDate < :endDate ORDER BY e.eventDate asc").setParameter("endDate", endDate).setParameter("startDate", startDate).getResultList();
        
        LinkedHashMap<String, DriverStat> driverMap = new LinkedHashMap();
        for(Events e : eventList)
        {
            List<Object[]> rawList = em.createQuery("SELECT min(r.runTime), r.runDriverName From Runs r where r.runEventUrl = :event and r.runOffcourse = 'N' and r.runClassName.className != 'NS' group by r.runDriverName order by min(r.runTime) asc").setParameter("event", e).getResultList();
            List<Object[]> paxList = em.createQuery("SELECT min(r.runPaxTime), r.runDriverName From Runs r where r.runEventUrl = :event and r.runOffcourse = 'N' and r.runClassName.className != 'NS' group by r.runDriverName order by min(r.runPaxTime) asc").setParameter("event", e).getResultList();
            List<Object[]> conesList = em.createQuery("SELECT sum(r.runCones), r.runDriverName From Runs r where r.runEventUrl = :event and r.runClassName.className != 'NS' group by r.runDriverName order by sum(r.runCones) desc").setParameter("event", e).getResultList();
            
            //we iterate through all the runs for an event.
            //we have a hashmap that uses driver name as key.
            //the value of the hashmap is a DriverStat object which contains
            //a running value of the raw/pax/class percentiles. once we have
            //iterated through all the runs we will divide the running percentile
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
        }
        
        drivers = new ArrayList();
        backupDrivers = new ArrayList();
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
            
            tempStat.setAverageCones(tempStat.getRunningCones() / tempStat.getEventsAttended());
            drivers.add(tempStat);
            backupDrivers.add(tempStat);
        } 
   }
    
    public void filterList()
    {
        List<DriverStat> tempList = new ArrayList();
        for(DriverStat d : drivers)
        {         
            if(eventFilter)
            {
                if(eventFilterExpression.equals("lt"))
                {
                    if(d.getEventsAttended() < eventFilterAmount)
                    {
                        tempList.add(d);
                    }
                }
                else
                {
                    if(d.getEventsAttended() > eventFilterAmount)
                    {
                        tempList.add(d);
                    }
                }
            }
            
            if(rawFilter)
            {
                if(rawFilterExpression.equals("lt"))
                {
                    if(d.getRawPercentile() < rawFilterAmount)
                    {
                        tempList.add(d);
                    }
                }
                else
                {
                    if(d.getRawPercentile() > rawFilterAmount)
                    {
                        tempList.add(d);
                    }
                }
            }
            
            if(paxFilter)
            {
                if(paxFilterExpression.equals("lt"))
                {
                    if(d.getPaxPercentile() < paxFilterAmount)
                    {
                        tempList.add(d);
                    }
                }
                else
                {
                    if(d.getPaxPercentile() > paxFilterAmount)
                    {
                        tempList.add(d);
                    }
                }
            }
            
            if(conesFilter)
            {
                if(conesFilterExpression.equals("lt"))
                {
                    if(d.getAverageCones() < conesFilterAmount)
                    {
                        tempList.add(d);
                    }
                }
                else
                {
                    if(d.getAverageCones() > conesFilterAmount)
                    {
                        tempList.add(d);
                    }
                }
            }
        }
        
        drivers = tempList;
    }
    
    public void unfilterList()
    {
        drivers = backupDrivers;
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

    public String getEventFilterExpression() {
        return eventFilterExpression;
    }

    public void setEventFilterExpression(String eventFilterExpression) {
        this.eventFilterExpression = eventFilterExpression;
    }

    public int getEventFilterAmount() {
        return eventFilterAmount;
    }

    public void setEventFilterAmount(int eventFilterAmount) {
        this.eventFilterAmount = eventFilterAmount;
    }

    public boolean isEventFilter() {
        return eventFilter;
    }

    public void setEventFilter(boolean eventFilter) {
        this.eventFilter = eventFilter;
    }

    public boolean isRawFilter() {
        return rawFilter;
    }

    public void setRawFilter(boolean rawFilter) {
        this.rawFilter = rawFilter;
    }

    public String getRawFilterExpression() {
        return rawFilterExpression;
    }

    public void setRawFilterExpression(String rawFilterExpression) {
        this.rawFilterExpression = rawFilterExpression;
    }

    public int getRawFilterAmount() {
        return rawFilterAmount;
    }

    public void setRawFilterAmount(int rawFilterAmount) {
        this.rawFilterAmount = rawFilterAmount;
    }

    public boolean isPaxFilter() {
        return paxFilter;
    }

    public void setPaxFilter(boolean paxFilter) {
        this.paxFilter = paxFilter;
    }

    public String getPaxFilterExpression() {
        return paxFilterExpression;
    }

    public void setPaxFilterExpression(String paxFilterExpression) {
        this.paxFilterExpression = paxFilterExpression;
    }

    public int getPaxFilterAmount() {
        return paxFilterAmount;
    }

    public void setPaxFilterAmount(int paxFilterAmount) {
        this.paxFilterAmount = paxFilterAmount;
    }

    public boolean isConesFilter() {
        return conesFilter;
    }

    public void setConesFilter(boolean conesFilter) {
        this.conesFilter = conesFilter;
    }

    public String getConesFilterExpression() {
        return conesFilterExpression;
    }

    public void setConesFilterExpression(String conesFilterExpression) {
        this.conesFilterExpression = conesFilterExpression;
    }

    public int getConesFilterAmount() {
        return conesFilterAmount;
    }

    public void setConesFilterAmount(int conesFilterAmount) {
        this.conesFilterAmount = conesFilterAmount;
    }

    public boolean isFilter() {
        return filter;
    }

    public void setFilter(boolean filter) {
        this.filter = filter;
    }
    
    
}
