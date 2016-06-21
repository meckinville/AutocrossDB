/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package autocrossdb.component;

import autocrossdb.entities.Events;
import autocrossdb.entities.Runs;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.primefaces.model.chart.LineChartModel;

/**
 *
 * @author rmcconville
 */
public class AnalyzedEvent implements Serializable
{
    
    private Events neglectedEvent;
    
    private static DecimalFormat threeDecimals = new DecimalFormat(".###");
    
    private int totalDrivers;
    private int runs;
    private long offCourseRuns;
    private double avgRunTime;
    private long totalCones;
    private double avgCones;
    private int seasonYear;
    
    private List<StandingsTableRow> rawTimes;
    private List<StandingsTableRow> paxTimes;
    private List<StandingsTableRow> classTimes;
    private List<StandingsTableRow> coneKillers;
    
    private HashMap<String, LineChartModel> classBattle;
    private HashMap<String, Integer> classLeadChanges;
    
    private String selectedClass;
    private String currentClass;
    private LineChartModel currentClassBattle;
    private int currentLeadChange;
    
    private Runs topRawRun;
    private long topRawWinCount;
    
    private Runs topPaxRun;
    private long topPaxWinCount;
    
    private Runs topConeKillerRun;
    
    private Runs noviceChampRun;

    public AnalyzedEvent()
    {
        currentClassBattle = new LineChartModel();
    }
    
    public AnalyzedEvent(Events e)
    {
        this.neglectedEvent = e;
    }
    
