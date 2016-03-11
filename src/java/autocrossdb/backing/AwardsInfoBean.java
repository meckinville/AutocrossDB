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
import java.util.Date;
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
    
    private List<List<String>> individualAwards2016;
    private List<List<String>> classAwards2016;
    private List<List<String>> eventAwards2016;
    private List<Long> stats2016;
    
    private List<List<String>> individualAwards2015;
    private List<List<String>> classAwards2015;
    private List<List<String>> eventAwards2015;
    private List<Long> stats2015;
    
    private List<List<String>> individualAwards2014;
    private List<List<String>> classAwards2014;
    private List<List<String>> eventAwards2014;
    private List<Long> stats2014;
    
    private List<List<String>> individualAwards2013;
    private List<List<String>> classAwards2013;
    private List<List<String>> eventAwards2013;
    private List<Long> stats2013;
    
    
    
    @PersistenceContext
    private EntityManager em;
    
    @PostConstruct
    public void init()
    {   
        stats2014 = getStatsForYear(2014);
        stats2015 = getStatsForYear(2015);
        stats2016 = getStatsForYear(2016);
        
        individualAwards2016 = getIndividualAwardsForYear(2016);
        individualAwards2015 = getIndividualAwardsForYear(2015);
        individualAwards2014 = getIndividualAwardsForYear(2014);
        individualAwards2013 = getIndividualAwardsForYear(2013);
        
        classAwards2016 = getClassAwardsForYear(2016);
        classAwards2015 = getClassAwardsForYear(2015);
        classAwards2014 = getClassAwardsForYear(2014);
        classAwards2013 = getClassAwardsForYear(2013);
        
        eventAwards2016 = getEventAwardsForYear(2016);
        eventAwards2015 = getEventAwardsForYear(2015);
        eventAwards2014 = getEventAwardsForYear(2014);
        eventAwards2013 = getEventAwardsForYear(2013);
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
    
    public List<List<String>> getIndividualAwardsForYear(int year)
    {
        List<List<String>> awards = new ArrayList<List<String>>();
        
        Calendar beginYear = Calendar.getInstance();
        Calendar endYear = Calendar.getInstance();
        beginYear.set(year, Calendar.JANUARY, 1);
        endYear.set(year, Calendar.DECEMBER, 31);
        List<Object[]> eventsAttendedQuery = em.createQuery("SELECT count(r.runDriverName), r.runDriverName from Runs r where r.runEventUrl.eventDate > :begin AND r.runEventUrl.eventDate < :end and r.runNumber = 1 group by r.runDriverName having count(r.runDriverName) > 3 order by count(r.runDriverName) desc").setParameter("begin", beginYear.getTime()).setParameter("end", endYear.getTime()).getResultList();
        List<Object[]> objectQuery = em.createQuery("SELECT count(e.eventRawWinner), e.eventRawWinner from Events e where e.eventDate > :begin AND e.eventDate < :end group by e.eventRawWinner order by count(e.eventRawWinner) desc ").setParameter("begin", beginYear.getTime()).setParameter("end", endYear.getTime()).getResultList();
        
        //add raw event winners and raw percent wins
        awards.add(populateAward(objectQuery, "[1] with [0] raw time wins.", 1));
        awards.add(populateRawPercentList(calculatePercent(eventsAttendedQuery, objectQuery), PLACES_TO_POPULATE));
        
        objectQuery = em.createQuery("SELECT count(e.eventPaxWinner), e.eventPaxWinner from Events e where e.eventDate > :begin AND e.eventDate < :end group by e.eventPaxWinner order by count(e.eventPaxWinner) desc ").setParameter("begin", beginYear.getTime()).setParameter("end", endYear.getTime()).getResultList();
        //add pax event winners and pax percent wins
        awards.add(populateAward(objectQuery, "[1] with [0] pax time wins.", 1));
        awards.add(populatePaxPercentList(calculatePercent(eventsAttendedQuery, objectQuery), PLACES_TO_POPULATE));
        
        objectQuery = em.createQuery("SELECT sum(r.runCones), r.runDriverName from Runs r where r.runEventUrl.eventDate > :begin AND r.runEventUrl.eventDate < :end group by r.runDriverName order by sum(r.runCones) desc").setParameter("begin", beginYear.getTime()).setParameter("end", endYear.getTime()).getResultList();
        //add most cones total and most cones per event
        awards.add(populateAward(objectQuery, "[1] with [0] total cones hit.", 1));
        awards.add(populateConesPercentList(calculatePercent(eventsAttendedQuery, objectQuery), PLACES_TO_POPULATE));
        
        //add most events attended
        awards.add(populateAward(eventsAttendedQuery, "[1] with [0] events attended.", 1));
        
        objectQuery = em.createQuery("SELECT count(r.runDriverName), r.runDriverName from Runs r where r.runEventUrl.eventDate > :begin AND r.runEventUrl.eventDate < :end group by r.runDriverName order by count(r.runDriverName) desc").setParameter("begin", beginYear.getTime()).setParameter("end", endYear.getTime()).getResultList();
        //add most runs taken
        awards.add(populateAward(objectQuery, "[1] with [0] runs taken.", 1));
        
        objectQuery = em.createQuery("SELECT count(distinct r.runClassName.className), r.runDriverName from Runs r where r.runNumber = 1 AND r.runEventUrl.eventDate > :begin AND r.runEventUrl.eventDate < :end group by r.runDriverName order by count(distinct r.runClassName.className) desc").setParameter("begin", beginYear.getTime()).setParameter("end", endYear.getTime()).getResultList();
        //add class jumper
        awards.add(populateAward(objectQuery, "[1] participated in [0] different classes.", 1));
        
        objectQuery = em.createQuery("SELECT max(r.runCones), r.runDriverName, r.runEventUrl.eventDate, r.runEventUrl.eventLocation, r.runNumber from Runs r where r.runEventUrl.eventDate > :begin AND r.runEventUrl.eventDate < :end and r.runOffcourse = 'N' group by r.runId order by max(r.runCones) desc").setParameter("begin", beginYear.getTime()).setParameter("end", endYear.getTime()).getResultList();
        //add dirtiest run
        awards.add(populateAward(objectQuery, "[1] hit [0] cones on run #[4] at [3] [2].", 4));
        
        return awards;
    }
    
    public List<List<String>> getEventAwardsForYear(int year)
    {
        List<List<String>> awards = new ArrayList<List<String>>();
        Calendar beginYear = Calendar.getInstance();
        Calendar endYear = Calendar.getInstance();
        beginYear.set(year, Calendar.JANUARY, 1);
        endYear.set(year, Calendar.DECEMBER, 31);
        
        List<Object[]> objectQuery = em.createQuery("SELECT count(r.runDriverName), r.runEventUrl.eventLocation, r.runEventUrl.eventDate, r.runEventUrl.eventClubName from Runs r where r.runNumber = 1 and r.runEventUrl.eventDate < :end and r.runEventUrl.eventDate > :begin group by r.runEventUrl order by count(r.runDriverName) desc").setParameter("begin", beginYear.getTime()).setParameter("end", endYear.getTime()).getResultList();
        //add biggest event
        awards.add(populateAward(objectQuery, "[0] participants at [1] [2] with [3].", 3));
        
        objectQuery = em.createQuery("SELECT max(r.runNumber), r.runEventUrl.eventLocation, r.runEventUrl.eventClubName, r.runEventUrl.eventDate from Runs r where r.runEventUrl.eventDate > :begin AND r.runEventUrl.eventDate < :end group by r.runEventUrl order by max(r.runNumber) desc").setParameter("begin", beginYear.getTime()).setParameter("end", endYear.getTime()).getResultList();
        //add most runs at an event
        awards.add(populateAward(objectQuery, "[0] runs at [1] [3] with [2].", 3));
        
        objectQuery = em.createQuery("SELECT sum(r.runCones), r.runEventUrl.eventLocation, r.runEventUrl.eventDate, r.runEventUrl.eventClubName from Runs r where r.runEventUrl.eventDate < :end and r.runEventUrl.eventDate > :begin group by r.runEventUrl order by sum(r.runCones) desc").setParameter("begin", beginYear.getTime()).setParameter("end", endYear.getTime()).getResultList();
        //add cone carnage
        awards.add(populateAward(objectQuery, "[0] cones hit at [1] [2] with [3].", 3));
        
        objectQuery = em.createQuery("SELECT cast(sum(r.runCones) as float) / cast(count(distinct r.runDriverName) as float) as average, r.runEventUrl.eventLocation, r.runEventUrl.eventDate, r.runEventUrl.eventClubName from Runs r where r.runEventUrl.eventDate < :end and r.runEventUrl.eventDate > :begin group by r.runEventUrl order by average desc").setParameter("begin", beginYear.getTime()).setParameter("end", endYear.getTime()).getResultList();
        //add average cones per driver
        awards.add(populateAward(objectQuery, "[0] cones hit per driver at [1] [2] with [3].", 3));
        
        return awards;
    }
    
    public List<List<String>> getClassAwardsForYear(int year)
    {
        List<List<String>> awards = new ArrayList<List<String>>();
        Calendar beginYear = Calendar.getInstance();
        Calendar endYear = Calendar.getInstance();
        beginYear.set(year, Calendar.JANUARY, 1);
        endYear.set(year, Calendar.DECEMBER, 31);
        
        List<Object[]> objectQuery = em.createQuery("SELECT count(r.runDriverName), r.runClassName.className, r.runEventUrl.eventLocation, r.runEventUrl.eventDate, r.runEventUrl.eventClubName from Runs r where r.runEventUrl.eventDate > :begin AND r.runEventUrl.eventDate < :end and r.runNumber = 1 and r.runClassName.className != 'NS' group by r.runClassName, r.runEventUrl order by count(r.runDriverName) desc" ).setParameter("begin", beginYear.getTime()).setParameter("end", endYear.getTime()).getResultList();
        //add biggest class at event
        awards.add(populateAward(objectQuery, "[1] with [0] participants at [2] [3].", 3));
        
        objectQuery = em.createQuery("SELECT count(distinct r.runDriverName), r.runClassName.className from Runs r where r.runEventUrl.eventDate > :begin AND r.runEventUrl.eventDate < :end and r.runNumber = 1 and r.runClassName.className != 'NS' group by r.runClassName order by count(distinct r.runDriverName) desc").setParameter("begin", beginYear.getTime()).setParameter("end", endYear.getTime()).getResultList();
        //add most unique drivers in class for the year
        awards.add(populateAward(objectQuery, "[1] with [0] unique participants.", 1));
         
        //highest average participation
        objectQuery = em.createQuery("SELECT count(r), r.runClassName.className from Runs r where r.runNumber = 1 and r.runEventUrl.eventDate > :begin AND r.runEventUrl.eventDate < :end group by r.runClassName order by count(r) desc").setParameter("begin", beginYear.getTime()).setParameter("end", endYear.getTime()).getResultList();
        long totalEvents = em.createQuery("SELECT e FROM Events e where e.eventDate > :begin AND e.eventDate < :end").setParameter("begin", beginYear.getTime()).setParameter("end", endYear.getTime()).getResultList().size();
        List<Object[]> tempQuery = new ArrayList();
        for(Object[] o : objectQuery)
        {
            Object[] temp = new Object[2];
            temp[0] = new Double((long)o[0]) / totalEvents;
            temp[1] = o[1];
            tempQuery.add(temp);
        }
        awards.add(populateAward(tempQuery, "[1] had an average of [0] participants.", 1));

        return awards;
    }

    private static List<String> populateAward(List<Object[]> query, String awardText, int replace)
    {
        List<String> returnList = new ArrayList();
        String originalAwardText = awardText;
        try
        {
            for(int i = 0; i < PLACES_TO_POPULATE; i++)
            {
                for(int x = 0; x <= replace; x++)
                {
                    if(query.get(i)[x] instanceof Date)
                    {
                        awardText = awardText.replace("[" + String.valueOf(x) + "]", webFormat.format(query.get(i)[x]));
                    }
                    else if(query.get(i)[x] instanceof Double)
                    {
                        awardText = awardText.replace("[" + String.valueOf(x) + "]", String.format("%.3f", query.get(i)[x]));
                    }
                    else
                    {
                        awardText = awardText.replace("[" + String.valueOf(x) + "]", query.get(i)[x].toString());
                    }
                    
                }
                returnList.add(awardText);
                awardText = originalAwardText;
            }
        }
        catch(IndexOutOfBoundsException e)
        {
            while(returnList.size() < PLACES_TO_POPULATE)
            {
                returnList.add("No others eligible.");
            }
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
                returnList.add("No others eligible.");
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
                returnList.add("No others eligible.");
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
                returnList.add("No others eligible.");
            }
            return returnList;
        }
    }
    
    

    public List<List<String>> getIndividualAwards2016() {
        return individualAwards2016;
    }

    public void setIndividualAwards2016(List<List<String>> individualAwards2016) {
        this.individualAwards2016 = individualAwards2016;
    }

    public List<List<String>> getClassAwards2016() {
        return classAwards2016;
    }

    public void setClassAwards2016(List<List<String>> classAwards2016) {
        this.classAwards2016 = classAwards2016;
    }

    public List<List<String>> getEventAwards2016() {
        return eventAwards2016;
    }

    public void setEventAwards2016(List<List<String>> eventAwards2016) {
        this.eventAwards2016 = eventAwards2016;
    }

    public List<List<String>> getIndividualAwards2015() {
        return individualAwards2015;
    }

    public void setIndividualAwards2015(List<List<String>> individualAwards2015) {
        this.individualAwards2015 = individualAwards2015;
    }

    public List<List<String>> getClassAwards2015() {
        return classAwards2015;
    }

    public void setClassAwards2015(List<List<String>> classAwards2015) {
        this.classAwards2015 = classAwards2015;
    }

    public List<List<String>> getEventAwards2015() {
        return eventAwards2015;
    }

    public void setEventAwards2015(List<List<String>> eventAwards2015) {
        this.eventAwards2015 = eventAwards2015;
    }

    public List<List<String>> getIndividualAwards2014() {
        return individualAwards2014;
    }

    public void setIndividualAwards2014(List<List<String>> individualAwards2014) {
        this.individualAwards2014 = individualAwards2014;
    }

    public List<List<String>> getClassAwards2014() {
        return classAwards2014;
    }

    public void setClassAwards2014(List<List<String>> classAwards2014) {
        this.classAwards2014 = classAwards2014;
    }

    public List<List<String>> getEventAwards2014() {
        return eventAwards2014;
    }

    public void setEventAwards2014(List<List<String>> eventAwards2014) {
        this.eventAwards2014 = eventAwards2014;
    }

    public List<List<String>> getIndividualAwards2013() {
        return individualAwards2013;
    }

    public void setIndividualAwards2013(List<List<String>> individualAwards2013) {
        this.individualAwards2013 = individualAwards2013;
    }

    public List<List<String>> getClassAwards2013() {
        return classAwards2013;
    }

    public void setClassAwards2013(List<List<String>> classAwards2013) {
        this.classAwards2013 = classAwards2013;
    }

    public List<List<String>> getEventAwards2013() {
        return eventAwards2013;
    }

    public void setEventAwards2013(List<List<String>> eventAwards2013) {
        this.eventAwards2013 = eventAwards2013;
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
