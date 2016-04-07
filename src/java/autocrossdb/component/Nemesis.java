/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package autocrossdb.component;

import autocrossdb.entities.Classes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 *
 * @author rmcconville
 */
public class Nemesis implements Comparable<Nemesis>
{
    private String name;
    private Classes cls;
    private double value;
    private int eventsTogether;
    private int rank;
    
    private List<Double> rawDiff;
    private List<Double> paxDiff;
    
    private Set<String> carsDriven;
    
    public Nemesis()
    {
        
    }
    
    public Nemesis(String name, double rawDiff, double paxDiff, String car, Classes cls)
    {
        this.name = name;
        this.rawDiff = new ArrayList();
        this.rawDiff.add(rawDiff);
        this.paxDiff = new ArrayList();
        this.paxDiff.add(paxDiff);
        this.cls = cls;
        this.eventsTogether = 1;
        carsDriven = new TreeSet();
        carsDriven.add(car);
    }

    public void calculateRawValue()
    {
        double total = 0;
        for(Double d : rawDiff)
        {
            total += d;
        }
        total /= (double)rawDiff.size();
        
        total = Math.round(total * 100000d) / 100000d;
        this.value = total;
    }
    
    public void calculatePaxValue()
    {
        double total = 0;
        for(Double d : paxDiff)
        {
            total += d;
        }
        total /= (double)paxDiff.size();
        
        total = Math.round(total * 100000d) / 100000d;
        this.value = total;
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

    public List<Double> getRawDiff() {
        return rawDiff;
    }

    public void setRawDiff(List<Double> rawDiff) {
        this.rawDiff = rawDiff;
    }

    public List<Double> getPaxDiff() {
        return paxDiff;
    }

    public void setPaxDiff(List<Double> paxDiff) {
        this.paxDiff = paxDiff;
    }
    
    public void addRawDiff(double rawDiff)
    {
        this.rawDiff.add(rawDiff);
    }
    
    public void addPaxDiff(double paxDiff)
    {
        this.paxDiff.add(paxDiff);
    }
    
    public void addCarDriven(String car)
    {
        this.carsDriven.add(car);
    }

    public Set<String> getCarsDriven() {
        return carsDriven;
    }

    public void setCarsDriven(Set<String> carsDriven) {
        this.carsDriven = carsDriven;
    }

    public Classes getCls() {
        return cls;
    }

    public void setCls(Classes cls) {
        this.cls = cls;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }
    
    
 
    @Override
    public int compareTo(Nemesis n)
    {
        if(Math.abs(this.value) > Math.abs(n.getValue()))
        {
            return 1;
        }
        else if(Math.abs(this.value) < Math.abs(n.getValue()))
        {
            return -1;
        }
        else
        {
            return 0;
        }
                
    }
    
}
