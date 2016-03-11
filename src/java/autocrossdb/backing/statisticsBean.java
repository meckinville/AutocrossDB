/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package autocrossdb.backing;

import autocrossdb.component.DriverStat;
import autocrossdb.entities.Events;
import autocrossdb.entities.Runs;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author rmcconville
 */

@ManagedBean(name="statistics")
@ApplicationScoped
public class StatisticsBean 
{
    List<DriverStat> drivers;
    
    private Date endDate;
    private Date startDate;
    
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
        List<Events> eventList = em.createQuery("SELECT e FROM Events e where e.eventDate > :startDate AND e.eventDate < :endDate ORDER BY e.eventDate asc").setParameter("endDate", endDate.getTime()).setParameter("startDate", startDate.getTime()).getResultList();
        
        LinkedHashMap<String, DriverStat> driverMap = new LinkedHashMap();
        for(Events e : eventList)
        {
            List<Runs> runsList = em.createQuery("SELECT r From Runs r where r.eventUrl = :event").setParameter("event", e).getResultList();
            
            for(Runs r : runsList)
            {
                
            }
        }
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
    
    
}
