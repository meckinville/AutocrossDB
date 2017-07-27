/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package autocrossdb.backing;

import autocrossdb.entities.Events;
import autocrossdb.entities.Runs;
import autocrossdb.component.AnalyzedDriver;
import autocrossdb.component.Nemesis;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author rmcconville
 */
@ManagedBean(name="driverAnalysis")
@SessionScoped
public class DriverAnalysisBean 
{
    private String driver = "";
    private Date endDate;
    private Date startDate;
    private long progress;
    
    private List<AnalyzedDriver> events;
    private AnalyzedDriver selectedEvent;
    Set<Nemesis> nemesisList = new TreeSet();
    
    private double avgClassPercent = 0;
    private double avgRawPercent = 0;
    private double avgPaxPercent = 0;
    private double avgCones = 0;
    private double avgBestRun = 0;
    private long totalCones = 0;
    private long totalOffcourse = 0;
    private double totalTimeOncourse = 0;
    private long totalRuns = 0; 
    private int totalClassWins = 0;
    private int totalRawWins = 0;
    private int totalPaxWins = 0;
    
    @PersistenceContext
    private EntityManager em;
    
    @PostConstruct
    public void init()
    {
        Calendar now = Calendar.getInstance();
        endDate = now.getTime();
        now.set(Calendar.MONTH, now.get(Calendar.MONTH)-8);
        startDate = now.getTime();
    }
    
