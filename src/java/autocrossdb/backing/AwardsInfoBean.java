/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package autocrossdb.backing;

import autocrossdb.component.Award;
import autocrossdb.component.AwardHelper;
import autocrossdb.entities.Cars;
import autocrossdb.util.AwardInfoUtil;
import autocrossdb.util.CarUtil;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author rmcconville
 */
@ManagedBean(name="awardsInfo")
@SessionScoped
public class AwardsInfoBean implements Serializable
{
    private static final DateFormat webFormat = new SimpleDateFormat("MM-dd-yyyy");
    
    private static final int PLACES_TO_POPULATE = 100;
    
    private List<String> filterList;
    private String driverToFind;
    private String foundDriver;
    
    private long progress;
    
    private List<Award> xindividualAwards2016;
    
    private List<Award> individualAwards2016;
    private List<Award> classAwards2016;
    private List<Award> eventAwards2016;
    private List<Award> carAwards2016;
    private List<Long> stats2016;
    
    private List<Award> individualAwards2015;
    private List<Award> classAwards2015;
    private List<Award> eventAwards2015;
    private List<Award> carAwards2015;
    private List<Long> stats2015;
    
    private List<Award> individualAwards2014;
    private List<Award> classAwards2014;
    private List<Award> eventAwards2014;
    private List<Award> carAwards2014;
    private List<Long> stats2014;
    
    
    private Award selectedAward;

    
    @PersistenceContext
    private EntityManager em;
    
    @PostConstruct
    public void init()
    {   
        filterList = new ArrayList();
        filterList.add("CFRSCCA");
        filterList.add("MARTINSCC");
        filterList.add("BUCCANEER");
        filterList.add("GULFCOAST");
        
        stats2014 = getStatsForYear(2014);
        stats2015 = getStatsForYear(2015);
        stats2016 = getStatsForYear(2016);
        
        individualAwards2016 = getIndividualAwardsForYear(2016);
        individualAwards2015 = getIndividualAwardsForYear(2015);
        individualAwards2014 = getIndividualAwardsForYear(2014);
        
        classAwards2016 = getClassAwardsForYear(2016);
        classAwards2015 = getClassAwardsForYear(2015);
        classAwards2014 = getClassAwardsForYear(2014);
        
        eventAwards2016 = getEventAwardsForYear(2016);
        eventAwards2015 = getEventAwardsForYear(2015);
        eventAwards2014 = getEventAwardsForYear(2014);
        
        carAwards2016 = getCarAwardsForYear(2016);
        carAwards2015 = getCarAwardsForYear(2015);
        carAwards2014 = getCarAwardsForYear(2014);
    }
    
    public void filterAwards()
    {
        progress = 0;
        stats2014 = getStatsForYear(2014);
        stats2015 = getStatsForYear(2015);
        stats2016 = getStatsForYear(2016);
        progress = 20;
        individualAwards2016 = getIndividualAwardsForYear(2016);
        individualAwards2015 = getIndividualAwardsForYear(2015);
        individualAwards2014 = getIndividualAwardsForYear(2014);
        progress = 40;
        classAwards2016 = getClassAwardsForYear(2016);
        classAwards2015 = getClassAwardsForYear(2015);
        classAwards2014 = getClassAwardsForYear(2014);
        progress = 60;
        eventAwards2016 = getEventAwardsForYear(2016);
        eventAwards2015 = getEventAwardsForYear(2015);
        eventAwards2014 = getEventAwardsForYear(2014);
        progress = 80;
        carAwards2016 = getCarAwardsForYear(2016);
        carAwards2015 = getCarAwardsForYear(2015);
        carAwards2014 = getCarAwardsForYear(2014);
        progress = 100;
    }
    