    public AnalyzedEvent(Events e, int totalDrivers, double avgRunTime, long totalCones, int runs, long offCourseRuns, List<Object[]> rawTimes, List<Object[]> paxTimes, List<Object[]> classTimes, List<Object[]> coneKillers)
    {
        this.neglectedEvent = e;
        this.totalDrivers = totalDrivers;
        this.avgRunTime = avgRunTime;
        this.totalCones = totalCones;
        this.avgCones = Double.parseDouble(threeDecimals.format((double)totalCones/(double)totalDrivers));
        this.runs = runs;
        this.offCourseRuns = offCourseRuns;
        
        this.rawTimes = new ArrayList();
        this.paxTimes = new ArrayList();
        this.classTimes = new ArrayList();
        this.coneKillers = new ArrayList();
        
        currentClassBattle = new LineChartModel();
        
        double topTime = 0;
        double lastTime = 0;
        for(int x = 0; x < rawTimes.size(); x++)
        {
            if(x == 0)
            {
                this.rawTimes.add(new StandingsTableRow(x+1, String.valueOf(rawTimes.get(x)[1]), String.valueOf(rawTimes.get(x)[3]), String.valueOf(rawTimes.get(x)[2]), (int)rawTimes.get(x)[4], (double)rawTimes.get(x)[0], 0.000, 0.000));
                lastTime = (double)rawTimes.get(x)[0];
                topTime = (double)rawTimes.get(x)[0];
            }
            else
            {
                this.rawTimes.add(new StandingsTableRow(x+1, String.valueOf(rawTimes.get(x)[1]), String.valueOf(rawTimes.get(x)[3]), String.valueOf(rawTimes.get(x)[2]), (int)rawTimes.get(x)[4], (double)rawTimes.get(x)[0], lastTime-(double)rawTimes.get(x)[0], topTime-(double)rawTimes.get(x)[0]));
                lastTime = (double)rawTimes.get(x)[0];
            }
        }
        
        for(int x = 0; x < paxTimes.size(); x++)
        {
            if(x == 0)
            {
                this.paxTimes.add(new StandingsTableRow(x+1, String.valueOf(paxTimes.get(x)[1]), String.valueOf(paxTimes.get(x)[3]), String.valueOf(paxTimes.get(x)[2]), (int)paxTimes.get(x)[4], (double)paxTimes.get(x)[0], 0.000, 0.000));
                lastTime = (double)paxTimes.get(x)[0];
                topTime = (double)paxTimes.get(x)[0];
            }
            else
            {
                this.paxTimes.add(new StandingsTableRow(x+1, String.valueOf(paxTimes.get(x)[1]), String.valueOf(paxTimes.get(x)[3]), String.valueOf(paxTimes.get(x)[2]), (int)paxTimes.get(x)[4], (double)paxTimes.get(x)[0], lastTime-(double)paxTimes.get(x)[0], topTime-(double)paxTimes.get(x)[0]));
                lastTime = (double)paxTimes.get(x)[0];
            }
        }
        
        String currentClass = "";
        int classPosition = 1;
        for(int x = 0; x < classTimes.size(); x++)
        {
            if(x == 0)
            {
                currentClass = String.valueOf(classTimes.get(x)[2]);
                this.classTimes.add(new StandingsTableRow(1, String.valueOf(classTimes.get(x)[1]), String.valueOf(classTimes.get(x)[3]), String.valueOf(classTimes.get(x)[2]), (int)classTimes.get(x)[4], (double)classTimes.get(x)[0], 0.000, 0.000));
                lastTime = (double)classTimes.get(x)[0];
                topTime = (double)classTimes.get(x)[0];
                classPosition = 2;
            }
            else if(!(String.valueOf(classTimes.get(x)[2]).equals(currentClass)))
            {
                currentClass = String.valueOf(classTimes.get(x)[2]);
                //this.classTimes.add(new StandingsTableRow(0, "", "", "", 0, 0.0, 0.0, 0.0));
                this.classTimes.add(new StandingsTableRow(1, String.valueOf(classTimes.get(x)[1]), String.valueOf(classTimes.get(x)[3]), String.valueOf(classTimes.get(x)[2]), (int)classTimes.get(x)[4], (double)classTimes.get(x)[0], 0.000, 0.000));
                lastTime = (double)classTimes.get(x)[0];
                topTime = (double)classTimes.get(x)[0];
                classPosition = 2;
            }
            else
            {
                this.classTimes.add(new StandingsTableRow(classPosition, String.valueOf(classTimes.get(x)[1]), String.valueOf(classTimes.get(x)[3]), String.valueOf(classTimes.get(x)[2]), (int)classTimes.get(x)[4], (double)classTimes.get(x)[0], lastTime-(double)classTimes.get(x)[0], topTime-(double)classTimes.get(x)[0]));
                classPosition++;
                lastTime = (double)classTimes.get(x)[0];
            }
        }
        
        for(int x = 0; x < coneKillers.size(); x++)
        {
            this.coneKillers.add(new StandingsTableRow(x+1, String.valueOf(coneKillers.get(x)[1]), String.valueOf(coneKillers.get(x)[3]), String.valueOf(coneKillers.get(x)[2]), Integer.parseInt(coneKillers.get(x)[0].toString()), 0, 0, 0));
        }
    }
    
    public void summaryRowCalculation(Object o)
    {
        this.currentLeadChange = classLeadChanges.get(o.toString());
        this.currentClass = o.toString();
    }
    
    public Events getNeglectedEvent() {
        return neglectedEvent;
    }

    public void setNeglectedEvent(Events neglectedEvent) {
        this.neglectedEvent = neglectedEvent;
    }

    public int getTotalDrivers() {
        return totalDrivers;
    }

    public void setTotalDrivers(int totalDrivers) {
        this.totalDrivers = totalDrivers;
    }

    public double getAvgRunTime() {
        return avgRunTime;
    }

    public void setAvgRunTime(double avgRunTime) {
        this.avgRunTime = avgRunTime;
    }

    public long getTotalCones() {
        return totalCones;
    }

    public void setTotalCones(long totalCones) {
        this.totalCones = totalCones;
    }

    public int getRuns() {
        return runs;
    }

    public void setRuns(int runs) {
        this.runs = runs;
    }

