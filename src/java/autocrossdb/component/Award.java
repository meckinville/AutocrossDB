/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package autocrossdb.component;

import java.util.Objects;

/**
 *
 * @author rmcconville
 */
public class Award implements Comparable<Award>
{
    private double value;
    private String name;
    
    public Award(Object[] award)
    {
        this.value = (double)award[0];
        this.name = String.valueOf(award[1]);
    }
    
    public Award(double l, String s)
    {
        this.name = s; 
        this.value = l;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    @Override
    public boolean equals(Object object)
    {
        if(object != null && object instanceof Award)
        {
            if(this.name.equalsIgnoreCase(((Award)object).getName()))
            {
                return true;
            }
            else
            {
                return false;
            }
        }
        else
        {
            return false;
        }
    }
    @Override
    public int hashCode() 
    {
        int hash = 7;
        hash = 43 * hash + Objects.hashCode(this.name);
        return hash;
    }
    
    
    @Override
    public int compareTo(Award a)
    {
        if(this.getValue() > a.getValue())
        {
            return 1;
        }
        else if(this.getValue() < a.getValue())
        {
            return -1;
        }
        else
        {
            return 0;
        }
    }
    
    @Override
    public String toString()
    {
        return this.name + " " + this.value;
    }
}
