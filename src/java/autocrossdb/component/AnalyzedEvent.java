/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package autocrossdb.component;

import autocrossdb.entities.Events;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author rmcconville
 */
public class AnalyzedEvent
{
    private Events neglectedEvent;
    
    private int totalDrivers;
    private int runs;
    private long offCourseRuns;
    private double avgRunTime;
    private long totalCones;
    
    private List<ClassTableRow> rawTimes;
    private List<ClassTableRow> paxTimes;
    private List<ClassTableRow> classTimes;
    
    private String topRawName;
    private String topRawTime;
    private String topRawCar;
    private String topRawClass;
    private String topPaxName;
    private String topPaxTime;
    private String topPaxCar;
    private String topPaxClass;
    private String topConeKillerName;
    private String topConeKillerCones;
    private String topConeKillerCar;
    private String topConeKillerClass;
    private String noviceChampName;
    private String noviceChampTime;
    private String noviceChampCar;
    private String noviceChampClass;
    
    public AnalyzedEvent(Events e)
    {
        this.neglectedEvent = e;
    }
    
    public AnalyzedEvent(Events e, int totalDrivers, double avgRunTime, long totalCones, int runs, long offCourseRuns, List<Object[]> rawTimes, List<Object[]> paxTimes, List<Object[]> classTimes)
    {
        this.neglectedEvent = e;
        this.totalDrivers = totalDrivers;
        this.avgRunTime = avgRunTime;
        this.totalCones = totalCones;
        this.runs = runs;
        this.offCourseRuns = offCourseRuns;
        
        this.rawTimes = new ArrayList();
        this.paxTimes = new ArrayList();
        this.classTimes = new ArrayList();
        
        double topTime = 0;
        double lastTime = 0;
        for(int x = 0; x < rawTimes.size(); x++)
        {
            if(x == 0)
            {
                this.rawTimes.add(new ClassTableRow(x+1, String.valueOf(rawTimes.get(x)[1]), String.valueOf(rawTimes.get(x)[3]), String.valueOf(rawTimes.get(x)[2]), (int)rawTimes.get(x)[4], (double)rawTimes.get(x)[0], 0.000, 0.000));
                lastTime = (double)rawTimes.get(x)[0];
                topTime = (double)rawTimes.get(x)[0];
            }
            else
            {
                this.rawTimes.add(new ClassTableRow(x+1, String.valueOf(rawTimes.get(x)[1]), String.valueOf(rawTimes.get(x)[3]), String.valueOf(rawTimes.get(x)[2]), (int)rawTimes.get(x)[4], (double)rawTimes.get(x)[0], lastTime-(double)rawTimes.get(x)[0], topTime-(double)rawTimes.get(x)[0]));
                lastTime = (double)rawTimes.get(x)[0];
            }
        }
        
        for(int x = 0; x < paxTimes.size(); x++)
        {
            if(x == 0)
            {
                this.paxTimes.add(new ClassTableRow(x+1, String.valueOf(paxTimes.get(x)[1]), String.valueOf(paxTimes.get(x)[3]), String.valueOf(paxTimes.get(x)[2]), (int)paxTimes.get(x)[4], (double)paxTimes.get(x)[0], 0.000, 0.000));
                lastTime = (double)paxTimes.get(x)[0];
                topTime = (double)paxTimes.get(x)[0];
            }
            else
            {
                this.paxTimes.add(new ClassTableRow(x+1, String.valueOf(paxTimes.get(x)[1]), String.valueOf(paxTimes.get(x)[3]), String.valueOf(paxTimes.get(x)[2]), (int)paxTimes.get(x)[4], (double)paxTimes.get(x)[0], lastTime-(double)paxTimes.get(x)[0], topTime-(double)paxTimes.get(x)[0]));
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
                this.classTimes.add(new ClassTableRow(1, String.valueOf(classTimes.get(x)[1]), String.valueOf(classTimes.get(x)[3]), String.valueOf(classTimes.get(x)[2]), (int)classTimes.get(x)[4], (double)classTimes.get(x)[0], 0.000, 0.000));
                lastTime = (double)classTimes.get(x)[0];
                topTime = (double)classTimes.get(x)[0];
                classPosition = 2;
            }
            else if(!(String.valueOf(classTimes.get(x)[2]).equals(currentClass)))
            {
                currentClass = String.valueOf(classTimes.get(x)[2]);
                this.classTimes.add(new ClassTableRow(0, "", "", "", 0, 0.0, 0.0, 0.0));
                this.classTimes.add(new ClassTableRow(1, String.valueOf(classTimes.get(x)[1]), String.valueOf(classTimes.get(x)[3]), String.valueOf(classTimes.get(x)[2]), (int)classTimes.get(x)[4], (double)classTimes.get(x)[0], 0.000, 0.000));
                lastTime = (double)classTimes.get(x)[0];
                topTime = (double)classTimes.get(x)[0];
                classPosition = 2;    
            }
            else
            {
                this.classTimes.add(new ClassTableRow(classPosition, String.valueOf(classTimes.get(x)[1]), String.valueOf(classTimes.get(x)[3]), String.valueOf(classTimes.get(x)[2]), (int)classTimes.get(x)[4], (double)classTimes.get(x)[0], lastTime-(double)classTimes.get(x)[0], topTime-(double)classTimes.get(x)[0]));
                classPosition++;
                lastTime = (double)classTimes.get(x)[0];
            }
        }
    }
    

