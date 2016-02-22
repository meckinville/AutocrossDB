/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package autocrossdb.backing;

import autocrossdb.component.Award;
import autocrossdb.entities.Events;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
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
    private static final DateFormat webFormat = new SimpleDateFormat("MM-dd-yyyy");
    
    private static final int PLACES_TO_POPULATE = 5;
    
    private static final int RAW_WINS_AWARD_POSITION = 0;
    private static final int RAW_PERCENT_WINS_AWARD_POSITION = 1;
    private static final int PAX_WINS_AWARD_POSITION = 2;
    private static final int PAX_PERCENT_WINS_AWARD_POSITION = 3;
    private static final int CONE_KILLER_AWARD_POSITION = 4;
    private static final int CONE_KILLER_PERCENT_AWARD_POSITION = 5;
    private static final int MOST_EVENTS_AWARD_POSITION = 6;
    private static final int MOST_RUNS_AWARD_POSITION = 7;
    private static final int BIGGEST_CLASS_AWARD_POSITION = 8;
    private static final int MOST_UNIQUE_DRIVERS_AWARD_POSITION = 9;
    private static final int CLASS_JUMPER_AWARD_POSITION = 10;
    private static final int DIRTIEST_RUN_AWARD_POSITION = 11;
    
    private List<List<String>> awards2016;
    private List<Long> stats2016;
    private List<List<String>> awards2015;
    private List<Long> stats2015;
    private List<List<String>> awards2014;
    private List<Long> stats2014;
    private List<List<String>> awards2013;
    private List<Long> stats2013;
    
    
    @PersistenceContext
    private EntityManager em;
    
    @PostConstruct
    public void init()
    {   
        awards2014 = getAwardsForYear(2014);
        stats2014 = getStatsForYear(2014);
        awards2015 = getAwardsForYear(2015);
        stats2015 = getStatsForYear(2015);
        awards2016 = getAwardsForYear(2016);
        stats2016 = getStatsForYear(2016);
    }
    
    private List<Long> getStatsForYear(int year)
    {
        List<Long> stats = new ArrayList<Long>();
        
        Calendar beginYear = Calendar.getInstance();
        Calendar endYear = Calendar.getInstance();
        beginYear.set(year, Calendar.JANUARY, 1);
        endYear.set(year, Calendar.DECEMBER, 31);
        
        stats.add((long)em.createQuery("SELECT e FROM Events e where e.eventDate > :begin AND e.eventDate < :end").setParameter("begin", beginYear.getTime()).setParameter("end", endYear.getTime()).getResultList().size());
        stats.add((long)em.createQuery("SELECT count(distinct r.runDriverName) FROM Runs r where r.runEventUrl.eventDate > :begin AND r.runEventUrl.eventDate < :end").setParameter("begin", beginYear.getTime()).setParameter("end", endYear.getTime()).getResultList().get(0));
        return stats;
    }
    
    private List<List<String>> getAwardsForYear(int year)
    {
        List<List<String>> awards = new ArrayList<List<String>>();
        
        Calendar beginYear = Calendar.getInstance();
        Calendar endYear = Calendar.getInstance();
        beginYear.set(year, Calendar.JANUARY, 1);
        endYear.set(year, Calendar.DECEMBER, 31);
        List<Object[]> eventsAttendedQuery = em.createQuery("SELECT count(r.runDriverName), r.runDriverName from Runs r where r.runEventUrl.eventDate > :begin AND r.runEventUrl.eventDate < :end and r.runNumber = 1 group by r.runDriverName having count(r.runDriverName) > 3 order by count(r.runDriverName) desc").setParameter("begin", beginYear.getTime()).setParameter("end", endYear.getTime()).getResultList();
        List<Object[]> objectQuery = em.createQuery("SELECT count(e.eventRawWinner), e.eventRawWinner from Events e where e.eventDate > :begin AND e.eventDate < :end group by e.eventRawWinner order by count(e.eventRawWinner) desc ").setParameter("begin", beginYear.getTime()).setParameter("end", endYear.getTime()).getResultList();
        
        //add raw event winners and raw percent wins
        awards.add(populateRawList(objectQuery, PLACES_TO_POPULATE));
        awards.add(populateRawPercentList(calculatePercent(eventsAttendedQuery, objectQuery), PLACES_TO_POPULATE));
        
        objectQuery = em.createQuery("SELECT count(e.eventPaxWinner), e.eventPaxWinner from Events e where e.eventDate > :begin AND e.eventDate < :end group by e.eventPaxWinner order by count(e.eventPaxWinner) desc ").setParameter("begin", beginYear.getTime()).setParameter("end", endYear.getTime()).getResultList();
        //add pax event winners and pax percent wins
        awards.add(populatePaxList(objectQuery, PLACES_TO_POPULATE));
        awards.add(populatePaxPercentList(calculatePercent(eventsAttendedQuery, objectQuery), PLACES_TO_POPULATE));
        
        objectQuery = em.createQuery("SELECT sum(r.runCones), r.runDriverName from Runs r where r.runEventUrl.eventDate > :begin AND r.runEventUrl.eventDate < :end group by r.runDriverName order by sum(r.runCones) desc").setParameter("begin", beginYear.getTime()).setParameter("end", endYear.getTime()).getResultList();
        
        //add most cones total and most cones per event
        awards.add(populateConesList(objectQuery, PLACES_TO_POPULATE));
        awards.add(populateConesPercentList(calculatePercent(eventsAttendedQuery, objectQuery), PLACES_TO_POPULATE));
        
        //add most events attended
        awards.add(populateEventsList(eventsAttendedQuery, PLACES_TO_POPULATE));
        
        objectQuery = em.createQuery("SELECT count(r.runDriverName), r.runDriverName from Runs r where r.runEventUrl.eventDate > :begin AND r.runEventUrl.eventDate < :end group by r.runDriverName order by count(r.runDriverName) desc").setParameter("begin", beginYear.getTime()).setParameter("end", endYear.getTime()).getResultList();
        //add most runs taken
        awards.add(populateRunsList(objectQuery, PLACES_TO_POPULATE));
        
        objectQuery = em.createQuery("SELECT count(r.runDriverName), r.runClassName.className, r.runEventUrl.eventLocation, r.runEventUrl.eventDate, r.runEventUrl.eventClubName from Runs r where r.runEventUrl.eventDate > :begin AND r.runEventUrl.eventDate < :end and r.runNumber = 1 and r.runClassName.className != 'NS' group by r.runClassName, r.runEventUrl order by count(r.runDriverName) desc" ).setParameter("begin", beginYear.getTime()).setParameter("end", endYear.getTime()).getResultList();
        //add biggest class at event
        awards.add(populateEventClassSizeList(objectQuery, PLACES_TO_POPULATE));
        
        objectQuery = em.createQuery("SELECT count(distinct r.runDriverName), r.runClassName.className from Runs r where r.runEventUrl.eventDate > :begin AND r.runEventUrl.eventDate < :end and r.runNumber = 1 and r.runClassName.className != 'NS' group by r.runClassName order by count(distinct r.runDriverName) desc").setParameter("begin", beginYear.getTime()).setParameter("end", endYear.getTime()).getResultList();
        //add most unique drivers in class for the year
        awards.add(populateUniqueDriversList(objectQuery, PLACES_TO_POPULATE));
        
        objectQuery = em.createQuery("SELECT count(distinct r.runClassName.className), r.runDriverName from Runs r where r.runNumber = 1 AND r.runEventUrl.eventDate > :begin AND r.runEventUrl.eventDate < :end group by r.runDriverName order by count(distinct r.runClassName.className) desc").setParameter("begin", beginYear.getTime()).setParameter("end", endYear.getTime()).getResultList();
        //add class jumper
        awards.add(populateClassJumper(objectQuery, PLACES_TO_POPULATE));
        
        objectQuery = em.createQuery("SELECT max(r.runCones), r.runDriverName, r.runEventUrl, r.runNumber from Runs r where r.runEventUrl.eventDate > :begin AND r.runEventUrl.eventDate < :end and r.runOffcourse = 'N' group by r.runId order by max(r.runCones) desc").setParameter("begin", beginYear.getTime()).setParameter("end", endYear.getTime()).getResultList();
        //add dirtiest run
        awards.add(populateDirtiestRun(objectQuery, PLACES_TO_POPULATE));
        
        return awards;
    }
    
    private static List<String> populateRawList(List<Object[]> query, int places)
    {
        if(query.size() == 0)
        {
            return null;
        }
        
        List<String> returnList = new ArrayList();
        try
        {
            for(int x = 0; x < places; x++)
            {
                returnList.add(query.get(x)[1] + " with " + query.get(x)[0] + " raw time wins.");
            }
            return returnList;
        }
        catch(IndexOutOfBoundsException e)
        {
            while(returnList.size() < places)
            {
                returnList.add("No other eligible drivers.");
            }
            return returnList;
        }
        
    }
    
    private static List<String> populatePaxList(List<Object[]> query, int places)
    {
        if(query.size() == 0)
        {
            return null;
        }
        
        List<String> returnList = new ArrayList();
        
        try
        {
            for(int x = 0; x < places; x++)
            {
                returnList.add(query.get(x)[1] + " with " + query.get(x)[0] + " pax time wins.");
            }
            return returnList;
        }
        catch(IndexOutOfBoundsException e)
        {
            while(returnList.size() < places)
            {
                returnList.add("No other eligible drivers.");
            }
            return returnList;
        }
    }
    
    private static List<String> populateConesList(List<Object[]> query, int places)
    {
        if(query.size() == 0)
        {
            return null;
        }
        
        List<String> returnList = new ArrayList();
        
        try
        {
            for(int x = 0; x < places; x++)
            {
                returnList.add(query.get(x)[1] + " with " + query.get(x)[0] + " total cones hit.");
            }
            return returnList;
        }
        catch(IndexOutOfBoundsException e)
        {
            while(returnList.size() < places)
            {
                returnList.add("No other eligible drivers.");
            }
            return returnList;
        }
    }
    
    private static List<String> populateEventsList(List<Object[]> query, int places)
    {
        if(query.size() == 0)
        {
            return null;
        }
        
        List<String> returnList = new ArrayList();
        
        try
        {
            for(int x = 0; x < places; x++)
            {
                returnList.add(query.get(x)[1] + " with " + query.get(x)[0] + " events attended.");
            }
            return returnList;
        }
        catch(IndexOutOfBoundsException e)
        {
            while(returnList.size() < places)
            {
                returnList.add("No other eligible drivers.");
            }
            return returnList;
        }
    }
    
    private static List<String> populateRunsList(List<Object[]> query, int places)
    {
        if(query.size() == 0)
        {
            return null;
        }
        
        List<String> returnList = new ArrayList();
        
        try
        {
            for(int x = 0; x < places; x++)
            {
                returnList.add(query.get(x)[1] + " with " + query.get(x)[0] + " runs taken.");
            }
            return returnList;
        }
        catch(IndexOutOfBoundsException e)
        {
            while(returnList.size() < places)
            {
                returnList.add("No other eligible drivers.");
            }
            return returnList;
        }
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
        if(awardList.size() == 0)
        {
            return null;
        }
        
        List<String> returnList = new ArrayList();
        Iterator<Award> awardIt = awardList.iterator();
        try
        {
            for(int x = 0; x < places; x++)
            {
                Award next = awardIt.next();
                returnList.add(next.getName() + " had top Raw Time at " + Math.round(next.getValue()) + "% of attended events.");
            }

            return returnList;
        }
        catch(NoSuchElementException e)
        {
            while(returnList.size() < places)
            {
                returnList.add("No other eligible drivers.");
            }
            return returnList;
        }
    }
    
    private static List<String> populatePaxPercentList(List<Award> awardList, int places)
    {
        if(awardList.size() == 0)
        {
            return null;
        }
        
        List<String> returnList = new ArrayList();
        Iterator<Award> awardIt = awardList.iterator();
            
        try
        {
            for(int x = 0; x < places; x++)
            {
                Award next = awardIt.next();
                returnList.add(next.getName() + " had top Pax Time at " + Math.round(next.getValue()) + "% of attended events.");
            }
        
            return returnList;
        }
        catch(NoSuchElementException e)
        {
            while(returnList.size() < places)
            {
                returnList.add("No other eligible drivers.");
            }
            return returnList;
        }
    }
    
    private static List<String> populateConesPercentList(List<Award> awardList, int places)
    {
        if(awardList.size() == 0)
        {
            return null;
        }
        
        List<String> returnList = new ArrayList();
        Iterator<Award> awardIt = awardList.iterator();
            
        try
        {
            for(int x = 0; x < places; x++)
            {
                Award next = awardIt.next();
                returnList.add(next.getName() + " hit " + String.format("%.3f", next.getValue()/100) + " cones per attended event.");
            }

            return returnList;
        }
        catch(NoSuchElementException e)
        {
            while(returnList.size() < places)
            {
                returnList.add("No other eligible drivers.");
            }
            return returnList;
        }
    }
    
    private static List<String> populateEventClassSizeList(List<Object[]> query, int places)
    {
        if(query.size() == 0)
        {
            return null;
        }
        
        List<String> returnList = new ArrayList();
        
        try
        {
            for(int x = 0; x < places; x++)
            {
                returnList.add(query.get(x)[1] + " with " + query.get(x)[0] + " participants at " + query.get(x)[2] + " " + webFormat.format(query.get(x)[3]) + ".");
            }
            return returnList;
        }
        catch(IndexOutOfBoundsException e)
        {
            while(returnList.size() < places)
            {
                returnList.add("No other eligible drivers.");
            }
            return returnList;
        }
    }
    
    private static List<String> populateUniqueDriversList(List<Object[]> query, int places)
    {
        if(query.size() == 0)
        {
            return null;
        }
        
        List<String> returnList = new ArrayList();
        
        try
        {
            for(int x = 0; x < places; x++)
            {
                returnList.add(query.get(x)[1] + " with " + query.get(x)[0] + " unique participants.");
            }
            return returnList;
        }
        catch(IndexOutOfBoundsException e)
        {
            while(returnList.size() < places)
            {
                returnList.add("No other eligible drivers.");
            }
            return returnList;
        }
    }
    
    private static List<String> populateClassJumper(List<Object[]> query, int places)
    {
        if(query.size() == 0)
        {
            return null;
        }
        
        List<String> returnList = new ArrayList();
        
        try
        {
            for(int x = 0; x < places; x++)
            {
                returnList.add(query.get(x)[1] + " participated in " + query.get(x)[0] + " different classes.");
            }
            return returnList;
        }
        catch(IndexOutOfBoundsException e)
        {
            while(returnList.size() < places)
            {
                returnList.add("No other eligible drivers.");
            }
            return returnList;
        }
    }
    
    private static List<String> populateDirtiestRun(List<Object[]> query, int places)
    {
        if(query.size() == 0)
        {
            return null;
        }
        
        List<String> returnList = new ArrayList();
        
        try
        {
            for(int x = 0; x < places; x++)
            {
                Events e = (Events)query.get(x)[2];
                returnList.add(query.get(x)[1] + " hit  " + query.get(x)[0] + " cones on run #" + query.get(x)[3] + " at " + e.getEventLocation() + " " + webFormat.format(e.getEventDate()) + ".");
            }
            return returnList;
        }
        catch(IndexOutOfBoundsException e)
        {
            while(returnList.size() < places)
            {
                returnList.add("No other eligible drivers.");
            }
            return returnList;
        }
    }

    public List<List<String>> getAwards2016() {
        return awards2016;
    }

    public void setAwards2016(List<List<String>> awards2016) {
        this.awards2016 = awards2016;
    }

    public List<List<String>> getAwards2015() {
        return awards2015;
    }

    public void setAwards2015(List<List<String>> awards2015) {
        this.awards2015 = awards2015;
    }

    public List<List<String>> getAwards2014() {
        return awards2014;
    }

    public void setAwards2014(List<List<String>> awards2014) {
        this.awards2014 = awards2014;
    }

    public List<List<String>> getAwards2013() {
        return awards2013;
    }

    public void setAwards2013(List<List<String>> awards2013) {
        this.awards2013 = awards2013;
    }

    public List<Long> getStats2016() {
        return stats2016;
    }

    public void setStats2016(List<Long> stats2016) {
        this.stats2016 = stats2016;
    }

    public List<Long> getStats2015() {
        return stats2015;
    }

    public void setStats2015(List<Long> stats2015) {
        this.stats2015 = stats2015;
    }

    public List<Long> getStats2014() {
        return stats2014;
    }

    public void setStats2014(List<Long> stats2014) {
        this.stats2014 = stats2014;
    }

    public List<Long> getStats2013() {
        return stats2013;
    }

    public void setStats2013(List<Long> stats2013) {
        this.stats2013 = stats2013;
    }

    

    
   
    
    
}
