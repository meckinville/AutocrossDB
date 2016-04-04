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
public class Nemesis implements Comparable<Nemesis>
{
    private String name;
    private double value;
    private int eventsTogether;
    
    private double rawDiff;
    private double paxDiff;
    
    public Nemesis()
    {
        
    }
    
    public Nemesis(String name, double rawDiff, double paxDiff)
    {
        this.name = name;
        this.rawDiff = rawDiff;
        this.paxDiff = paxDiff;
        this.eventsTogether = 1;
        calculateValue();
    }
    
    private void calculateValue()
    {
        double tempPax = this.paxDiff;
        double tempRaw = this.rawDiff;
        
        if(tempPax < 0)
        {
            tempPax *= -1;
        }
        if(tempRaw < 0)
        {
            tempRaw *= -1;
        }
        
        this.value = tempRaw + tempPax;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    

    public int getEventsTogether() {
        return eventsTogether;
    }

    public void setEventsTogether(int eventsTogether) {
        this.eventsTogether = eventsTogether;
    }

    public double getRawDiff() {
        return rawDiff;
    }

    public void setRawDiff(double rawDiff) {
        this.rawDiff = rawDiff;
        calculateValue();
    }

    public double getPaxDiff() {
        return paxDiff;
    }

    public void setPaxDiff(double paxDiff) {
        this.paxDiff = paxDiff;
        calculateValue();
    }
    
    
    
    @Override
    public int compareTo(Nemesis n)
    {
        if(this.value > n.getValue())
        {
            return 1;
        }
        else if(this.value < n.getValue())
        {
            return -1;
        }
        else
        {
            return 0;
        }
                
    }
    
}
