/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package autocrossdb.backing;

import autocrossdb.component.AnalyzedEvent;
import autocrossdb.component.AwardHelper;
import autocrossdb.component.StandingsTableRow;
import autocrossdb.entities.Cars;
import autocrossdb.entities.Runs;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.primefaces.json.JSONArray;
import org.primefaces.model.chart.Axis;
import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.CategoryAxis;
import org.primefaces.model.chart.LineChartModel;
import org.primefaces.model.chart.OhlcChartModel;
import org.primefaces.model.chart.OhlcChartSeries;
import org.primefaces.model.chart.PieChartModel;

/**
 *
 * @author rmcconville
 */
@ManagedBean(name="eventBreakdown")
@ViewScoped
public class EventBreakdownBean implements Serializable
{
    @PersistenceContext
    private EntityManager em;
    
    @ManagedProperty("#{eventInfo}")
    private EventInfoBean eventInfo;
    private AnalyzedEvent event;
    private String dateString;
    
    private Calendar seasonStart;
    
    private int seasonEventCount = 0;
    private int participantRank = 0;
    private int avgTimeRank = 0;
    private int conesKilledRank = 0;
    private int avgConesRank = 0;
    
    private PieChartModel carChart;
    private HashMap<String,Integer> makesMap;
    
    private PieChartModel countriesChart;
    private HashMap<String,Integer> countriesMap;
    
    private LineChartModel bestBattleChart;
    private int bestBattleLegendRows = 1;
    
    private OhlcChartModel spreadChart;
    private List<String> spreadTicks;
    private JSONArray spreadTicksJson;
    
    private List<String> classList;
    private int currentClassListSize;
    private String selectedClass;
    
    private List<StandingsTableRow> classTableList;
    
    @PostConstruct
    public void init()
    {
        event = eventInfo.getSelectedAnalyzedEvent();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        dateString = dateFormat.format(event.getNeglectedEvent().getEventDate());
        
        seasonStart = Calendar.getInstance();
        seasonStart.set(Calendar.MONTH, 1);
        seasonStart.set(Calendar.DAY_OF_MONTH, 1);
        seasonStart.set(Calendar.YEAR, event.getSeasonYear());
        
        classList = em.createQuery("SELECT distinct r.runClassName.className from Runs r where r.runEventUrl.eventUrl = :eventUrl").setParameter("eventUrl", event.getNeglectedEvent().getEventUrl()).getResultList();
        selectedClass = classList.get(0);
        
        drawCarChart();
        drawBestClassBattle();
        getSeasonStatRankings();
        drawClassTable();
        drawSpreadChart();
    }
    
    private void drawSpreadChart()
    {
        spreadChart = new OhlcChartModel();
        spreadChart.setCandleStick(true);
        
        CategoryAxis xAxis = new CategoryAxis("Classes");
        xAxis.setTickAngle(50);
        spreadChart.getAxes().put(AxisType.X, xAxis);
        spreadChart.setExtender("spreadChartExtender");
        spreadChart.setTitle("Class Spreads");
        spreadChart.setShowPointLabels(true);
        
        spreadTicks = new ArrayList();
        
        List<Object[]> query = em.createQuery("SELECT min(r.runTime), r.runClassName.className, r.runDriverName from Runs r where r.runEventUrl.eventUrl = :eventUrl and r.runOffcourse = 'N' group by r.runDriverName, r.runClassName.className order by r.runClassName.className, min(r.runTime)").setParameter("eventUrl", event.getNeglectedEvent().getEventUrl()).getResultList();
        
        String currentClass = query.get(0)[1].toString();
        double minTime = 100;
        double maxTime = 0;
        int index = 1;
        int numberInClass = 0;
        int chartMaxHeight = 0;
        int chartMinHeight = 100;
        for(Object[] o : query)
        {
            if(!o[1].toString().equals(currentClass))
            {
                if(numberInClass > 1)
                {
                    OhlcChartSeries series = new OhlcChartSeries(index, minTime, maxTime, minTime, maxTime);
                    spreadChart.add(series);
                    spreadTicks.add(currentClass);
                    if(minTime < chartMinHeight)
                    {
                        chartMinHeight = (int)minTime;
                    }
                    if(maxTime > chartMaxHeight)
                    {
                        chartMaxHeight = (int)maxTime;
                    }
                    numberInClass = 1;
                    index++;
                }
                minTime = (double)o[0];
                maxTime = (double)o[0];
                currentClass = o[1].toString();
            }
            else
            {
                numberInClass++;
                if((double)o[0] < minTime)
                {
                    minTime = (double)o[0];
                }
                if((double)o[0] > maxTime)
                {
                    maxTime = (double)o[0];
                }
            }
        }
        spreadTicksJson = new JSONArray(spreadTicks.toArray());
        
        Axis yAxis = spreadChart.getAxis(AxisType.Y);
        yAxis.setMax(chartMaxHeight + 2);
        yAxis.setMin(chartMinHeight - 2);

    }
    
