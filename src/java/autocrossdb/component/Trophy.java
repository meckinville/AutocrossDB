/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package autocrossdb.component;

import autocrossdb.entities.Classes;
import autocrossdb.entities.Events;
import java.text.SimpleDateFormat;

/**
 *
 * @author rmcconville
 */
public class Trophy 
{
    private int position = 0;
    private String type;
    private Events event;
    private Classes cls;
    
    private SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy");
    
    public Trophy(int pos, String type, Events e, Classes c)
    {
        this.position = pos;
        this.type = type;
        this.event = e;
        this.cls = c;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Events getEvent() {
        return event;
    }

    public void setEvent(Events event) {
        this.event = event;
    }

    public Classes getCls() {
        return cls;
    }

    public void setCls(Classes cls) {
        this.cls = cls;
    }

    public String toString()
    {
        String trophyColor = "";
        if(this.position == 0)
        {
            trophyColor = "Gold";
        }
        else if(this.position == 1)
        {
            trophyColor = "Silver";
        }
        else
        {
            trophyColor = "Bronze";
        }
        
        return this.cls.getClassName() + " " + trophyColor + " " + this.type + " " + this.event.getEventLocation() + " " + df.format(this.event.getEventDate()) + " " + this.event.getEventClubName();
    }
}
