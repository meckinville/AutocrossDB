/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package autocrossdb.backing;

import component.Award;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import javax.annotation.PostConstruct;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author rmcconville
 */
@ManagedBean(name="awardsInfo")
@ApplicationScoped
public class AwardsInfoBean 
{
    private List<String> rawWins2015;
    private List<String> paxWins2015;
    private List<String> coneKillers2015;
    private List<String> mostEvents2015;
    private List<String> mostRuns2015;
    private List<String> rawWinsPercent2015;
    private List<String> paxWinsPercent2015;
    private List<String> coneKillersPercent2015;
    
    
    @PersistenceContext
    private EntityManager em;
    
    @PostConstruct
    public void init()
    {     
        Calendar beginYear = Calendar.getInstance();
        Calendar endYear = Calendar.getInstance();
        beginYear.set(2015, Calendar.JANUARY, 1);
        endYear.set(2015, Calendar.DECEMBER, 31);
        List<Object[]> eventsAttendedQuery = em.createQuery("SELECT count(r.runDriverName), r.runDriverName from Runs r where r.runEventUrl.eventDate > :begin AND r.runEventUrl.eventDate < :end and r.runNumber = 1 group by r.runDriverName having count(r.runDriverName) > 3 order by count(r.runDriverName) desc").setParameter("begin", beginYear.getTime()).setParameter("end", endYear.getTime()).getResultList();
        List<Object[]> objectQuery = em.createQuery("SELECT count(e.eventRawWinner), e.eventRawWinner from Events e where e.eventDate > :begin AND e.eventDate < :end group by e.eventRawWinner order by count(e.eventRawWinner) desc ").setParameter("begin", beginYear.getTime()).setParameter("end", endYear.getTime()).getResultList();
        rawWins2015 = populateRawList(objectQuery, 5);                                                                                                                                 
        rawWinsPercent2015 = populateRawPercentList(calculatePercent(eventsAttendedQuery, objectQuery), 5);
        
        objectQuery = em.createQuery("SELECT count(e.eventPaxWinner), e.eventPaxWinner from Events e where e.eventDate > :begin AND e.eventDate < :end group by e.eventPaxWinner order by count(e.eventPaxWinner) desc ").setParameter("begin", beginYear.getTime()).setParameter("end", endYear.getTime()).getResultList();
        paxWins2015 = populatePaxList(objectQuery, 5);
        paxWinsPercent2015 = populatePaxPercentList(calculatePercent(eventsAttendedQuery, objectQuery), 5);
        
        objectQuery = em.createQuery("SELECT sum(r.runCones), r.runDriverName from Runs r where r.runEventUrl.eventDate > :begin AND r.runEventUrl.eventDate < :end group by r.runDriverName order by sum(r.runCones) desc").setParameter("begin", beginYear.getTime()).setParameter("end", endYear.getTime()).getResultList();
        coneKillers2015 = populateConesList(objectQuery, 5);
        coneKillersPercent2015 = populateConesPercentList(calculatePercent(eventsAttendedQuery, objectQuery), 5);
        
        mostEvents2015 = populateEventsList(eventsAttendedQuery, 5);
        
        objectQuery = em.createQuery("SELECT count(r.runDriverName), r.runDriverName from Runs r where r.runEventUrl.eventDate > :begin AND r.runEventUrl.eventDate < :end group by r.runDriverName order by count(r.runDriverName) desc").setParameter("begin", beginYear.getTime()).setParameter("end", endYear.getTime()).getResultList();
        mostRuns2015 = populateRunsList(objectQuery, 5);
    }
    
    private static List<String> populateRawList(List<Object[]> query, int places)
    {
        List<String> returnList = new ArrayList();
        
        for(int x = 0; x < places; x++)
        {
            returnList.add(query.get(x)[1] + " with " + query.get(x)[0] + " raw time wins.");
        }
        return returnList;
    }
    
    private static List<String> populatePaxList(List<Object[]> query, int places)
    {
        List<String> returnList = new ArrayList();
        
        for(int x = 0; x < places; x++)
        {
            returnList.add(query.get(x)[1] + " with " + query.get(x)[0] + " pax time wins.");
        }
        return returnList;
    }
    
    private static List<String> populateConesList(List<Object[]> query, int places)
    {
        List<String> returnList = new ArrayList();
        
        for(int x = 0; x < places; x++)
        {
            returnList.add(query.get(x)[1] + " with " + query.get(x)[0] + " total cones hit.");
        }
        return returnList;
    }
    
    private static List<String> populateEventsList(List<Object[]> query, int places)
    {
        List<String> returnList = new ArrayList();
        
        for(int x = 0; x < places; x++)
        {
            returnList.add(query.get(x)[1] + " with " + query.get(x)[0] + " events attended.");
        }
        return returnList;
    }
    
