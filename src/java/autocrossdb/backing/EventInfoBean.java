/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package autocrossdb.backing;

import autocrossdb.component.AnalyzedEvent;
import autocrossdb.entities.Events;
import autocrossdb.entities.Runs;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author rmcconville
 */
@ManagedBean(name="eventInfo")
@SessionScoped
public class EventInfoBean implements Serializable
{
    private List<AnalyzedEvent> analyzedEvents;
    private AnalyzedEvent selectedAnalyzedEvent;
    
    private Date startDate;
    private Date endDate;
    
    private String clubFilter = "";
    
    @PersistenceContext
    private EntityManager em;

    @PostConstruct
    public void init()
    {
        Calendar now = Calendar.getInstance();
        endDate = now.getTime();
        now.set(Calendar.MONTH, now.get(Calendar.MONTH)-3);
        startDate = now.getTime();
        getEvents();
    }
    
    public void analyzeEvent()
    {
        List<Object[]> rawResults = em.createNamedQuery("Runs.findBestRawByEvent", Object[].class).setParameter("eventUrl", selectedAnalyzedEvent.getNeglectedEvent().getEventUrl()).getResultList();
        List<Object[]> paxResults = em.createNamedQuery("Runs.findBestPaxByEvent", Object[].class).setParameter("eventUrl", selectedAnalyzedEvent.getNeglectedEvent().getEventUrl()).getResultList();
        List<Object[]> coneKillerResults = em.createNamedQuery("Runs.findTopConeKiller", Object[].class).setParameter("eventUrl", selectedAnalyzedEvent.getNeglectedEvent().getEventUrl()).getResultList();
        List<Object[]> noviceResults = em.createNamedQuery("Runs.findNoviceChamp", Object[].class).setParameter("eventUrl", selectedAnalyzedEvent.getNeglectedEvent().getEventUrl()).getResultList();
        selectedAnalyzedEvent.setTopRawName(rawResults.get(0)[0].toString());
        selectedAnalyzedEvent.setTopRawCar(rawResults.get(0)[1].toString());
        selectedAnalyzedEvent.setTopRawClass(rawResults.get(0)[2].toString());
        selectedAnalyzedEvent.setTopRawTime(rawResults.get(0)[3].toString());
        
        selectedAnalyzedEvent.setTopPaxName(paxResults.get(0)[0].toString());
        selectedAnalyzedEvent.setTopPaxCar(paxResults.get(0)[1].toString());
        selectedAnalyzedEvent.setTopPaxClass(paxResults.get(0)[2].toString());
        selectedAnalyzedEvent.setTopPaxTime(paxResults.get(0)[3].toString());
        
        selectedAnalyzedEvent.setTopConeKillerName(coneKillerResults.get(0)[0].toString());
        selectedAnalyzedEvent.setTopConeKillerCar(coneKillerResults.get(0)[1].toString());
        selectedAnalyzedEvent.setTopConeKillerClass(coneKillerResults.get(0)[2].toString());
        selectedAnalyzedEvent.setTopConeKillerCones(coneKillerResults.get(0)[3].toString());
        
        if(noviceResults.size() == 0)
        {
            selectedAnalyzedEvent.setNoviceChampName("No Novices.");
            selectedAnalyzedEvent.setNoviceChampTime("N/A");
            selectedAnalyzedEvent.setNoviceChampClass("N/A");
            selectedAnalyzedEvent.setNoviceChampCar("N/A");
        }
        else
        {
            selectedAnalyzedEvent.setNoviceChampName(noviceResults.get(0)[0].toString());
            selectedAnalyzedEvent.setNoviceChampCar(noviceResults.get(0)[1].toString());
            selectedAnalyzedEvent.setNoviceChampClass(noviceResults.get(0)[2].toString());
            selectedAnalyzedEvent.setNoviceChampTime(noviceResults.get(0)[3].toString());
        }
    }
    