    private List<Long> getStatsForYear(int year)
    {
        List<Long> stats = new ArrayList<Long>();
        
        Calendar beginYear = Calendar.getInstance();
        Calendar endYear = Calendar.getInstance();
        beginYear.set(year, Calendar.JANUARY, 1);
        endYear.set(year, Calendar.DECEMBER, 31);
        
        stats.add((long)em.createQuery("SELECT e FROM Events e where e.eventDate > :begin AND e.eventDate < :end and e.eventClubName in :clubList").setParameter("begin", beginYear.getTime()).setParameter("end", endYear.getTime()).setParameter("clubList", filterList).getResultList().size());
        stats.add((long)em.createQuery("SELECT count(distinct r.runDriverName) FROM Runs r where r.runEventUrl.eventDate > :begin AND r.runEventUrl.eventDate < :end and r.runEventUrl.eventClubName  in :clubList").setParameter("begin", beginYear.getTime()).setParameter("end", endYear.getTime()).setParameter("clubList", filterList).getResultList().get(0));
        return stats;
    }
    
    public List<Award> getIndividualAwardsForYear(int year)
    {
        List<Award> awards = new ArrayList();
        
        Calendar beginYear = Calendar.getInstance();
        Calendar endYear = Calendar.getInstance();
        beginYear.set(year, Calendar.JANUARY, 1);
        endYear.set(year, Calendar.DECEMBER, 31);
        List<Object[]> eventsAttendedQuery = em.createQuery("SELECT count(r.runDriverName), r.runDriverName from Runs r where r.runEventUrl.eventDate > :begin AND r.runEventUrl.eventDate < :end and r.runNumber = 1 and r.runEventUrl.eventClubName in :clubList group by r.runDriverName having count(r.runDriverName) > 3 order by count(r.runDriverName) desc").setParameter("begin", beginYear.getTime()).setParameter("end", endYear.getTime()).setParameter("clubList", filterList).getResultList();
        List<Object[]> objectQuery = em.createQuery("SELECT count(e.eventRawWinner), e.eventRawWinner from Events e where e.eventDate > :begin AND e.eventDate < :end and e.eventClubName in :clubList group by e.eventRawWinner order by count(e.eventRawWinner) desc ").setParameter("begin", beginYear.getTime()).setParameter("end", endYear.getTime()).setParameter("clubList", filterList).getResultList();
        
        //add raw event winners and raw percent wins
        awards.add(populateAward(objectQuery, "Most Raw Wins", "[1] with [0] raw time wins.", AwardInfoUtil.MOST_RAW_WINS_INFO, 1));
        awards.add(populateAward(calculatePercent(eventsAttendedQuery, objectQuery), "Highest Percent Raw Wins", "[name] had top raw time at [value]% of attended events.", AwardInfoUtil.HIGHEST_RAW_PERCENT_INFO));
        
        objectQuery = em.createQuery("SELECT count(e.eventPaxWinner), e.eventPaxWinner from Events e where e.eventDate > :begin AND e.eventDate < :end and e.eventClubName in :clubList group by e.eventPaxWinner order by count(e.eventPaxWinner) desc ").setParameter("begin", beginYear.getTime()).setParameter("end", endYear.getTime()).setParameter("clubList", filterList).getResultList();
        //add pax event winners and pax percent wins
        awards.add(populateAward(objectQuery, "Most Pax Wins", "[1] with [0] pax time wins.", AwardInfoUtil.MOST_PAX_WINS_INFO, 1));
        awards.add(populateAward(calculatePercent(eventsAttendedQuery, objectQuery), "Highest Percent Pax Wins", "[name] had top pax time at [value]% of attended events.", AwardInfoUtil.HIGHEST_PAX_PERCENT_INFO));
        
        objectQuery = em.createQuery("SELECT sum(r.runCones), r.runDriverName from Runs r where r.runEventUrl.eventDate > :begin AND r.runEventUrl.eventDate < :end and r.runEventUrl.eventClubName in :clubList group by r.runDriverName order by sum(r.runCones) desc").setParameter("begin", beginYear.getTime()).setParameter("end", endYear.getTime()).setParameter("clubList", filterList).getResultList();
        //add most cones total and most cones per event
        awards.add(populateAward(objectQuery, "Most Cones Hit", "[1] with [0] total cones hit.", AwardInfoUtil.MOST_CONES_HIT_INFO, 1));
        awards.add(populateAward(calculatePercent(eventsAttendedQuery, objectQuery), "Most Cones Hit Per Event Average", "[name] hit [value] cones per attended event.", AwardInfoUtil.MOST_CONES_HIT_AVG_INFO));
        
        //add most events attended
        awards.add(populateAward(eventsAttendedQuery, "Most Events Attended", "[1] with [0] events attended.", AwardInfoUtil.MOST_EVENTS_ATTENDED_INFO, 1));
        
        objectQuery = em.createQuery("SELECT count(r.runDriverName), r.runDriverName from Runs r where r.runEventUrl.eventDate > :begin AND r.runEventUrl.eventDate < :end and r.runEventUrl.eventClubName in :clubList group by r.runDriverName order by count(r.runDriverName) desc").setParameter("begin", beginYear.getTime()).setParameter("end", endYear.getTime()).setParameter("clubList", filterList).getResultList();
        //add most runs taken
        awards.add(populateAward(objectQuery, "Most Runs Taken", "[1] with [0] runs taken.", AwardInfoUtil.MOST_RUNS_TAKEN_INFO, 1));
        
        objectQuery = em.createQuery("SELECT count(distinct r.runClassName.className), r.runDriverName from Runs r where r.runNumber = 1 AND r.runEventUrl.eventDate > :begin AND r.runEventUrl.eventDate < :end and r.runEventUrl.eventClubName in :clubList group by r.runDriverName order by count(distinct r.runClassName.className) desc").setParameter("begin", beginYear.getTime()).setParameter("end", endYear.getTime()).setParameter("clubList", filterList).getResultList();
        //add class jumper
        awards.add(populateAward(objectQuery, "Class Jumper", "[1] participated in [0] different classes.", AwardInfoUtil.CLASS_JUMPER_INFO, 1));
        
        objectQuery = em.createQuery("SELECT max(r.runCones), r.runDriverName, r.runEventUrl.eventDate, r.runEventUrl.eventLocation, r.runNumber from Runs r where r.runEventUrl.eventDate > :begin AND r.runEventUrl.eventDate < :end and r.runEventUrl.eventClubName in :clubList and r.runOffcourse = 'N' group by r.runId order by max(r.runCones) desc").setParameter("begin", beginYear.getTime()).setParameter("end", endYear.getTime()).setParameter("clubList", filterList).getResultList();
        //add dirtiest run
        awards.add(populateAward(objectQuery, "Dirtiest Run", "[1] hit [0] cones on run #[4] at [3] [2].", AwardInfoUtil.DIRTIEST_RUN_INFO, 4));
        
        objectQuery = em.createQuery("SELECT min(r.runTime)-min(r.runTime - (2 * r.runCones)) as diff, r.runDriverName from Runs r where r.runEventUrl.eventDate > :begin and r.runEventUrl.eventDate < :end and r.runEventUrl.eventClubName in :clubList and r.runOffcourse = 'N' group by r.runEventUrl, r.runDriverName having min(r.runTime)-min(r.runTime - (2 * r.runCones)) > 0 order by r.runDriverName, diff").setParameter("begin", beginYear.getTime()).setParameter("end", endYear.getTime()).setParameter("clubList", filterList).getResultList();
        //add most time lost from cones
        Map<String, AwardHelper> lostTimeMap = new TreeMap();
        for(Object[] o : objectQuery)
        {
            if(lostTimeMap.containsKey(o[1].toString()))
            {
                AwardHelper tempHelper = lostTimeMap.get(o[1].toString());
                tempHelper.setValue(tempHelper.getValue() + (double)o[0]);
                tempHelper.setCount(tempHelper.getCount() + 1);
                lostTimeMap.put(o[1].toString(), tempHelper);
            }
            else
            {
                AwardHelper tempHelper = new AwardHelper((double)o[0], o[1].toString());
                lostTimeMap.put(o[1].toString(), tempHelper);
            }
        }
        List<AwardHelper> lostTimeList = new ArrayList();
        for(String key : lostTimeMap.keySet())
        {
            if(lostTimeMap.get(key).getCount() > 2)
            {
                lostTimeList.add(lostTimeMap.get(key));
            }
            
        }
        
        Collections.sort(lostTimeList, Collections.reverseOrder());
        awards.add(populateAward(lostTimeList, "Most Time Lost from Cones", "[name] coned away [count] best runs for a loss of [value] seconds.", AwardInfoUtil.TIME_LOST_INFO));
        return awards;
    }
    public List<Award> getEventAwardsForYear(int year)
    {
        List<Award> awards = new ArrayList();
        Calendar beginYear = Calendar.getInstance();
        Calendar endYear = Calendar.getInstance();
        beginYear.set(year, Calendar.JANUARY, 1);
        endYear.set(year, Calendar.DECEMBER, 31);
        
        List<Object[]> objectQuery = em.createQuery("SELECT count(r.runDriverName), r.runEventUrl.eventLocation, r.runEventUrl.eventDate, r.runEventUrl.eventClubName from Runs r where r.runNumber = 1 and r.runEventUrl.eventDate < :end and r.runEventUrl.eventDate > :begin and r.runEventUrl.eventClubName in :clubList group by r.runEventUrl order by count(r.runDriverName) desc").setParameter("begin", beginYear.getTime()).setParameter("end", endYear.getTime()).setParameter("clubList", filterList).getResultList();
        //add biggest event
        awards.add(populateAward(objectQuery, "Largest Event Attendance", "[0] participants at [1] [2] with [3].", AwardInfoUtil.LARGEST_EVENT_INFO,  3));
        
        objectQuery = em.createQuery("SELECT count(r.runDriverName), r.runEventUrl.eventLocation, r.runEventUrl.eventDate, r.runEventUrl.eventClubName from Runs r where r.runNumber = 1 and r.runEventUrl.eventDate < :end and r.runEventUrl.eventDate > :begin and r.runEventUrl.eventClubName in :clubList group by r.runEventUrl order by count(r.runDriverName) asc").setParameter("begin", beginYear.getTime()).setParameter("end", endYear.getTime()).setParameter("clubList", filterList).getResultList();
        //add smallest event
        awards.add(populateAward(objectQuery, "Smallest Event Attendance", "[0] participants at [1] [2] with [3].", AwardInfoUtil.SMALLEST_EVENT_INFO, 3));
        
        objectQuery = em.createQuery("SELECT max(r.runNumber), r.runEventUrl.eventLocation, r.runEventUrl.eventClubName, r.runEventUrl.eventDate from Runs r where r.runEventUrl.eventDate > :begin AND r.runEventUrl.eventDate < :end and r.runEventUrl.eventClubName in :clubList group by r.runEventUrl order by max(r.runNumber) desc").setParameter("begin", beginYear.getTime()).setParameter("end", endYear.getTime()).setParameter("clubList", filterList).getResultList();
        //add most runs at an event
        awards.add(populateAward(objectQuery, "Most Runs at Event", "[0] runs at [1] [3] with [2].", AwardInfoUtil.MOST_RUNS_AT_EVENT_INFO,  3));
        
        objectQuery = em.createQuery("SELECT sum(r.runCones), r.runEventUrl.eventLocation, r.runEventUrl.eventDate, r.runEventUrl.eventClubName from Runs r where r.runEventUrl.eventDate < :end and r.runEventUrl.eventDate > :begin and r.runEventUrl.eventClubName in :clubList group by r.runEventUrl order by sum(r.runCones) desc").setParameter("begin", beginYear.getTime()).setParameter("end", endYear.getTime()).setParameter("clubList", filterList).getResultList();
        //add cone carnage
        awards.add(populateAward(objectQuery, "Cone Carnage", "[0] cones hit at [1] [2] with [3].", AwardInfoUtil.CONE_CARNAGE_INFO, 3));
        
        objectQuery = em.createQuery("SELECT cast(sum(r.runCones) as float) / cast(count(distinct r.runDriverName) as float) as average, r.runEventUrl.eventLocation, r.runEventUrl.eventDate, r.runEventUrl.eventClubName from Runs r where r.runEventUrl.eventDate < :end and r.runEventUrl.eventDate > :begin and r.runEventUrl.eventClubName in :clubList group by r.runEventUrl order by average desc").setParameter("begin", beginYear.getTime()).setParameter("end", endYear.getTime()).setParameter("clubList", filterList).getResultList();
        //add average cones per driver
        awards.add(populateAward(objectQuery, "Average Cones Per Driver", "[0] cones hit per driver at [1] [2] with [3].", AwardInfoUtil.AVG_CONES_PER_DRIVER_INFO, 3));
        
        objectQuery = em.createQuery("SELECT count(*), r.runEventUrl.eventLocation, r.runEventUrl.eventClubName, r.runEventUrl.eventDate FROM Runs r WHERE r.runEventUrl.eventDate < :end and r.runEventUrl.eventDate > :begin and r.runOffcourse = 'Y' and r.runEventUrl.eventClubName in :clubList group by r.runEventUrl order by count(*) desc").setParameter("begin", beginYear.getTime()).setParameter("end", endYear.getTime()).setParameter("clubList", filterList).getResultList();
        //add most offcourses
        awards.add(populateAward(objectQuery, "Most Confusing Course", "[0] offcourse calls at [1] [3] with [2].", AwardInfoUtil.MOST_CONFUSING_COURSE_INFO, 3));
        
        objectQuery = em.createQuery("SELECT count(*), r.runEventUrl.eventLocation, r.runEventUrl.eventClubName, r.runEventUrl.eventDate from Runs r where r.runEventUrl.eventDate < :end and r.runEventUrl.eventDate > :begin and r.runNumber = 1 and r.runClassName.className = 'NS' and r.runEventUrl.eventClubName in :clubList group by r.runEventUrl order by count(*) desc ").setParameter("begin", beginYear.getTime()).setParameter("end", endYear.getTime()).setParameter("clubList", filterList).getResultList();
        //add novice invasion
        awards.add(populateAward(objectQuery, "Novice Invasion", "[0] novices attended [1] [3] with [2].", AwardInfoUtil.NOVICE_INVASION_INFO, 3));
        
        return awards;
    }
    
