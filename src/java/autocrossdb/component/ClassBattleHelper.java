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
public class ClassBattleHelper implements Comparable<ClassBattleHelper>
{
    String driver;
    double time;
    boolean offcourse;
    
    public ClassBattleHelper(String driver, double time, String offcourse)
    {
        this.driver = driver;
        this.time = time;
        if(offcourse.equals("Y"))
        {
            this.offcourse = true;
        }
        else
        {
            this.offcourse = false;
        }
    }
    
    @Override
    public int compareTo(ClassBattleHelper other)
    {
        if(this.time < other.getTime() && !this.offcourse && !other.isOffcourse())
        {
            return 1;
        }
        else if(!this.offcourse && other.offcourse)
        {
            return 1;
        }
        else if(this.time == other.getTime())
        {
            return 0;
        }
        else
        {
            return -1;
        }
    }
    
    @Override
    public boolean equals(Object other)
    {
        if(other != null && other instanceof ClassBattleHelper)
        {
            return this.driver.equals(((ClassBattleHelper)other).getDriver());
        }
        else
        {
            return false;
        }
    }
    
    @Override
    public String toString()
    {
        return "Driver: " + this.driver + "    Time: " + this.time + "      Offcourse: " + this.offcourse;
    }

    public String getDriver() {
        return driver;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }

    public double getTime() {
        return time;
    }

    public void setTime(double time) {
        this.time = time;
    }

    public boolean isOffcourse() {
        return offcourse;
    }

    public void setOffcourse(boolean offcourse) {
        this.offcourse = offcourse;
    }
    
    
}
