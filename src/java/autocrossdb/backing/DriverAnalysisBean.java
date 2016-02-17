/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package autocrossdb.backing;

import autocrossdb.entities.Events;
import autocrossdb.entities.Runs;
import component.AnalyzedEvent;
import component.Nemesis;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author rmcconville
 */
@ManagedBean(name="driverAnalysis")
@ViewScoped
public class DriverAnalysisBean 
{
    private String driver;
    private String driverLabel = "";
    
    private List<AnalyzedEvent> events;
    private AnalyzedEvent selectedEvent;
    Set<Nemesis> nemesisList = new TreeSet();
    
    @PersistenceContext
    private EntityManager em;
    
    @PostConstruct
    public void init()
    {
        events = new ArrayList();
    }
    
    public void analyzeDriver()
    {
        driverLabel = driver;
        List<Events> rawEventList = em.createQuery("SELECT e from Events e JOIN e.runsCollection r WHERE r.runDriverName = :driver AND r.runNumber = 1 ORDER BY e.eventDate desc", Events.class).setParameter("driver", this.driver).getResultList();

        for(Events e : rawEventList)
        {
            List<Runs> yourRuns = em.createQuery("SELECT r FROM Runs r where r.runDriverName = :driver and r.runEventUrl = :url", Runs.class).setParameter("driver", driver).setParameter("url", e).getResultList();
            List<Object[]> competitorRuns = em.createQuery("SELECT min(r.runTime), r.runDriverName, r.runCarName FROM Runs r WHERE r.runClassName.className = :class AND r.runEventUrl = :url AND r.runOffcourse = 'N' group by r.runDriverName order by min(r.runTime)", Object[].class).setParameter("class", yourRuns.get(0).getRunClassName().getClassName()).setParameter("url", e).getResultList();
            String classPosition = "";
            
            boolean beatingUs = true;
            for(int x = 0; x < competitorRuns.size(); x++)
            {
                if(competitorRuns.get(x)[1].equals(driver))
                {
                    beatingUs = false;
                    classPosition = x+1 + "/" + competitorRuns.size();
                }
                else if(beatingUs)
                {
                   Nemesis temp = new Nemesis(String.valueOf(competitorRuns.get(x)[1]), 5);
                   if(nemesisList.contains(x))
                   {
                       
                   }
                }
                else
                {
                    
                }
                
            }
            
            String rawPosition = "";
            List<Object[]> rawRuns = em.createQuery("SELECT min(r.runTime), r.runDriverName, r.runCarName FROM Runs r WHERE r.runEventUrl = :url AND r.runOffcourse = 'N' group by r.runDriverName order by min(r.runTime)", Object[].class).setParameter("url", e).getResultList();
            for(int x = 0; x < rawRuns.size(); x++)
            {
                if(rawRuns.get(x)[1].equals(driver))
                {
                    rawPosition = x+1 + "/" + rawRuns.size();
                }
                
            }
            
            String paxPosition = "";
            List<Object[]> paxRuns = em.createQuery("SELECT min(r.runPaxTime), r.runDriverName, r.runCarName FROM Runs r WHERE r.runEventUrl = :url AND r.runOffcourse = 'N' group by r.runDriverName order by min(r.runPaxTime)", Object[].class).setParameter("url", e).getResultList();
            for(int x = 0; x < paxRuns.size(); x++)
            {
                if(paxRuns.get(x)[1].equals(driver))
                {
                    paxPosition = x+1 + "/" + paxRuns.size();
                }
                
            }
            
            List<Runs> classRuns =  em.createQuery("SELECT r FROM Runs r WHERE r.runClassName.className = :class AND r.runEventUrl = :url AND r.runOffcourse = 'N' group by r.runDriverName order by min(r.runTime)", Runs.class).setParameter("class", yourRuns.get(0).getRunClassName().getClassName()).setParameter("url", e).getResultList();
            events.add(new AnalyzedEvent(e, driver, yourRuns.get(0).getRunClassName().getClassName(), yourRuns.get(0).getRunCarName(), classPosition, rawPosition, paxPosition, competitorRuns));
        }
        driver = "";
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

    public List<AnalyzedEvent> getEvents() {
        return events;
    }

    public void setEvents(List<AnalyzedEvent> events) {
        this.events = events;
    }

    public String getDriver() {
        return driver;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }

    public AnalyzedEvent getSelectedEvent() {
        return selectedEvent;
    }

    public void setSelectedEvent(AnalyzedEvent selectedEvent) {
        this.selectedEvent = selectedEvent;
    }

    public String getDriverLabel() {
        return driverLabel;
    }

    public void setDriverLabel(String driverLabel) {
        this.driverLabel = driverLabel;
    }
    
    
    
}