    public List<Award> getClassAwardsForYear(int year)
    {
        List<Award> awards = new ArrayList();
        Calendar beginYear = Calendar.getInstance();
        Calendar endYear = Calendar.getInstance();
        beginYear.set(year, Calendar.JANUARY, 1);
        endYear.set(year, Calendar.DECEMBER, 31);
        
        List<Object[]> objectQuery = em.createQuery("SELECT count(r.runDriverName), r.runClassName.className, r.runEventUrl.eventLocation, r.runEventUrl.eventDate, r.runEventUrl.eventClubName from Runs r where r.runEventUrl.eventDate > :begin AND r.runEventUrl.eventDate < :end and r.runNumber = 1 and r.runEventUrl.eventClubName in :clubList and r.runClassName.className != 'NS' group by r.runClassName, r.runEventUrl order by count(r.runDriverName) desc" ).setParameter("begin", beginYear.getTime()).setParameter("end", endYear.getTime()).setParameter("clubList", filterList).getResultList();
        //add biggest class at event
        awards.add(populateAward(objectQuery, "Largest Class Attendance", "[1] with [0] participants at [2] [3].", AwardInfoUtil.LARGEST_CLASS_ATTENDANCE_INFO, 3));
        
        objectQuery = em.createQuery("SELECT count(distinct r.runDriverName), r.runClassName.className from Runs r where r.runEventUrl.eventDate > :begin AND r.runEventUrl.eventDate < :end and r.runNumber = 1 and r.runClassName.className != 'NS' and r.runEventUrl.eventClubName in :clubList group by r.runClassName order by count(distinct r.runDriverName) desc").setParameter("begin", beginYear.getTime()).setParameter("end", endYear.getTime()).setParameter("clubList", filterList).getResultList();
        //add most unique drivers in class for the year
        awards.add(populateAward(objectQuery, "Most Unique Drivers in Class", "[1] with [0] unique participants.", AwardInfoUtil.MOST_UNIQUE_DRIVERS_CLASS_INFO, 1));
         
        //highest average participation
        objectQuery = em.createQuery("SELECT count(r), r.runClassName.className from Runs r where r.runNumber = 1 and r.runEventUrl.eventDate > :begin AND r.runEventUrl.eventDate < :end and r.runClassName.className != 'NS' and r.runEventUrl.eventClubName in :clubList group by r.runClassName order by count(r) desc").setParameter("begin", beginYear.getTime()).setParameter("end", endYear.getTime()).setParameter("clubList", filterList).getResultList();
        long totalEvents = em.createQuery("SELECT e FROM Events e where e.eventDate > :begin AND e.eventDate < :end and e.eventClubName in :clubList").setParameter("begin", beginYear.getTime()).setParameter("end", endYear.getTime()).setParameter("clubList", filterList).getResultList().size();
        List<Object[]> tempQuery = new ArrayList();
        for(Object[] o : objectQuery)
        {
            Object[] temp = new Object[2];
            temp[0] = new Double((long)o[0]) / totalEvents;
            temp[1] = o[1];
            tempQuery.add(temp);
        }
        awards.add(populateAward(tempQuery, "Highest Average Participation", "[1] had an average of [0] participants.", AwardInfoUtil.HIGHEST_AVG_PARTICIPATION_INFO, 1));
        
        //dirtiest class
        objectQuery = em.createQuery("SELECT cast(sum(r.runCones) as float) / cast(count(distinct r.runDriverName) as float) as avgCones, r.runClassName.className, r.runEventUrl.eventUrl from Runs r where r.runEventUrl.eventDate > :begin AND r.runEventUrl.eventDate < :end and r.runEventUrl.eventClubName in :clubList group by r.runClassName having count(distinct r.runDriverName) > 2 order by avgCones desc").setParameter("begin", beginYear.getTime()).setParameter("end", endYear.getTime()).setParameter("clubList", filterList).getResultList();
        awards.add(populateAward(objectQuery, "Dirtiest Class", "[1] hit [0] cones per driver per event.", AwardInfoUtil.DIRTIEST_CLASS_INFO, 2));
        return awards;
    }
    
