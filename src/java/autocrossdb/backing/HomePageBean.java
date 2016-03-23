/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package autocrossdb.backing;

import autocrossdb.entities.UpcomingEvents;
import java.util.ArrayList;
import java.util.Calendar;
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
@ManagedBean(name="homePage")
@ViewScoped
public class HomePageBean 
{
    private List<String> images;
    private List<UpcomingEvents> upcomingEvents;
    
    private long eventCount;
    private long driverCount;
    private long clubCount;
    private long runCount;
    
    @PersistenceContext
    private EntityManager em;
    
    @PostConstruct
    public void init()
    {
        /*
        images = new ArrayList<String>();
        for(int x = 1; x <= 9; x++)
        {
            images.add(x + ".jpg");
        }
        */
        upcomingEvents = em.createQuery("SELECT ue from UpcomingEvents ue where ue.upcomingDate > :today order by ue.upcomingDate asc").setParameter("today", Calendar.getInstance().getTime()).getResultList();
        if(upcomingEvents.size() > 10)
        {
            upcomingEvents = upcomingEvents.subList(0,8);
        }
        
        
        eventCount = (long)em.createQuery("SELECT count(e) from Events e").getResultList().get(0);
        runCount = (long)em.createQuery("SELECT count(r) from Runs r").getResultList().get(0);
        driverCount = (long)em.createQuery("SELECT count(distinct r.runDriverName) from Runs r").getResultList().get(0);
        clubCount = (long)em.createQuery("SELECT count(distinct r.runEventUrl.eventClubName) from Runs r").getResultList().get(0);
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public long getEventCount() {
        return eventCount;
    }

    public void setEventCount(long eventCount) {
        this.eventCount = eventCount;
    }

    public long getDriverCount() {
        return driverCount;
    }

    public void setDriverCount(long driverCount) {
        this.driverCount = driverCount;
    }

    public long getClubCount() {
        return clubCount;
    }

    public void setClubCount(long clubCount) {
        this.clubCount = clubCount;
    }

    public long getRunCount() {
        return runCount;
    }

    public void setRunCount(long runCount) {
        this.runCount = runCount;
    }

    public List<UpcomingEvents> getUpcomingEvents() {
        return upcomingEvents;
    }

    public void setUpcomingEvents(List<UpcomingEvents> upcomingEvents) {
        this.upcomingEvents = upcomingEvents;
    }

    
    
}