    public long getOffCourseRuns() {
        return offCourseRuns;
    }

    public void setOffCourseRuns(long offCourseRuns) {
        this.offCourseRuns = offCourseRuns;
    }

    public List<StandingsTableRow> getRawTimes() {
        return rawTimes;
    }

    public void setRawTimes(List<StandingsTableRow> rawTimes) {
        this.rawTimes = rawTimes;
    }

    public List<StandingsTableRow> getPaxTimes() {
        return paxTimes;
    }

    public void setPaxTimes(List<StandingsTableRow> paxTimes) {
        this.paxTimes = paxTimes;
    }

    public List<StandingsTableRow> getClassTimes() {
        return classTimes;
    }

    public void setClassTimes(List<StandingsTableRow> classTimes) {
        this.classTimes = classTimes;
    }

    public HashMap<String, LineChartModel> getClassBattle() {
        return classBattle;
    }

    public void setClassBattle(HashMap<String, LineChartModel> classBattle) {
        this.classBattle = classBattle;
    }
    
    public LineChartModel getCurrentClassBattle()
    {
        if(this.selectedClass == null || this.selectedClass.equals(""))
        {
            return new LineChartModel();
        }
        return classBattle.get(this.selectedClass);
    }
    
    public void setCurrentClassBattle(LineChartModel currentClassBattle) {
        this.currentClassBattle = currentClassBattle;
    }

    public String getSelectedClass() {
        return selectedClass;
    }

    public void setSelectedClass(String selectedClass) {
        this.selectedClass = selectedClass;
    }

    public HashMap<String, Integer> getClassLeadChanges() {
        return classLeadChanges;
    }

    public void setClassLeadChanges(HashMap<String, Integer> classLeadChanges) {
        this.classLeadChanges = classLeadChanges;
    }

    public int getCurrentLeadChange() {
        
        return currentLeadChange;
    }

    public void setCurrentLeadChange(int currentLeadChange) {
        this.currentLeadChange = currentLeadChange;
    }

    public String getCurrentClass() {
        return currentClass;
    }

    public void setCurrentClass(String currentClass) {
        this.currentClass = currentClass;
    }

    public double getAvgCones() {
        return avgCones;
    }

    public void setAvgCones(double avgCones) {
        this.avgCones = avgCones;
    }

    public long getTopRawWinCount() {
        return topRawWinCount;
    }

    public void setTopRawWinCount(long topRawWinCount) {
        this.topRawWinCount = topRawWinCount;
    }

    public long getTopPaxWinCount() {
        return topPaxWinCount;
    }

    public void setTopPaxWinCount(long topPaxWinCount) {
        this.topPaxWinCount = topPaxWinCount;
    }

    public int getSeasonYear() {
        return seasonYear;
    }

    public void setSeasonYear(int seasonYear) {
        this.seasonYear = seasonYear;
    }

    public Runs getTopRawRun() {
        return topRawRun;
    }

    public void setTopRawRun(Runs topRawRun) {
        this.topRawRun = topRawRun;
    }

    public Runs getTopPaxRun() {
        return topPaxRun;
    }

    public void setTopPaxRun(Runs topPaxRun) {
        this.topPaxRun = topPaxRun;
    }

    public Runs getTopConeKillerRun() {
        return topConeKillerRun;
    }

    public void setTopConeKillerRun(Runs topConeKillerRun) {
        this.topConeKillerRun = topConeKillerRun;
    }

    public Runs getNoviceChampRun() {
        return noviceChampRun;
    }

    public void setNoviceChampRun(Runs noviceChampRun) {
        this.noviceChampRun = noviceChampRun;
    }

    public List<StandingsTableRow> getConeKillers() {
        return coneKillers;
    }

    public void setConeKillers(List<StandingsTableRow> coneKillers) {
        this.coneKillers = coneKillers;
    }


    

    

    
    
    
    
}