    private void getSeasonStatRankings()
    {
        List<Object[]> query = em.createQuery("SELECT count(*), r.runEventUrl.eventUrl from Runs r where r.runEventUrl.eventDate > :seasonStart and r.runNumber = 1 group by r.runEventUrl order by count(*)").setParameter("seasonStart", seasonStart.getTime()).getResultList();
        this.seasonEventCount = query.size();
        
        List<AwardHelper> participants = new ArrayList();
        for(Object[] o : query)
        {
            participants.add(new AwardHelper(Integer.parseInt(o[0].toString()), o[1].toString()));
        }
        Collections.sort(participants, Collections.reverseOrder());
        for(int x = 0; x < participants.size(); x++)
        {
            if(participants.get(x).getName().equals(event.getNeglectedEvent().getEventUrl()))
            {
                this.participantRank = x+1;
                break;
            }
        }
        
        List<AwardHelper> cones = new ArrayList();
        List<AwardHelper> avgCones = new ArrayList();
        query = em.createQuery("SELECT sum(r.runCones), r.runEventUrl.eventUrl, count(r.runDriverName) from Runs r where r.runEventUrl.eventDate > :seasonStart group by r.runEventUrl order by sum(r.runCones)").setParameter("seasonStart", seasonStart.getTime()).getResultList();
        for(Object[] o : query)
        {
            cones.add(new AwardHelper(Integer.parseInt(o[0].toString()), o[1].toString()));
            double avg = Double.parseDouble(o[0].toString()) / Double.parseDouble(o[2].toString());
            avgCones.add(new AwardHelper(avg, o[1].toString()));
        }
        Collections.sort(cones, Collections.reverseOrder());
        Collections.sort(avgCones, Collections.reverseOrder());
        for(int x = 0; x < cones.size(); x++)
        {
            if(cones.get(x).getName().equals(event.getNeglectedEvent().getEventUrl()))
            {
                this.conesKilledRank = x+1;
            }
            if(avgCones.get(x).getName().equals(event.getNeglectedEvent().getEventUrl()))
            {
                this.avgConesRank = x+1;
            }
            //if both avgCones and conesKilled have been set to not 0, we can break the loop to save time
            if(avgConesRank != 0 && conesKilledRank != 0)
            {
                break;
            }
        }
        
        List<AwardHelper> avgTimes = new ArrayList();
        query = em.createQuery("SELECT avg(r.runTime), r.runEventUrl.eventUrl from Runs r where r.runEventUrl.eventDate > :seasonStart group by r.runEventUrl order by avg(r.runTime) asc").setParameter("seasonStart", seasonStart.getTime()).getResultList();
        for(Object[] o : query)
        {
            avgTimes.add(new AwardHelper(Double.parseDouble(o[0].toString()), o[1].toString()));
        }
        Collections.sort(avgTimes);
        for(int x = 0; x < avgTimes.size(); x++)
        {
            if(avgTimes.get(x).getName().equals(event.getNeglectedEvent().getEventUrl()))
            {
                this.avgTimeRank = x+1;
                break;
            }
        }
    }
    
    private void drawBestClassBattle()
    {
        HashMap<String, Integer> leadChanges = event.getClassLeadChanges();
        
        int mostChanges = -1;
        String mostChangesKey = "";
        for(String key : leadChanges.keySet())
        {
            if(leadChanges.get(key) > mostChanges)
            {
                mostChanges = leadChanges.get(key);
                mostChangesKey = key;
            }
            //if the class battle is equal in lead changes we want to take the bigger class
            else if(leadChanges.get(key) == mostChanges)
            {
                if(event.getClassBattle().get(mostChangesKey).getSeries().size() < event.getClassBattle().get(key).getSeries().size())
                {
                    mostChangesKey = key;
                }
            }
        }
        
        this.bestBattleChart = event.getClassBattle().get(mostChangesKey);
        bestBattleChart.setTitle("Best Class Battle - " + mostChangesKey);
        bestBattleChart.setLegendPosition("s");
        bestBattleChart.setExtender("battleChartExtender");
        bestBattleLegendRows = bestBattleChart.getSeries().size() / 3;
    }
    
