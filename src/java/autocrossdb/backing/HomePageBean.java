/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package autocrossdb.backing;

import autocrossdb.component.UpcomingEventMap;
import autocrossdb.entities.UpcomingEvents;
import java.io.Serializable;
import java.util.Calendar;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.primefaces.model.map.DefaultMapModel;
import org.primefaces.model.map.LatLng;
import org.primefaces.model.map.MapModel;
import org.primefaces.model.map.Polygon;

/**
 *
 * @author rmcconville
 */
@ManagedBean(name="homePage")
@SessionScoped
public class HomePageBean implements Serializable
{
    private List<String> images;
    private List<UpcomingEvents> upcomingEvents;
    private UpcomingEvents selectedUpcomingEvent;
    private UpcomingEventMap map;
    
    
    private long eventCount;
    private long driverCount;
    private long clubCount;
    private long runCount;
    
    @PersistenceContext
    private EntityManager em;
    
    @PostConstruct
    public void init()
    {
        map = new UpcomingEventMap();
        upcomingEvents = em.createQuery("SELECT ue from UpcomingEvents ue where ue.upcomingDate > :today order by ue.upcomingDate asc").setParameter("today", Calendar.getInstance().getTime()).getResultList();    
        eventCount = (long)em.createQuery("SELECT count(e) from Events e").getResultList().get(0);
        runCount = (long)em.createQuery("SELECT count(r) from Runs r").getResultList().get(0);
        driverCount = (long)em.createQuery("SELECT count(distinct r.runDriverName) from Runs r").getResultList().get(0);
        clubCount = (long)em.createQuery("SELECT count(distinct r.runEventUrl.eventClubName) from Runs r").getResultList().get(0);
    }
    
    public String getClubLink(UpcomingEvents e)
    {
        if(e.getUpcomingClub().equals("MARTINSCC"))
        {
            return "http://www.martinscc.org";
        }
        else if(e.getUpcomingClub().equals("CFRSCCA"))
        {
            return "http://www.cfrsolo2.com";
        }
        else if(e.getUpcomingClub().equals("GCAC"))
        {
            return "http://www.gulfcoastautocrossers.com";
        }
        else if(e.getUpcomingClub().equals("FAST"))
        {
            return "http://www.drivefast.org";
        }
        else
        {
            return "";
        }
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

    public UpcomingEvents getSelectedUpcomingEvent() {
        return selectedUpcomingEvent;
    }

    public void setSelectedUpcomingEvent(UpcomingEvents selectedUpcomingEvent) 
    {
        this.selectedUpcomingEvent = selectedUpcomingEvent;
        map = new UpcomingEventMap(selectedUpcomingEvent);
    }

    public UpcomingEventMap getMap() {
        return map;
    }

    public void setMap(UpcomingEventMap map) {
        this.map = map;
    }
    
    
}