    public void getEvents()
    {
        try
        {
            analyzedEvents = new ArrayList();
            List<Events> eventsList = new ArrayList();
            
            if(clubFilter.equals("") || clubFilter == null)
            {
                eventsList = em.createNamedQuery("Events.findEventsInDateRange", Events.class).setParameter("startDate", startDate).setParameter("endDate", endDate).getResultList();
            }
            else
            {
                eventsList = em.createNamedQuery("Events.findClubEventsInDateRange", Events.class).setParameter("startDate", startDate).setParameter("endDate", endDate).setParameter("clubName", clubFilter).getResultList();
            }
            
            for(Events e : eventsList)
            {
                
                int totalDrivers = em.createNamedQuery("Runs.findTotalDriversAtEvent", Object[].class).setParameter("eventUrl", e.getEventUrl()).getResultList().size();
                
                List<Double> doubleResults = em.createQuery("SELECT min(r.runTime) FROM Runs r where r.runEventUrl.eventUrl = :eventUrl and r.runOffcourse='N' group by r.runDriverName order by min(r.runTime) asc", Double.class).setParameter("eventUrl", e.getEventUrl()).getResultList();
                double sum = 0;
                for(Double d : doubleResults)
                {
                    sum += d;
                } 
                double tempAvg = sum / doubleResults.size();
                tempAvg = (double)Math.round(tempAvg * 1000d)/1000d;
                double avgRunTime = tempAvg;
               
                long totalCones = em.createNamedQuery("Runs.findTotalConesHitAtEvent", Long.class).setParameter("eventUrl", e.getEventUrl()).getResultList().get(0);
                int runs = (int)em.createQuery("SELECT max(r.runNumber) from Runs r where r.runEventUrl.eventUrl = :eventUrl").setParameter("eventUrl", e.getEventUrl()).getResultList().get(0);
                long offCourseRuns = (long)em.createQuery("SELECT count(r) from Runs r where r.runOffcourse = 'Y' and r.runEventUrl.eventUrl = :eventUrl").setParameter("eventUrl", e.getEventUrl()).getResultList().get(0);
                List<Object[]> rawTimes = em.createQuery("SELECT min(r.runTime), r.runDriverName, r.runClassName.className, r.runCarName, r.runCones from Runs r where r.runEventUrl.eventUrl = :eventUrl and r.runOffcourse = 'N' group by r.runDriverName order by min(r.runTime) asc").setParameter("eventUrl", e.getEventUrl()).getResultList();
                List<Object[]> paxTimes = em.createQuery("SELECT min(r.runPaxTime), r.runDriverName, r.runClassName.className, r.runCarName, r.runCones from Runs r where r.runEventUrl.eventUrl = :eventUrl and r.runOffcourse = 'N' group by r.runDriverName order by min(r.runPaxTime) asc").setParameter("eventUrl", e.getEventUrl()).getResultList();
                List<Object[]> classTimes = em.createQuery("SELECT min(r.runTime), r.runDriverName, r.runClassName.className, r.runCarName, r.runCones from Runs r where r.runEventUrl.eventUrl = :eventUrl and r.runOffcourse = 'N' group by r.runDriverName order by r.runClassName.className, min(r.runTime) asc").setParameter("eventUrl", e.getEventUrl()).getResultList();

                analyzedEvents.add(new AnalyzedEvent(e, totalDrivers, avgRunTime, totalCones, runs, offCourseRuns, rawTimes, paxTimes, classTimes));

            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    
    public List<String> completeClubText(String query)
    {
        List<String> results = new ArrayList<String>();
        List<String> driverList = em.createQuery("SELECT distinct(e.eventClubName) FROM Events e ", String.class).getResultList();
        for(int x = 0; x < driverList.size(); x++)
        {
            if(driverList.get(x).contains(query.toUpperCase()))
            {
                results.add(driverList.get(x));
            }
        }
        return results;
    }

    public List<AnalyzedEvent> getAnalyzedEvents() {
        return analyzedEvents;
    }

    public void setAnalyzedEvents(List<AnalyzedEvent> analyzedEvents) {
        this.analyzedEvents = analyzedEvents;
    }

    public AnalyzedEvent getSelectedAnalyzedEvent() {
        return selectedAnalyzedEvent;
    }

    public void setSelectedAnalyzedEvent(AnalyzedEvent selectedAnalyzedEvent) {
        this.selectedAnalyzedEvent = selectedAnalyzedEvent;
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

    public String getClubFilter() {
        return clubFilter;
    }

    public void setClubFilter(String clubFilter) {
        this.clubFilter = clubFilter;
    }

    

   
    
}
 