    private void drawCarChart()
    {
        this.carChart = new PieChartModel();
        carChart.setTitle("Car Makes");
        carChart.setShowDataLabels(true);
        carChart.setDataFormat("label");
        carChart.setLegendPosition("s");
        carChart.setExtender("pieChartExtender");
        carChart.setSliceMargin(2);
        
        this.countriesChart = new PieChartModel();
        countriesChart.setTitle("Car Countries of Origin");
        countriesChart.setShowDataLabels(true);
        countriesChart.setDataFormat("label");
        countriesChart.setLegendPosition("s");
        countriesChart.setExtender("countriesChartExtender");
        countriesChart.setSliceMargin(2);
        
        List<Cars> carList = em.createQuery("SELECT c FROM Cars c").getResultList();
        List<Runs> runList = em.createQuery("SELECT r FROM Runs r where r.runEventUrl.eventUrl = :eventUrl and r.runNumber = 1").setParameter("eventUrl", event.getNeglectedEvent().getEventUrl()).getResultList();
        populateCarMakesMap(carList, runList);
        
        for(String key : makesMap.keySet())
        {
            carChart.set(key, makesMap.get(key));
        }
        
        for(String key: countriesMap.keySet())
        {
            countriesChart.set(key, countriesMap.get(key));
        }
    }
    
    private void populateCarMakesMap(List<Cars> carList, List<Runs> runList)
    {
        this.makesMap = new HashMap();
        this.countriesMap = new HashMap();
        
        for(Runs r : runList)
        {
            boolean added = false;
            for(Cars c : carList)
            {
                if(r.getRunCarName().contains(c.getCarMake()))
                {
                    if(makesMap.get(c.getCarMake()) == null)
                    {
                        makesMap.put(c.getCarMake(), 1);
                        added = true;
                    }
                    else
                    {
                        int value = makesMap.get(c.getCarMake());
                        value++;
                        makesMap.put(c.getCarMake(), value);
                        added = true;
                    }
                    
                    if(countriesMap.get(c.getCarCountry()) == null)
                    {
                        countriesMap.put(c.getCarCountry(), 1);
                    }
                    else
                    {
                        int value = countriesMap.get(c.getCarCountry());
                        value++;
                        countriesMap.put(c.getCarCountry(), value);
                    }
                }
                else
                {
                    String alternate = c.getCarAlternatives();
                    if(alternate != null)
                    {
                        String[] alternatives = alternate.split(",");
                        for(int x = 0; x < alternatives.length; x++)
                        {
                            if(r.getRunCarName().contains(c.getCarMake()))
                            {
                                if(makesMap.get(c.getCarMake()) == null)
                                {
                                    makesMap.put(c.getCarMake(), 1);
                                    added = true;
                                }
                                else
                                {
                                    int value = makesMap.get(c.getCarMake());
                                    value++;
                                    makesMap.put(c.getCarMake(), value);
                                    added = true;
                                }
                                if(countriesMap.get(c.getCarCountry()) == null)
                                {
                                    countriesMap.put(c.getCarCountry(), 1);
                                }
                                else
                                {
                                    int value = countriesMap.get(c.getCarCountry());
                                    value++;
                                    countriesMap.put(c.getCarCountry(), value);
                                }
                                break;
                            }
                        }
                    }
                }
            }
            /*
            if(!added)
            {
                if(makesMap.get("UNKNOWN") == null)
                {
                    makesMap.put("UNKNOWN", 1);
                }
                else
                {
                    int value = makesMap.get("UNKNOWN");
                    value++;
                    makesMap.put("UNKNOWN", value);
                }
            }
                    */
        }
    }
    
