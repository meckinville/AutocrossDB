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
public class ClassTableRow 
{
    private int position;
    private String driver;
    private String car;
    private String cls;
    private int cones;
    private String time;
    private String diff;
    private String leaderDiff;
    

    public ClassTableRow(int position, String driver, String car, String cls, int cones, double time, double diff, double leaderDiff) {
        this.position = position;
        this.driver = driver;
        this.car = car;
        this.cls = cls;
        this.cones = cones;
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

    public String getCls() {
        return cls;
    }

    public void setCls(String cls) {
        this.cls = cls;
    }

    public int getCones() {
        return cones;
    }

    public void setCones(int cones) {
        this.cones = cones;
    }

    

    
    
}