    public void analyzeDriver()
    {
        avgClassPercent = 0;
        avgRawPercent = 0;
        avgPaxPercent = 0;
        avgCones = 0;
        avgBestRun = 0;
        totalCones = 0;
        totalOffcourse = 0;
        totalTimeOncourse = 0;
        totalRuns = 0;
        totalClassWins = 0;
        totalRawWins = 0;
        totalPaxWins = 0;
        
        events = new ArrayList();
        List<Events> rawEventList = em.createQuery("SELECT e from Events e JOIN e.runsCollection r WHERE r.runDriverName = :driver AND r.runNumber = 1 AND e.eventDate > :start AND e.eventDate < :end ORDER BY e.eventDate desc", Events.class).setParameter("driver", this.driver).setParameter("start", startDate).setParameter("end", endDate).getResultList();

        for(Events e : rawEventList)
        {
            List<Runs> yourRuns = em.createQuery("SELECT r FROM Runs r where r.runDriverName = :driver and r.runEventId = :url", Runs.class).setParameter("driver", driver).setParameter("url", e).getResultList();
            List<Object[]> competitorRuns = em.createQuery("SELECT min(r.runTime), r.runDriverName, r.runCarName FROM Runs r WHERE r.runClassName.className = :class AND r.runEventId = :url AND r.runOffcourse = 'N' group by r.runDriverName order by min(r.runTime)", Object[].class).setParameter("class", yourRuns.get(0).getRunClassName().getClassName()).setParameter("url", e).getResultList();
            String classPosition = "";
            double ourRunTime = 0;
            
            for(int x = 0; x < competitorRuns.size(); x++)
            {
                if(competitorRuns.get(x)[1].equals(driver))
                {
                    classPosition = String.valueOf(x+1);
                    if(classPosition.equals("1"))
                    {
                        totalClassWins++;
                    }
                    ourRunTime = (double)competitorRuns.get(x)[0];
                    break;
                }
            }
            
            if(classPosition.equals(""))
            {
                Object[] temp = { 999.999, yourRuns.get(0).getRunDriverName(), yourRuns.get(0).getRunCarName() };
                competitorRuns.add(temp);
                classPosition = String.valueOf(competitorRuns.size());
                ourRunTime = (double)competitorRuns.get(competitorRuns.size()-1)[0];
            }
            
            String rawPosition = "";
            List<Object[]> rawRuns = em.createQuery("SELECT min(r.runTime), r.runDriverName, r.runCarName, r.runClassName.className FROM Runs r WHERE r.runEventId = :url AND r.runOffcourse = 'N' group by r.runDriverName order by min(r.runTime)", Object[].class).setParameter("url", e).getResultList();
            for(int x = 0; x < rawRuns.size(); x++)
            {
                if(rawRuns.get(x)[1].equals(driver))
                {
                    rawPosition = String.valueOf(x+1);
                    if(rawPosition.equals("1"))
                    {
                        totalRawWins++;
                    }
                    break;
                }
            }
            
            if(rawPosition.equals(""))
            {
                Object[] temp = { 999.999, yourRuns.get(0).getRunDriverName(), yourRuns.get(0).getRunCarName(), yourRuns.get(0).getRunClassName().getClassName() };
                rawRuns.add(temp);
                rawPosition = String.valueOf(rawRuns.size());
            }
            
            String paxPosition = "";
            List<Object[]> paxRuns = em.createQuery("SELECT min(r.runPaxTime), r.runDriverName, r.runCarName, r.runClassName.className FROM Runs r WHERE r.runEventId = :url AND r.runOffcourse = 'N' group by r.runDriverName order by min(r.runPaxTime)", Object[].class).setParameter("url", e).getResultList();
            for(int x = 0; x < paxRuns.size(); x++)
            {
                if(paxRuns.get(x)[1].equals(driver))
                {
                    paxPosition = String.valueOf(x+1);
                    if(paxPosition.equals("1"))
                    {
                        totalPaxWins++;
                    }
                    break;
                }
            }
            
            if(paxPosition.equals(""))
            {
                Object[] temp = { 999.999, yourRuns.get(0).getRunDriverName(), yourRuns.get(0).getRunCarName(), yourRuns.get(0).getRunClassName().getClassName() };
                paxRuns.add(temp);
                paxPosition = String.valueOf(paxRuns.size());
            }
            
            List<Integer> bestRunQuery = em.createQuery("SELECT r.runNumber FROM Runs r WHERE r.runDriverName = :driver and r.runTime = :runTime").setParameter("driver", driver).setParameter("runTime", ourRunTime).getResultList();
            long conesKilled = (long)em.createQuery("SELECT sum(r.runCones) FROM Runs r where r.runDriverName = :driver AND r.runEventId = :url").setParameter("driver", driver).setParameter("url", e).getResultList().get(0);
            
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
            List<Runs> noConesQuery = em.createQuery("SELECT r FROM Runs r where r.runEventId = :url and r.runClassName.className = :class and r.runOffcourse = 'N'").setParameter("url", e).setParameter("class", yourRuns.get(0).getRunClassName().getClassName()).getResultList();
            
            AnalyzedDriver eventToAdd;
            if(bestRunQuery.isEmpty())
            {
                eventToAdd = new AnalyzedDriver(e, yourRuns, classPosition, rawPosition, paxPosition, "Off Course on All Runs", conesKilled, bestTimeIgnoringCones, competitorRuns, rawRuns, paxRuns, noConesQuery);
            }
            else
            {
                eventToAdd = new AnalyzedDriver(e, yourRuns, classPosition, rawPosition, paxPosition, bestRunQuery.get(0) + " out of " + yourRuns.size(), conesKilled, bestTimeIgnoringCones, competitorRuns, rawRuns, paxRuns, noConesQuery);
            }
            
            events.add(eventToAdd);
            avgClassPercent += Double.parseDouble(eventToAdd.getClassPercent());
            avgRawPercent += Double.parseDouble(eventToAdd.getRawPercent());
            avgPaxPercent += Double.parseDouble(eventToAdd.getPaxPercent());
            if(bestRunQuery.isEmpty())
            {
                avgBestRun += 1;
            }
            else
            {
                avgBestRun += bestRunQuery.get(0);
            }
            
            avgCones += Double.parseDouble(eventToAdd.getConesKilled());
            
            for(Runs r : yourRuns)
            {
                if(r.getRunOffcourse().equals("N"))
                {
                    totalCones += r.getRunCones();
                    totalTimeOncourse += r.getRunTime();
                }
                else
                {
                    totalOffcourse++;
                }
                totalRuns++;
            }
            
            progress += 100 / rawEventList.size();
        }
        avgClassPercent /= rawEventList.size();
        avgClassPercent = (double)Math.round(avgClassPercent * 10d)/10d;
        avgRawPercent /= rawEventList.size();
        avgRawPercent = (double)Math.round(avgRawPercent * 10d)/10d;
        avgPaxPercent /= rawEventList.size();
        avgPaxPercent = (double)Math.round(avgPaxPercent * 10d)/10d;
        avgBestRun /= rawEventList.size();
        avgBestRun = (double)Math.round(avgBestRun * 10d)/10d;
        avgCones /= rawEventList.size();
        avgCones = (double)Math.round(avgCones * 10d)/10d;
        totalTimeOncourse = (double)Math.round(totalTimeOncourse * 1000d) / 1000d;
        progress = 100;
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
    
    public int getEventsSize()
    {
        if(events == null)
        {
            return 0;
        }
        else
        {
            return events.size();
        }
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

    public List<AnalyzedDriver> getEvents() {
        return events;
    }

    public void setEvents(List<AnalyzedDriver> events) {
        this.events = events;
    }

    public String getDriver() {
        return driver;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }

    public AnalyzedDriver getSelectedEvent() {
        return selectedEvent;
    }

    public void setSelectedEvent(AnalyzedDriver selectedEvent) {
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

    public double getAvgClassPercent() {
        return avgClassPercent;
    }

    public void setAvgClassPercent(double avgClassPercent) {
        this.avgClassPercent = avgClassPercent;
    }

    public double getAvgRawPercent() {
        return avgRawPercent;
    }

    public void setAvgRawPercent(double avgRawPercent) {
        this.avgRawPercent = avgRawPercent;
    }

    public double getAvgPaxPercent() {
        return avgPaxPercent;
    }

    public void setAvgPaxPercent(double avgPaxPercent) {
        this.avgPaxPercent = avgPaxPercent;
    }

    public double getAvgCones() {
        return avgCones;
    }

    public void setAvgCones(double avgCones) {
        this.avgCones = avgCones;
    }

    public double getAvgBestRun() {
        return avgBestRun;
    }

    public void setAvgBestRun(double avgBestRun) {
        this.avgBestRun = avgBestRun;
    }

    public long getTotalCones() {
        return totalCones;
    }

    public void setTotalCones(long totalCones) {
        this.totalCones = totalCones;
    }

    public long getTotalOffcourse() {
        return totalOffcourse;
    }

    public void setTotalOffcourse(long totalOffcourse) {
        this.totalOffcourse = totalOffcourse;
    }

    public double getTotalTimeOncourse() {
        return totalTimeOncourse;
    }

    public void setTotalTimeOncourse(double totalTimeOncourse) {
        this.totalTimeOncourse = totalTimeOncourse;
    }

    public long getTotalRuns() {
        return totalRuns;
    }

    public void setTotalRuns(long totalRuns) {
        this.totalRuns = totalRuns;
    }

    public int getTotalClassWins() {
        return totalClassWins;
    }

    public void setTotalClassWins(int totalClassWins) {
        this.totalClassWins = totalClassWins;
    }

    public int getTotalRawWins() {
        return totalRawWins;
    }

    public void setTotalRawWins(int totalRawWins) {
        this.totalRawWins = totalRawWins;
    }

    public int getTotalPaxWins() {
        return totalPaxWins;
    }

    public void setTotalPaxWins(int totalPaxWins) {
        this.totalPaxWins = totalPaxWins;
    }
    
    
    
}
