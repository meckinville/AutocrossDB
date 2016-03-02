/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package autocrossdb.backing;

import autocrossdb.component.AnalyzedEvent;
import autocrossdb.entities.Events;
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
@ManagedBean(name="eventInfo")
@ApplicationScoped
public class EventInfoBean 
{
    private List<AnalyzedEvent> analyzedEvents;
    private AnalyzedEvent selectedAnalyzedEvent;
    
    @PersistenceContext
    private EntityManager em;

    @PostConstruct
    public void init()
    {
        try
        {
            analyzedEvents = new ArrayList();
            List<Events> eventsList = em.createNamedQuery("Events.findAll", Events.class).getResultList();
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
                
                analyzedEvents.add(new AnalyzedEvent(e, totalDrivers, avgRunTime, totalCones));

            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    
    public void analyzeEvent()
    {
        List<Object[]> rawResults = em.createNamedQuery("Runs.findBestRawByEvent", Object[].class).setParameter("eventUrl", selectedAnalyzedEvent.getNeglectedEvent().getEventUrl()).getResultList();
        List<Object[]> paxResults = em.createNamedQuery("Runs.findBestPaxByEvent", Object[].class).setParameter("eventUrl", selectedAnalyzedEvent.getNeglectedEvent().getEventUrl()).getResultList();
        List<Object[]> coneKillerResults = em.createNamedQuery("Runs.findTopConeKiller", Object[].class).setParameter("eventUrl", selectedAnalyzedEvent.getNeglectedEvent().getEventUrl()).getResultList();
        List<Object[]> noviceResults = em.createNamedQuery("Runs.findNoviceChamp", Object[].class).setParameter("eventUrl", selectedAnalyzedEvent.getNeglectedEvent().getEventUrl()).getResultList();
        System.out.println("done with queries");
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
        System.out.println("end of analyzed event");
        System.out.println(selectedAnalyzedEvent.getTopRawName() + " " + selectedAnalyzedEvent.getTopRawCar() + " " + selectedAnalyzedEvent.getTopRawClass() + " " + selectedAnalyzedEvent.getTopRawTime());
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

    

   
    
}
 