    public List<Award> getCarAwardsForYear(int year)
    {
        List<Award> awards = new ArrayList();
        Calendar beginYear = Calendar.getInstance();
        Calendar endYear = Calendar.getInstance();
        beginYear.set(year, Calendar.JANUARY, 1);
        endYear.set(year, Calendar.DECEMBER, 31);
        
        List<Object[]> objectQuery = em.createQuery("SELECT min(r.runTime), r.runDriverName, r.runCarName from Runs r where r.runOffcourse = 'N' AND r.runEventUrl.eventDate > :begin AND r.runEventUrl.eventDate < :end and r.runEventUrl.eventClubName in :clubList group by r.runEventUrl, r.runClassName" ).setParameter("begin", beginYear.getTime()).setParameter("end", endYear.getTime()).setParameter("clubList", filterList).getResultList();
        List<Cars> carList = em.createQuery("SELECT c FROM Cars c").getResultList();
        CarUtil carUtil = new CarUtil(carList);
        List<AwardHelper> classWinsAwards = carUtil.getClassWins(objectQuery);
        awards.add(populateAward(classWinsAwards, "Most Class Wins by Make", "[name] had [value] class wins.", AwardInfoUtil.MOST_CLASS_WINS_CARMAKE_INFO));
        
        List<String> query = em.createQuery("SELECT r.runCarName from Runs r WHERE r.runNumber = 1 AND r.runEventUrl.eventDate > :begin AND r.runEventUrl.eventDate < :end and r.runEventUrl.eventClubName in :clubList").setParameter("begin", beginYear.getTime()).setParameter("end", endYear.getTime()).setParameter("clubList", filterList).getResultList();
        List<AwardHelper> participationAward = carUtil.getParticipation(query);
        awards.add(populateAward(participationAward, "Highest Participation by Make", "[name] had [value] cars show up.", AwardInfoUtil.HIGHEST_ATTENDANCE_CARMAKE_INFO));
        
        return awards;
    }
    
