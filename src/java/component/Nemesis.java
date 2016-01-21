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
public class Nemesis implements Comparable<Nemesis>
{
    private String name;
    private int value;
    
    public Nemesis(String name, int value)
    {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
    
    @Override
    public int compareTo(Nemesis n)
    {
        if(this.getValue() > n.getValue())
        {
            return 1;
        }
        else if(this.getValue() < n.getValue())
        {
            return -1;
        }
        else
        {
            return 0;
        }
    }
    
}
