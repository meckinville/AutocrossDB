/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package autocrossdb.backing;

import autocrossdb.component.AnalyzedEvent;
import autocrossdb.component.ClassBattleHelper;
import autocrossdb.entities.Events;
import autocrossdb.entities.Runs;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.primefaces.model.chart.Axis;
import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.ChartSeries;
import org.primefaces.model.chart.LineChartModel;
import org.primefaces.model.chart.LineChartSeries;

/**
 *
 * @author rmcconville
 */
@ManagedBean(name="eventInfo")
@ViewScoped
public class EventInfoBean implements Serializable
{
    private List<AnalyzedEvent> analyzedEvents;
    private AnalyzedEvent selectedAnalyzedEvent;
    private long progress;
    
    private Date startDate;
    private Date endDate;
    
    private String clubFilter;
    private String classBattleSelection;
    
    @PersistenceContext
    private EntityManager em;

    @PostConstruct
    public void init()
    {
        Calendar now = Calendar.getInstance();
        endDate = now.getTime();
        now.set(Calendar.MONTH, now.get(Calendar.MONTH)-3);
        startDate = now.getTime();
        progress = 0;
        selectedAnalyzedEvent = new AnalyzedEvent();
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
            
            if(clubFilter.equals("ALL") || clubFilter == null)
            {
                eventsList = em.createNamedQuery("Events.findEventsInDateRange", Events.class).setParameter("startDate", startDate).setParameter("endDate", endDate).getResultList();
            }
            else
            {
                eventsList = em.createNamedQuery("Events.findClubEventsInDateRange", Events.class).setParameter("startDate", startDate).setParameter("endDate", endDate).setParameter("clubName", clubFilter).getResultList();
            }
            
            progress = 0;
            double progDoub = 0;
            
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
                
                List<Runs> classBattleTimes = em.createQuery("SELECT r from Runs r where r.runEventUrl.eventUrl = :eventUrl order by r.runClassName.className, r.runNumber, r.runTime").setParameter("eventUrl", e.getEventUrl()).getResultList();
                AnalyzedEvent tempEvent = new AnalyzedEvent(e, totalDrivers, avgRunTime, totalCones, runs, offCourseRuns, rawTimes, paxTimes, classTimes);
                //tempEvent.setClassBattle(calculateClassBattles(classBattleTimes));
                tempEvent.setClassBattle(separateClassesForBattle(classBattleTimes));
                
                analyzedEvents.add(tempEvent);
                progDoub += 100 / eventsList.size();
                progress = (long)progDoub;
            }
            progress = 100;
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    
    private HashMap<String, LineChartModel> separateClassesForBattle(List<Runs> runs)
    {
        HashMap<String, LineChartModel> battle = new HashMap();
        
        HashMap<String, List<Runs>> separateClasses = new HashMap();
        
        for(Runs r : runs)
        {
            if(separateClasses.containsKey(r.getRunClassName().getClassName()))
            {
                separateClasses.get(r.getRunClassName().getClassName()).add(r);
            }
            else
            {
                List<Runs> temp = new ArrayList();
                temp.add(r);
                separateClasses.put(r.getRunClassName().getClassName(), temp);
            }
        }
        
        for(String s : separateClasses.keySet())
        {
            List<Runs> currentList = separateClasses.get(s);
            int xMax = 0;
            int yMax = 0;
            HashMap<Integer, List<ClassBattleHelper>> positions = new HashMap();
            HashMap<String, ClassBattleHelper> bestRuns = new HashMap();
            for(Runs r : currentList)
            {
                if(positions.containsKey(r.getRunNumber()))
                {
                    ClassBattleHelper temp = new ClassBattleHelper(r.getRunDriverName(), r.getRunTime(), r.getRunOffcourse());
                    if(bestRuns.containsKey(r.getRunDriverName()))
                    {
                        if(temp.compareTo(bestRuns.get(r.getRunDriverName())) > 0)
                        {
                            bestRuns.put(r.getRunDriverName(), temp);
                        }
                    }
                    else
                    {
                        bestRuns.put(r.getRunDriverName(), temp);
                    }
                    positions.get(r.getRunNumber()).add(bestRuns.get(r.getRunDriverName()));
                }
                else
                {
                    ClassBattleHelper temp = new ClassBattleHelper(r.getRunDriverName(), r.getRunTime(), r.getRunOffcourse());
                    if(bestRuns.containsKey(r.getRunDriverName()))
                    {
                        if(temp.compareTo(bestRuns.get(r.getRunDriverName())) > 0)
                        {
                            bestRuns.put(r.getRunDriverName(), temp);
                        }
                    }
                    else
                    {
                        bestRuns.put(r.getRunDriverName(), temp);
                    }
                    
                    List<ClassBattleHelper> tempList = new ArrayList();
                    tempList.add(bestRuns.get(r.getRunDriverName()));
                    positions.put(r.getRunNumber(), tempList);
                }
            }
            
            LineChartModel chart = new LineChartModel();
            chart.setTitle(s + " Position Battle");
            chart.setLegendPosition("ne");
            Axis yAxis = chart.getAxis(AxisType.Y);
            Axis xAxis = chart.getAxis(AxisType.X);
            yAxis.setLabel("Position");
            xAxis.setLabel("Run Number");

             
            
            HashMap<String, ChartSeries> seriesMap = new HashMap();
            for(int key : positions.keySet())
            {
                if(positions.get(key).size() < bestRuns.keySet().size())
                {
                    Set<String> keys = bestRuns.keySet();
                    List<ClassBattleHelper> checkList = positions.get(key);
                    for(String k : keys)
                    {
                        if(!checkList.contains(bestRuns.get(k)))
                        {
                            checkList.add(bestRuns.get(k));
                        }
                    }
                    
                }
                if(key > xMax)
                {
                    xMax = key;
                }
                List<ClassBattleHelper> positionList = positions.get(key);
                Collections.sort(positionList, Collections.reverseOrder());
                int position = 1;
                for(ClassBattleHelper cbh : positionList)
                {
                    if(seriesMap.containsKey(cbh.getDriver()))
                    {
                        seriesMap.get(cbh.getDriver()).set(key, position);
                        if(position > yMax)
                        {
                            yMax = position;
                        }
                        position++;
                    }
                    else
                    {
                        ChartSeries tempSeries = new ChartSeries();
                        tempSeries.setLabel(cbh.getDriver());
                        tempSeries.set(key, position);
                        seriesMap.put(cbh.getDriver(), tempSeries);
                        if(position > yMax)
                        {
                            yMax = position;
                        }
                        position++;
                    }
                }
            }
            for(String st : seriesMap.keySet())
            {
                chart.addSeries(seriesMap.get(st));
            }

            xAxis.setMax(xMax + 1);
            yAxis.setMax(yMax + 1);
            yAxis.setMin(0);
            xAxis.setMin(0);
            yAxis.setTickCount(yMax+2);
            xAxis.setTickCount(xMax+2);
            yAxis.setTickInterval("1");
            xAxis.setTickInterval("1");
            battle.put(s, chart);
        }
        
        return battle;
    }
    
    public int getEventsSize()
    {
        if(analyzedEvents == null)
        {
            return 0;
        }
        else
        {
            return analyzedEvents.size();
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

    public String getClassBattleSelection() {
        return classBattleSelection;
    }

    public void setClassBattleSelection(String classBattleSelection) {
        this.classBattleSelection = classBattleSelection;
    }

    

   
    
}
 