    private static Award populateAward(List<Object[]> query, String awardHeader, String awardText, String awardInfo, int replace)
    {
        Award award = new Award(awardHeader, awardInfo);
        String originalAwardText = awardText;
        try
        {
            int i = 0;
            for(Object[] o : query)
            {
                for(int x = 0; x <= replace; x++)
                {
                    if(query.get(i)[x] instanceof Date)
                    {
                        awardText = awardText.replace("[" + String.valueOf(x) + "]", webFormat.format(query.get(i)[x]));
                    }
                    else if(query.get(i)[x] instanceof Double || query.get(i)[x] instanceof Float)
                    {
                        awardText = awardText.replace("[" + String.valueOf(x) + "]", String.format("%.3f", query.get(i)[x]));
                    }
                    else
                    {
                        awardText = awardText.replace("[" + String.valueOf(x) + "]", query.get(i)[x].toString());
                    }
                }
                award.add(awardText);
                awardText = originalAwardText;
                i++;
            }
            
            while(award.getAwardStrings().size() < 5)
            {
                award.add("No others eligible.");
            }
        }
        catch(IndexOutOfBoundsException e)
        {
            while(award.size() < PLACES_TO_POPULATE)
            {
                award.add("No others eligible.");
            }
        }
        return award;
    }
    
