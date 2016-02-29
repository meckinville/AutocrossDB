/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package autocrossdb.backing;

import autocrossdb.entities.Events;
import autocrossdb.entities.Runs;
import autocrossdb.component.AnalyzedEvent;
import autocrossdb.component.Nemesis;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
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
    private String driver = "";
    private Date endDate;
    private Date startDate;
    private long progress;
    
    private List<AnalyzedEvent> events;
    private AnalyzedEvent selectedEvent;
    Set<Nemesis> nemesisList = new TreeSet();
    
    @PersistenceContext
    private EntityManager em;
    
    @PostConstruct
    public void init()
    {
        events = new ArrayList();
        Calendar now = Calendar.getInstance();
        endDate = now.getTime();
        now.set(Calendar.MONTH, now.get(Calendar.MONTH)-8);
        startDate = now.getTime();
    }
    
    public void analyzeDriver()
    {
        events = new ArrayList();
        List<Events> rawEventList = em.createQuery("SELECT e from Events e JOIN e.runsCollection r WHERE r.runDriverName = :driver AND r.runNumber = 1 AND e.eventDate > :start AND e.eventDate < :end ORDER BY e.eventDate desc", Events.class).setParameter("driver", this.driver).setParameter("start", startDate).setParameter("end", endDate).getResultList();

        for(Events e : rawEventList)
        {
            List<Runs> yourRuns = em.createQuery("SELECT r FROM Runs r where r.runDriverName = :driver and r.runEventUrl = :url", Runs.class).setParameter("driver", driver).setParameter("url", e).getResultList();
            List<Object[]> competitorRuns = em.createQuery("SELECT min(r.runTime), r.runDriverName, r.runCarName FROM Runs r WHERE r.runClassName.className = :class AND r.runEventUrl = :url AND r.runOffcourse = 'N' group by r.runDriverName order by min(r.runTime)", Object[].class).setParameter("class", yourRuns.get(0).getRunClassName().getClassName()).setParameter("url", e).getResultList();
            String classPosition = "";
            double ourRunTime = 0;
            
            boolean beatingUs = true;
            for(int x = 0; x < competitorRuns.size(); x++)
            {
                if(competitorRuns.get(x)[1].equals(driver))
                {
                    beatingUs = false;
                    classPosition = String.valueOf(x+1);
                    ourRunTime = (double)competitorRuns.get(x)[0];
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
            List<Object[]> rawRuns = em.createQuery("SELECT min(r.runTime), r.runDriverName, r.runCarName, r.runClassName.className FROM Runs r WHERE r.runEventUrl = :url AND r.runOffcourse = 'N' group by r.runDriverName order by min(r.runTime)", Object[].class).setParameter("url", e).getResultList();
            for(int x = 0; x < rawRuns.size(); x++)
            {
                if(rawRuns.get(x)[1].equals(driver))
                {
                    rawPosition = String.valueOf(x+1);
                    break;
                }
                
            }
            
            String paxPosition = "";
            List<Object[]> paxRuns = em.createQuery("SELECT min(r.runPaxTime), r.runDriverName, r.runCarName, r.runClassName.className FROM Runs r WHERE r.runEventUrl = :url AND r.runOffcourse = 'N' group by r.runDriverName order by min(r.runPaxTime)", Object[].class).setParameter("url", e).getResultList();
            for(int x = 0; x < paxRuns.size(); x++)
            {
                if(paxRuns.get(x)[1].equals(driver))
                {
                    paxPosition = String.valueOf(x+1);
                    break;
                }
                
            }
            
            List<Integer> bestRunQuery = em.createQuery("SELECT r.runNumber FROM Runs r WHERE r.runDriverName = :driver and r.runTime = :runTime").setParameter("driver", driver).setParameter("runTime", ourRunTime).getResultList();
            long conesKilled = (long)em.createQuery("SELECT sum(r.runCones) FROM Runs r where r.runDriverName = :driver AND r.runEventUrl = :url").setParameter("driver", driver).setParameter("url", e).getResultList().get(0);
            
            double bestTimeIgnoringCones = yourRuns.get(0).getRunTime();
            for(int x = 1; x < yourRuns.size(); x++)
            {
                if(yourRuns.get(x).getRunOffcourse().equals("N"))
                {
                    double newRunTime = yourRuns.get(x).getRunTime();
                    newRunTime -= 2 * yourRuns.get(x).getRunCones();
                    if(newRunTime < bestTimeIgnoringCones)
                    {
                        bestTimeIgnoringCones = newRunTime;
                    }
                }      
            }
            List<Runs> noConesQuery = em.createQuery("SELECT r FROM Runs r where r.runEventUrl = :url and r.runClassName.className = :class and r.runOffcourse = 'N'").setParameter("url", e).setParameter("class", yourRuns.get(0).getRunClassName().getClassName()).getResultList();
            
            events.add(new AnalyzedEvent(e, yourRuns, classPosition, rawPosition, paxPosition, bestRunQuery.get(0) + " out of " + yourRuns.size(), conesKilled, bestTimeIgnoringCones, competitorRuns, rawRuns, paxRuns, noConesQuery));
            progress += 100 / rawEventList.size();
        }
        progress = 100;
        
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
    
    public void onCompleteLoad()
    {
        progress = 0;
    }

    public long getProgress() {
        if(progress > 100)
        {
            progress = 100;
        }
        return progress;
    }

    public void setProgress(long progress) {
        this.progress = progress;
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
    
    
    
}