    private void drawClassTable()
    {
        List<StandingsTableRow> tempList = event.getClassTimes();
        this.classTableList = new ArrayList();
        
        for(StandingsTableRow s : tempList)
        {
            if(s.getCls().equals(selectedClass))
            {
                this.classTableList.add(s);
            }
        }
        currentClassListSize = this.classTableList.size();
    }
   
    public void classChanged()
    {
        drawClassTable();
    }

    public EventInfoBean getEventInfo() {
        return eventInfo;
    }

    public void setEventInfo(EventInfoBean eventInfo) {
        this.eventInfo = eventInfo;
    }

    public AnalyzedEvent getEvent() {
        return event;
    }

    public void setEvent(AnalyzedEvent event) {
        this.event = event;
    }

    public String getDateString() {
        return dateString;
    }

    public void setDateString(String dateString) {
        this.dateString = dateString;
    }

    public PieChartModel getCarChart() {
        return carChart;
    }

    public void setCarChart(PieChartModel carChart) {
        this.carChart = carChart;
    }

    public LineChartModel getBestBattleChart() {
        return bestBattleChart;
    }

    public void setBestBattleChart(LineChartModel bestBattleChart) {
        this.bestBattleChart = bestBattleChart;
    }

    public HashMap<String, Integer> getMakesMap() {
        return makesMap;
    }

    public void setMakesMap(HashMap<String, Integer> makesMap) {
        this.makesMap = makesMap;
    }

    public PieChartModel getCountriesChart() {
        return countriesChart;
    }

    public void setCountriesChart(PieChartModel countriesChart) {
        this.countriesChart = countriesChart;
    }

    public HashMap<String, Integer> getCountriesMap() {
        return countriesMap;
    }

    public void setCountriesMap(HashMap<String, Integer> countriesMap) {
        this.countriesMap = countriesMap;
    }

    public int getBestBattleLegendRows() {
        return bestBattleLegendRows;
    }

    public void setBestBattleLegendRows(int bestBattleLegendRows) {
        this.bestBattleLegendRows = bestBattleLegendRows;
    }

    public Calendar getSeasonStart() {
        return seasonStart;
    }

    public void setSeasonStart(Calendar seasonStart) {
        this.seasonStart = seasonStart;
    }

    public int getSeasonEventCount() {
        return seasonEventCount;
    }

    public void setSeasonEventCount(int seasonEventCount) {
        this.seasonEventCount = seasonEventCount;
    }

    public int getParticipantRank() {
        return participantRank;
    }

    public void setParticipantRank(int participantRank) {
        this.participantRank = participantRank;
    }

    public int getAvgTimeRank() {
        return avgTimeRank;
    }

    public void setAvgTimeRank(int avgTimeRank) {
        this.avgTimeRank = avgTimeRank;
    }

    public int getConesKilledRank() {
        return conesKilledRank;
    }

    public void setConesKilledRank(int conesKilledRank) {
        this.conesKilledRank = conesKilledRank;
    }

    public int getAvgConesRank() {
        return avgConesRank;
    }

    public void setAvgConesRank(int avgConesRank) {
        this.avgConesRank = avgConesRank;
    }

    public OhlcChartModel getSpreadChart() {
        return spreadChart;
    }

    public void setSpreadChart(OhlcChartModel spreadChart) {
        this.spreadChart = spreadChart;
    }

    public List<String> getClassList() {
        return classList;
    }

    public void setClassList(List<String> classList) {
        this.classList = classList;
    }

    public String getSelectedClass() {
        return selectedClass;
    }

    public void setSelectedClass(String selectedClass) {
        this.selectedClass = selectedClass;
    }

    public List<StandingsTableRow> getClassTableList() {
        return classTableList;
    }

    public void setClassTableList(List<StandingsTableRow> classTableList) {
        this.classTableList = classTableList;
    }

    public int getCurrentClassListSize() {
        return currentClassListSize;
    }

    public void setCurrentClassListSize(int currentClassListSize) {
        this.currentClassListSize = currentClassListSize;
    }

    public List<String> getSpreadTicks() {
        return spreadTicks;
    }

    public void setSpreadTicks(List<String> spreadTicks) {
        this.spreadTicks = spreadTicks;
    }

    public JSONArray getSpreadTicksJson() {
        return spreadTicksJson;
    }

    public void setSpreadTicksJson(JSONArray spreadTicksJson) {
        this.spreadTicksJson = spreadTicksJson;
    }

    
    
}
