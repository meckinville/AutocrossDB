/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package autocrossdb.backing;

import autocrossdb.component.Trophy;
import autocrossdb.entities.Classes;
import autocrossdb.entities.Events;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author rmcconville
 */
@ManagedBean(name="trophyRoom")
@SessionScoped
public class TrophyRoomBean implements Serializable
{
    @PersistenceContext
    private EntityManager em;
    
    private String driver;
    
    private List<Events> eventList;
    private List<Trophy> rawTrophies;
    private List<Trophy> paxTrophies;
    private List<Trophy> noviceTrophies;
    private List<Trophy> classTrophies;
    private List<Trophy> awardTrophies;
    
    private long progress;
    
    @PostConstruct
    public void init()
    {
        
    }
    
    public void populateTrophies()
    {
        eventList = em.createQuery("select e from Events e join e.runsCollection r where r.runDriverName = :driverName group by r.runDriverName, r.runEventUrl having count(r.runDriverName) >= 1 ").setParameter("driverName", driver).getResultList();
        populateRawTrophies();
        progress = 25;
        populatePaxTrophies();
        progress = 50;
        populateNoviceTrophies();
        progress = 75;
        populateClassTrophies();
        progress = 100;
        
        for(Trophy t : rawTrophies)
        {
            System.out.println(t.toString());
        }
        
        for(Trophy t : paxTrophies)
        {
            System.out.println(t.toString());
        }
        
        for(Trophy t : noviceTrophies)
        {
            System.out.println(t.toString());
        }
        
        for(Trophy t : classTrophies)
        {
            System.out.println(t.toString());
        }
        
    }
    
    public void populateRawTrophies()
    {
        rawTrophies = new ArrayList();
        
        for(Events e : eventList)
        {
            List<Object[]> driverList = em.createQuery("select min(r.runTime), r.runDriverName, r.runClassName from Runs r where r.runEventUrl.eventUrl = :eventUrl and r.runOffcourse = 'N' group by r.runDriverName order by min(r.runTime)").setParameter("eventUrl", e.getEventUrl()).getResultList();
            for(int x = 0; x < 3; x++)
            {
                if(driverList.get(x)[1].equals(driver))
                {
                    rawTrophies.add(new Trophy(x, "raw", e, (Classes)driverList.get(x)[2]));
                    break;
                }
            }
        }
    }
    
    public void populatePaxTrophies()
    {
        paxTrophies = new ArrayList();
        
        for(Events e : eventList)
        {
            List<Object[]> driverList = em.createQuery("select min(r.runPaxTime), r.runDriverName, r.runClassName from Runs r where r.runEventUrl.eventUrl = :eventUrl and r.runOffcourse = 'N' group by r.runDriverName order by min(r.runPaxTime)").setParameter("eventUrl", e.getEventUrl()).getResultList();
            for(int x = 0; x < 3; x++)
            {
                if(driverList.get(x)[1].equals(driver))
                {
                    paxTrophies.add(new Trophy(x, "pax", e, (Classes)driverList.get(x)[2]));
                    break;
                }
            }
        }
    }
    
    public void populateNoviceTrophies()
    {
        noviceTrophies = new ArrayList();
        
        for(Events e : eventList)
        {
            List<Object[]> driverList = em.createQuery("select min(r.runTime), r.runDriverName, r.runClassName from Runs r where r.runEventUrl.eventUrl = :eventUrl and r.runOffcourse = 'N' and r.runClassName.className = 'NS' group by r.runDriverName order by min(r.runTime)").setParameter("eventUrl", e.getEventUrl()).getResultList();
            
            int size = (driverList.size() >= 3) ? 3 : driverList.size();
            for(int x = 0; x < size; x++)
            {
                if(driverList.get(x)[1].equals(driver))
                {
                    noviceTrophies.add(new Trophy(x, "novice", e, (Classes)driverList.get(x)[2]));
                    break;
                }
            }
        }
    }
    
    public void populateClassTrophies()
    {
        classTrophies = new ArrayList();
        
        for(Events e : eventList)
        {
            String driverClass = em.createQuery("select r.runClassName.className from Runs r where r.runDriverName = :driverName and r.runEventUrl.eventUrl = :eventUrl and r.runNumber = 1").setParameter("driverName", driver).setParameter("eventUrl", e.getEventUrl()).getResultList().get(0).toString();
            List<Object[]> driverList = em.createQuery("select min(r.runTime), r.runDriverName, r.runClassName from Runs r where r.runEventUrl.eventUrl = :eventUrl and r.runOffcourse = 'N' and r.runClassName.className = :className group by r.runDriverName order by min(r.runTime)").setParameter("eventUrl", e.getEventUrl()).setParameter("className", driverClass).getResultList();
            int size = (driverList.size() >= 3) ? 3 : driverList.size();
            for(int x = 0; x < size; x++)
            {
                if(driverList.get(x)[1].equals(driver))
                {
                    classTrophies.add(new Trophy(x, "class", e, (Classes)driverList.get(x)[2]));
                    break;
                }
            }
        }
    }
    
    public void populateAwardTrophies()
    {
        
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
    
    public void onComplete()
    {
        progress = 0;
    }

    public String getDriver() {
        return driver;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }

    public long getProgress() {
        return progress;
    }

    public void setProgress(long progress) {
        this.progress = progress;
    }

    public List<Trophy> getRawTrophies() {
        return rawTrophies;
    }

    public void setRawTrophies(List<Trophy> rawTrophies) {
        this.rawTrophies = rawTrophies;
    }

    public List<Trophy> getPaxTrophies() {
        return paxTrophies;
    }

    public void setPaxTrophies(List<Trophy> paxTrophies) {
        this.paxTrophies = paxTrophies;
    }

    public List<Trophy> getNoviceTrophies() {
        return noviceTrophies;
    }

    public void setNoviceTrophies(List<Trophy> noviceTrophies) {
        this.noviceTrophies = noviceTrophies;
    }

    public List<Trophy> getClassTrophies() {
        return classTrophies;
    }

    public void setClassTrophies(List<Trophy> classTrophies) {
        this.classTrophies = classTrophies;
    }

    public List<Trophy> getAwardTrophies() {
        return awardTrophies;
    }

    public void setAwardTrophies(List<Trophy> awardTrophies) {
        this.awardTrophies = awardTrophies;
    }
    
    
}
