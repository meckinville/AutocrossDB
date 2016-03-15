/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package autocrossdb.component;

/**
 *
 * @author rmcconville
 */
public class DriverStat implements Comparable<DriverStat>
{
    private String name;
    private double rawPercentile;
    private double paxPercentile;
    private double averageCones;
    
    private double runningRawPercentile;
    private double runningPaxPercentile;
    private double runningAverageCones;
    private long runningCones;
    
    private int eventsAttended;
    
    
    public DriverStat(String name)
    {
        this.name = name;
        rawPercentile = 0;
        paxPercentile = 0;
        averageCones = 0;
        runningRawPercentile = 0;
        runningPaxPercentile = 0;
        runningAverageCones = 0;
        eventsAttended = 0;
        runningCones = 0;
    }
    
    public DriverStat(DriverStat d)
    {
        this.name = d.getName();
        this.rawPercentile = d.getRawPercentile();
        this.paxPercentile = d.getPaxPercentile();
        this.averageCones = d.getAverageCones();
        this.runningRawPercentile = d.getRunningRawPercentile();
        this.runningPaxPercentile = d.getRunningPaxPercentile();
        this.runningAverageCones = d.getRunningAverageCones();
        this.runningCones = d.getRunningCones();
        this.eventsAttended = d.getEventsAttended();
    }
    
    @Override
    public int compareTo(DriverStat d)
    {
        return this.name.compareTo(d.getName());
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getRawPercentile() {
        return new Double(String.format("%.1f", rawPercentile));
    }

    public void setRawPercentile(double rawPercentile) {
        this.rawPercentile = rawPercentile;
    }

    public double getPaxPercentile() {
        return new Double(String.format("%.1f", paxPercentile));
    }

    public void setPaxPercentile(double paxPercentile) {
        this.paxPercentile = paxPercentile;
    }

    public double getAverageCones() {
        return new Double(String.format("%.1f", this.averageCones));
    }

    public void setAverageCones(double averageCones) {
        this.averageCones = averageCones;
    }

    public double getRunningRawPercentile() {
        return runningRawPercentile;
    }

    public void setRunningRawPercentile(double runningRawPercentile) {
        this.runningRawPercentile = runningRawPercentile;
    }

    public double getRunningPaxPercentile() {
        return runningPaxPercentile;
    }

    public void setRunningPaxPercentile(double runningPaxPercentile) {
        this.runningPaxPercentile = runningPaxPercentile;
    }

    public double getRunningAverageCones() {
        return runningAverageCones;
    }

    public void setRunningAverageCones(double runningAverageCones) {
        this.runningAverageCones = runningAverageCones;
    }

    public int getEventsAttended() {
        return eventsAttended;
    }

    public void setEventsAttended(int eventsAttended) {
        this.eventsAttended = eventsAttended;
    }

    public long getRunningCones() {
        return runningCones;
    }

    public void setRunningCones(long runningCones) {
        this.runningCones = runningCones;
    }
    
    
}
