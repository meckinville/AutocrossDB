/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package component;

/**
 *
 * @author rmcconville
 */
public class ChartStatistic 
{
    private String name;
    private String driver1Value;
    private String driver2Value;
    
    public ChartStatistic(String n, String v1, String v2)
    {
        this.name = n;
        this.driver1Value = v1;
        this.driver2Value = v2;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDriver1Value() {
        return driver1Value;
    }

    public void setDriver1Value(String driver1Value) {
        this.driver1Value = driver1Value;
    }

    public String getDriver2Value() {
        return driver2Value;
    }

    public void setDriver2Value(String driver2Value) {
        this.driver2Value = driver2Value;
    }
    
    
}