    public String getTopRawName() {
        return topRawName;
    }

    public void setTopRawName(String topRawName) {
        this.topRawName = topRawName;
    }

    public String getTopRawTime() {
        return topRawTime;
    }

    public void setTopRawTime(String topRawTime) {
        this.topRawTime = topRawTime;
    }

    public String getTopRawCar() {
        return topRawCar;
    }

    public void setTopRawCar(String topRawCar) {
        this.topRawCar = topRawCar;
    }

    public String getTopRawClass() {
        return topRawClass;
    }

    public void setTopRawClass(String topRawClass) {
        this.topRawClass = topRawClass;
    }

    public String getTopPaxName() {
        return topPaxName;
    }

    public void setTopPaxName(String topPaxName) {
        this.topPaxName = topPaxName;
    }

    public String getTopPaxTime() {
        return topPaxTime;
    }

    public void setTopPaxTime(String topPaxTime) {
        this.topPaxTime = topPaxTime;
    }

    public String getTopPaxCar() {
        return topPaxCar;
    }

    public void setTopPaxCar(String topPaxCar) {
        this.topPaxCar = topPaxCar;
    }

    public String getTopPaxClass() {
        return topPaxClass;
    }

    public void setTopPaxClass(String topPaxClass) {
        this.topPaxClass = topPaxClass;
    }

    public String getTopConeKillerName() {
        return topConeKillerName;
    }

    public void setTopConeKillerName(String topConeKillerName) {
        this.topConeKillerName = topConeKillerName;
    }

    public String getTopConeKillerCones() {
        return topConeKillerCones;
    }

    public void setTopConeKillerCones(String topConeKillerCones) {
        this.topConeKillerCones = topConeKillerCones;
    }

    public String getTopConeKillerCar() {
        return topConeKillerCar;
    }

    public void setTopConeKillerCar(String topConeKillerCar) {
        this.topConeKillerCar = topConeKillerCar;
    }

    public String getTopConeKillerClass() {
        return topConeKillerClass;
    }

    public void setTopConeKillerClass(String topConeKillerClass) {
        this.topConeKillerClass = topConeKillerClass;
    }

    public String getNoviceChampName() {
        return noviceChampName;
    }

    public void setNoviceChampName(String noviceChampName) {
        this.noviceChampName = noviceChampName;
    }

    public String getNoviceChampTime() {
        return noviceChampTime;
    }

    public void setNoviceChampTime(String noviceChampTime) {
        this.noviceChampTime = noviceChampTime;
    }

    public String getNoviceChampCar() {
        return noviceChampCar;
    }

    public void setNoviceChampCar(String noviceChampCar) {
        this.noviceChampCar = noviceChampCar;
    }

    public String getNoviceChampClass() {
        return noviceChampClass;
    }

    public void setNoviceChampClass(String noviceChampClass) {
        this.noviceChampClass = noviceChampClass;
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

    public List<ClassTableRow> getRawTimes() {
        return rawTimes;
    }

    public void setRawTimes(List<ClassTableRow> rawTimes) {
        this.rawTimes = rawTimes;
    }

    public List<ClassTableRow> getPaxTimes() {
        return paxTimes;
    }

    public void setPaxTimes(List<ClassTableRow> paxTimes) {
        this.paxTimes = paxTimes;
    }

    public List<ClassTableRow> getClassTimes() {
        return classTimes;
    }

    public void setClassTimes(List<ClassTableRow> classTimes) {
        this.classTimes = classTimes;
    }

    

    

    
    
    
    
}
