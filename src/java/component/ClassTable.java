/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package component;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author rmcconville
 */
public class ClassTable 
{
    private int position;
    private String driver;
    private String car;
    private String time;
    private String diff;
    private String leaderDiff;
    

    public ClassTable(int position, String driver, String car, double time, double diff, double leaderDiff) {
        this.position = position;
        this.driver = driver;
        this.car = car;
        this.time = String.format("%.3f", time);
        this.diff = String.format("%.3f", diff);
        this.leaderDiff = String.format("%.3f", leaderDiff);
    }
    
    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getDriver() {
        return driver;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }

    public String getCar() {
        return car;
    }

    public void setCar(String car) {
        this.car = car;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
    
    public void setTime(double time)
    {
        this.time = String.format("%.3f", time);
    }

    public String getDiff() {
        return diff;
    }

    public void setDiff(String diff) {
        this.diff = diff;
    }

    public String getLeaderDiff() {
        return leaderDiff;
    }

    public void setLeaderDiff(String leaderDiff) {
        this.leaderDiff = leaderDiff;
    }

    

    
    
}
