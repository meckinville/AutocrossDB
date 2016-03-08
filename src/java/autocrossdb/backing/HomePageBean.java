/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package autocrossdb.backing;

import java.util.ArrayList;
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
@ManagedBean(name="homePage")
@ApplicationScoped
public class HomePageBean 
{
    private List<String> images;
    
    private long eventCount;
    private long driverCount;
    private long clubCount;
    private long runCount;
    
    @PersistenceContext
    private EntityManager em;
    
    @PostConstruct
    public void init()
    {
        images = new ArrayList<String>();
        for(int x = 1; x <= 9; x++)
        {
            images.add(x + ".jpg");
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

    
    
}