    private static Award populateAward(List<AwardHelper> award, String awardHeader, String awardText, String awardInfo)
    {
        Award returnAward = new Award(awardHeader, awardInfo);
        String originalAwardText = awardText;
        try
        {
            for(int i = 0; i < PLACES_TO_POPULATE; i++)
            {
                awardText = awardText.replace("[name]", award.get(i).getName());
                awardText = awardText.replace("[count]", Integer.toString(award.get(i).getCount()));
                if(awardText.contains("cones per attended event"))
                {
                    awardText = awardText.replace("[value]", String.format("%.3f" , award.get(i).getValue()/100));
                }
                else if(awardText.contains("class wins.") || awardText.contains("cars show up"))
                {
                    awardText = awardText.replace("[value]", String.format("%.0f" , award.get(i).getValue()));
                }
                else
                {
                    awardText = awardText.replace("[value]", String.format("%.1f" , award.get(i).getValue()));
                }
                returnAward.add(awardText);
                awardText = originalAwardText;
            }
        }
        catch(IndexOutOfBoundsException e)
        {
            while(returnAward.size() < PLACES_TO_POPULATE)
            {
                returnAward.add("No others eligible.");
            }
        }
        return returnAward;
        
    }

    private static List<AwardHelper> calculatePercent(List<Object[]> eventQuery, List<Object[]> statQuery)
    {

        Map<String, Number> firstMap = new TreeMap();
        Map<String, Number> map = new TreeMap();
        //go over the list of raw winners from events. add an entry to the map for each unique winner. the value for the key is the number of wins
        for(Object[] o : statQuery)
        {
            firstMap.put(String.valueOf(o[1]), new Long(String.valueOf(o[0])).doubleValue());
        }
        
        //go through the eventsAttended query. for each raw winner entry, we will divide their wins by their events attended. we then replace the value
        //in the map with this divided value. 
        for(Object[] o : eventQuery)
        {
            if(firstMap.containsKey(String.valueOf(o[1])))
            {
                double eventsAttended = new Long(String.valueOf(o[0])).doubleValue();
                double rawWins = firstMap.get(String.valueOf(o[1])).doubleValue();
                double percent = rawWins / eventsAttended;
                map.put(String.valueOf(o[1]), percent * 100);
            }
        }
        
        //we create a new award list that contains an award for each map key/value pair.
        List<AwardHelper> awardList = new ArrayList();
        Iterator<String> namesIt = map.keySet().iterator();
        Iterator<Number> percentsIt = map.values().iterator();
        while(namesIt.hasNext())
        {
            awardList.add(new AwardHelper(percentsIt.next().doubleValue(), namesIt.next()));
        }
        
        Collections.sort(awardList, Collections.reverseOrder());
        return awardList;
    }
    
