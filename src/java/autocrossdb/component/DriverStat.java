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
public class DriverStat 
{
    private String name;
    private double classPercentile;
    private double rawPercentile;
    private double paxPercentile;
    private double averageCones;
    
    private double runningClassPercentile;
    private double runningRawPercentile;
    private double runningPaxPercentile;
    private double runningAverageCones;
    
    
    public DriverStat(String name)
    {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getClassPercentile() {
        return classPercentile;
    }

    public void setClassPercentile(double classPercentile) {
        this.classPercentile = classPercentile;
    }

    public double getRawPercentile() {
        return rawPercentile;
    }

    public void setRawPercentile(double rawPercentile) {
        this.rawPercentile = rawPercentile;
    }

    public double getPaxPercentile() {
        return paxPercentile;
    }

    public void setPaxPercentile(double paxPercentile) {
        this.paxPercentile = paxPercentile;
    }

    public double getAverageCones() {
        return averageCones;
    }

    public void setAverageCones(double averageCones) {
        this.averageCones = averageCones;
    }

    public double getRunningClassPercentile() {
        return runningClassPercentile;
    }

    public void setRunningClassPercentile(double runningClassPercentile) {
        this.runningClassPercentile = runningClassPercentile;
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
    
    
}