    private static List<String> populateRunsList(List<Object[]> query, int places)
    {
        List<String> returnList = new ArrayList();
        
        for(int x = 0; x < places; x++)
        {
            returnList.add(query.get(x)[1] + " with " + query.get(x)[0] + " runs taken.");
        }
        return returnList;
    }
    
    private static List<Award> calculatePercent(List<Object[]> eventQuery, List<Object[]> rawQuery)
    {
        Map<String, Number> rawPercentMap = new TreeMap();
        for(Object[] rawObject : rawQuery)
        {
            System.out.println("Added " + String.valueOf(rawObject[1]) + " to rawPercentMap");
            rawPercentMap.put(String.valueOf(rawObject[1]), new Long(String.valueOf(rawObject[0])).doubleValue());
        }
        
        for(Object[] eventObject : eventQuery)
        {
            if(rawPercentMap.containsKey(String.valueOf(eventObject[1])))
            {
                System.out.println("rawPercentMap contains " + String.valueOf(eventObject[1]));
                double eventsAttended = new Long(String.valueOf(eventObject[0])).doubleValue();
                double rawWins = rawPercentMap.get(String.valueOf(eventObject[1])).doubleValue();
                double percent = rawWins / eventsAttended;
                rawPercentMap.put(String.valueOf(eventObject[1]), percent * 100);
                
            }
        }
        Set<String> driverNames = rawPercentMap.keySet();
        Collection<Number> driverPercents = rawPercentMap.values();
        List<Award> awardList = new ArrayList();
        Iterator<String> namesIt = driverNames.iterator();
        Iterator<Number> percentsIt = driverPercents.iterator();
        while(namesIt.hasNext())
        {
            awardList.add(new Award(percentsIt.next().doubleValue(), namesIt.next()));
        }
        
        Collections.sort(awardList, Collections.reverseOrder());
        return awardList;
    }
    
    private static List<String> populateRawPercentList(List<Award> awardList, int places)
    {
        List<String> returnList = new ArrayList();
        Iterator<Award> awardIt = awardList.iterator();
        for(int x = 0; x < places; x++)
        {
            Award next = awardIt.next();
            returnList.add(next.getName() + " had top Raw Time at " + Math.round(next.getValue()) + "% of attended events.");
        }
        
        return returnList;
    }
    
    private static List<String> populatePaxPercentList(List<Award> awardList, int places)
    {
        List<String> returnList = new ArrayList();
        Iterator<Award> awardIt = awardList.iterator();
        for(int x = 0; x < places; x++)
        {
            Award next = awardIt.next();
            returnList.add(next.getName() + " had top Pax Time at " + Math.round(next.getValue()) + "% of attended events.");
        }
        
        return returnList;
    }
    
    private static List<String> populateConesPercentList(List<Award> awardList, int places)
    {
        List<String> returnList = new ArrayList();
        Iterator<Award> awardIt = awardList.iterator();
        for(int x = 0; x < places; x++)
        {
            Award next = awardIt.next();
            returnList.add(next.getName() + " hit " + String.format("%.3f", next.getValue()/100) + " cones per attended event.");
        }
        
        return returnList;
    }

    public List<String> getRawWins2015() {
        return rawWins2015;
    }

    public void setRawWins2015(List<String> rawWins2015) {
        this.rawWins2015 = rawWins2015;
    }

    public List<String> getPaxWins2015() {
        return paxWins2015;
    }

    public void setPaxWins2015(List<String> paxWins2015) {
        this.paxWins2015 = paxWins2015;
    }

    public List<String> getConeKillers2015() {
        return coneKillers2015;
    }

    public List<String> getRawWinsPercent2015() {
        return rawWinsPercent2015;
    }

    public void setRawWinsPercent2015(List<String> rawWinsPercent2015) {
        this.rawWinsPercent2015 = rawWinsPercent2015;
    }

    public List<String> getPaxWinsPercent2015() {
        return paxWinsPercent2015;
    }

    public void setPaxWinsPercent2015(List<String> paxWinsPercent2015) {
        this.paxWinsPercent2015 = paxWinsPercent2015;
    }

    public List<String> getConeKillersPercent2015() {
        return coneKillersPercent2015;
    }

    public void setConeKillersPercent2015(List<String> coneKillersPercent2015) {
        this.coneKillersPercent2015 = coneKillersPercent2015;
    }

    
    public void setConeKillers2015(List<String> coneKillers2015) {
        this.coneKillers2015 = coneKillers2015;
    }

    public List<String> getMostEvents2015() {
        return mostEvents2015;
    }

    public void setMostEvents2015(List<String> mostEvents2015) {
        this.mostEvents2015 = mostEvents2015;
    }

    public List<String> getMostRuns2015() {
        return mostRuns2015;
    }

    public void setMostRuns2015(List<String> mostRuns2015) {
        this.mostRuns2015 = mostRuns2015;
    }

   
    
    
}