    public void findAwardForDriver()
    {
        List<String> awardList = selectedAward.getAwardStrings();
        int iterator = 1;
        for(String s : awardList)
        {
            System.out.println(iterator);
            if(s.contains(driverToFind))
            {
                foundDriver = iterator + ". " + s;
                System.out.println("found the driver -- " + foundDriver);
                break;
            }
            else
            {
                iterator++;
            }
        }
    }
    
    public void onCompleteLoad()
    {
        progress = 0;
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

    public List<Award> getIndividualAwards2016() {
        return individualAwards2016;
    }

    public void setIndividualAwards2016(List<Award> individualAwards2016) {
        this.individualAwards2016 = individualAwards2016;
    }

    public List<Award> getClassAwards2016() {
        return classAwards2016;
    }

    public void setClassAwards2016(List<Award> classAwards2016) {
        this.classAwards2016 = classAwards2016;
    }

    public List<Award> getEventAwards2016() {
        return eventAwards2016;
    }

    public void setEventAwards2016(List<Award> eventAwards2016) {
        this.eventAwards2016 = eventAwards2016;
    }

    public List<Award> getCarAwards2016() {
        return carAwards2016;
    }

    public void setCarAwards2016(List<Award> carAwards2016) {
        this.carAwards2016 = carAwards2016;
    }

    public List<Award> getIndividualAwards2015() {
        return individualAwards2015;
    }

    public void setIndividualAwards2015(List<Award> individualAwards2015) {
        this.individualAwards2015 = individualAwards2015;
    }

    public List<Award> getClassAwards2015() {
        return classAwards2015;
    }

    public void setClassAwards2015(List<Award> classAwards2015) {
        this.classAwards2015 = classAwards2015;
    }

    public List<Award> getEventAwards2015() {
        return eventAwards2015;
    }

    public void setEventAwards2015(List<Award> eventAwards2015) {
        this.eventAwards2015 = eventAwards2015;
    }

    public List<Award> getCarAwards2015() {
        return carAwards2015;
    }

    public void setCarAwards2015(List<Award> carAwards2015) {
        this.carAwards2015 = carAwards2015;
    }

    public List<Award> getIndividualAwards2014() {
        return individualAwards2014;
    }

    public void setIndividualAwards2014(List<Award> individualAwards2014) {
        this.individualAwards2014 = individualAwards2014;
    }

    public List<Award> getClassAwards2014() {
        return classAwards2014;
    }

    public void setClassAwards2014(List<Award> classAwards2014) {
        this.classAwards2014 = classAwards2014;
    }

    public List<Award> getEventAwards2014() {
        return eventAwards2014;
    }

    public void setEventAwards2014(List<Award> eventAwards2014) {
        this.eventAwards2014 = eventAwards2014;
    }

    public List<Award> getCarAwards2014() {
        return carAwards2014;
    }

    public void setCarAwards2014(List<Award> carAwards2014) {
        this.carAwards2014 = carAwards2014;
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

    public List<Award> getXindividualAwards2016() {
        return xindividualAwards2016;
    }

    public void setXindividualAwards2016(List<Award> xindividualAwards2016) {
        this.xindividualAwards2016 = xindividualAwards2016;
    }

    public Award getSelectedAward() {
        return selectedAward;
    }

    public void setSelectedAward(Award selectedAward) {
        this.selectedAward = selectedAward;
    }

    public List<String> getFilterList() {
        return filterList;
    }

    public void setFilterList(List<String> filterList) {
        this.filterList = filterList;
    }

    public long getProgress() {
        return progress;
    }

    public void setProgress(long progress) {
        this.progress = progress;
    }

    public String getDriverToFind() {
        return driverToFind;
    }

    public void setDriverToFind(String driverToFind) {
        this.driverToFind = driverToFind;
    }

    public String getFoundDriver() {
        return foundDriver;
    }

    public void setFoundDriver(String foundDriver) {
        this.foundDriver = foundDriver;
    }


}
