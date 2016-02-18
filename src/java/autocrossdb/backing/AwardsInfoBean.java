/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package autocrossdb.backing;

import autocrossdb.component.Award;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
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
    private static final int PLACES_TO_POPULATE = 5;
    
    private List<String> rawWins2015;
    private List<String> paxWins2015;
    private List<String> coneKillers2015;
    private List<String> mostEvents2015;
    private List<String> mostRuns2015;
    private List<String> rawWinsPercent2015;
    private List<String> paxWinsPercent2015;
    private List<String> coneKillersPercent2015;
    private List<String> biggestClassAtEvent2015;
    private List<String> mostUniqueDriversInClass2015;
    private List<String> classJumper2015;
    
    
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
        rawWins2015 = populateRawList(objectQuery, PLACES_TO_POPULATE);                                                                                                                                 
        rawWinsPercent2015 = populateRawPercentList(calculatePercent(eventsAttendedQuery, objectQuery), PLACES_TO_POPULATE);
        
        objectQuery = em.createQuery("SELECT count(e.eventPaxWinner), e.eventPaxWinner from Events e where e.eventDate > :begin AND e.eventDate < :end group by e.eventPaxWinner order by count(e.eventPaxWinner) desc ").setParameter("begin", beginYear.getTime()).setParameter("end", endYear.getTime()).getResultList();
        paxWins2015 = populatePaxList(objectQuery, PLACES_TO_POPULATE);
        paxWinsPercent2015 = populatePaxPercentList(calculatePercent(eventsAttendedQuery, objectQuery), PLACES_TO_POPULATE);
        
        objectQuery = em.createQuery("SELECT sum(r.runCones), r.runDriverName from Runs r where r.runEventUrl.eventDate > :begin AND r.runEventUrl.eventDate < :end group by r.runDriverName order by sum(r.runCones) desc").setParameter("begin", beginYear.getTime()).setParameter("end", endYear.getTime()).getResultList();
        coneKillers2015 = populateConesList(objectQuery, PLACES_TO_POPULATE);
        coneKillersPercent2015 = populateConesPercentList(calculatePercent(eventsAttendedQuery, objectQuery), PLACES_TO_POPULATE);
        
        mostEvents2015 = populateEventsList(eventsAttendedQuery, PLACES_TO_POPULATE);
        
        objectQuery = em.createQuery("SELECT count(r.runDriverName), r.runDriverName from Runs r where r.runEventUrl.eventDate > :begin AND r.runEventUrl.eventDate < :end group by r.runDriverName order by count(r.runDriverName) desc").setParameter("begin", beginYear.getTime()).setParameter("end", endYear.getTime()).getResultList();
        mostRuns2015 = populateRunsList(objectQuery, PLACES_TO_POPULATE);
        
        objectQuery = em.createQuery("SELECT count(r.runDriverName), r.runClassName.className, r.runEventUrl.eventLocation, r.runEventUrl.eventDate, r.runEventUrl.eventClubName from Runs r where r.runEventUrl.eventDate > :begin AND r.runEventUrl.eventDate < :end and r.runNumber = 1 and r.runClassName.className != 'NS' group by r.runClassName, r.runEventUrl order by count(r.runDriverName) desc" ).setParameter("begin", beginYear.getTime()).setParameter("end", endYear.getTime()).getResultList();
        biggestClassAtEvent2015 = populateEventClassSizeList(objectQuery, PLACES_TO_POPULATE);
        
        objectQuery = em.createQuery("SELECT count(distinct r.runDriverName), r.runClassName.className from Runs r where r.runEventUrl.eventDate > :begin AND r.runEventUrl.eventDate < :end and r.runNumber = 1 and r.runClassName.className != 'NS' group by r.runClassName order by count(distinct r.runDriverName) desc").setParameter("begin", beginYear.getTime()).setParameter("end", endYear.getTime()).getResultList();
        mostUniqueDriversInClass2015 = populateUniqueDriversList(objectQuery, PLACES_TO_POPULATE);
        
        objectQuery = em.createQuery("SELECT count(distinct r.runClassName.className), r.runDriverName from Runs r where r.runNumber = 1 AND r.runEventUrl.eventDate > :begin AND r.runEventUrl.eventDate < :end group by r.runDriverName order by count(distinct r.runClassName.className) desc").setParameter("begin", beginYear.getTime()).setParameter("end", endYear.getTime()).getResultList();
        classJumper2015 = populateClassJumper(objectQuery, PLACES_TO_POPULATE);
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
            rawPercentMap.put(String.valueOf(rawObject[1]), new Long(String.valueOf(rawObject[0])).doubleValue());
        }
        
        for(Object[] eventObject : eventQuery)
        {
            if(rawPercentMap.containsKey(String.valueOf(eventObject[1])))
            {
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
    
    private static List<String> populateEventClassSizeList(List<Object[]> query, int places)
    {
        List<String> returnList = new ArrayList();
        
        for(int x = 0; x < places; x++)
        {
            returnList.add(query.get(x)[1] + " with " + query.get(x)[0] + " participants at " + query.get(x)[4] + " " + query.get(x)[2] + " " + new SimpleDateFormat("MM/dd/yyyy").format(query.get(x)[3]) + ".");
        }
        return returnList;
    }
    
    private static List<String> populateUniqueDriversList(List<Object[]> query, int places)
    {
        List<String> returnList = new ArrayList();
        
        for(int x = 0; x < places; x++)
        {
            returnList.add(query.get(x)[1] + " with " + query.get(x)[0] + " unique participants.");
        }
        return returnList;
    }
    
    private static List<String> populateClassJumper(List<Object[]> query, int places)
    {
        List<String> returnList = new ArrayList();
        
        for(int x = 0; x < places; x++)
        {
            returnList.add(query.get(x)[1] + " participated in " + query.get(x)[0] + " different classes.");
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

    public List<String> getBiggestClassAtEvent2015() {
        return biggestClassAtEvent2015;
    }

    public void setBiggestClassAtEvent2015(List<String> biggestClassAtEvent2015) {
        this.biggestClassAtEvent2015 = biggestClassAtEvent2015;
    }

    public List<String> getMostUniqueDriversInClass2015() {
        return mostUniqueDriversInClass2015;
    }

    public void setMostUniqueDriversInClass2015(List<String> mostUniqueDriversInClass2015) {
        this.mostUniqueDriversInClass2015 = mostUniqueDriversInClass2015;
    }

    public List<String> getClassJumper2015() {
        return classJumper2015;
    }

    public void setClassJumper2015(List<String> classJumper2015) {
        this.classJumper2015 = classJumper2015;
    }

   
    
    
}
