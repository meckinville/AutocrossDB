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
import javax.faces.bean.ManagedBean;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author rmcconville
 */
@ManagedBean(name="eventInfo")
public class EventInfoBean 
{
    private String event;

    private List<Events> eventsList;
    private List<AnalyzedEvent> analyzedEvents;
    private AnalyzedEvent analyzedEvent;
    
    @PersistenceContext
    private EntityManager em;
    
    

    @PostConstruct
    public void init()
    {
        try
        {
            analyzedEvents = new ArrayList();
            eventsList = em.createNamedQuery("Events.findAll", Events.class).getResultList();
            for(Events e : eventsList)
            {
                analyzedEvents.add(new AnalyzedEvent(e));
                System.out.println("added: " + e.getEventUrl() + " " + e.getEventClubName() + " " + e.getEventLocation());
            }
            System.out.println("total events: " + analyzedEvents.size());
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    
    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public List<Events> getEventsList() {
        return eventsList;
    }

    public void setEventsList(List<Events> eventsList) {
        this.eventsList = eventsList;
    }

    public AnalyzedEvent getAnalyzedEvent() {
        return analyzedEvent;
    }

    public void setAnalyzedEvent(AnalyzedEvent analyzedEvent) {
        this.analyzedEvent = analyzedEvent;
    }

    public List<AnalyzedEvent> getAnalyzedEvents() {
        return analyzedEvents;
    }

    public void setAnalyzedEvents(List<AnalyzedEvent> analyzedEvents) {
        this.analyzedEvents = analyzedEvents;
    }

   
    
}
 