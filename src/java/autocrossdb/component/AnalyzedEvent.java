/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package autocrossdb.component;

import autocrossdb.entities.Events;
import autocrossdb.entities.Runs;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import javax.faces.bean.ManagedBean;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author rmcconville
 */
public class AnalyzedEvent 
{
    private DateFormat dbFormat = new SimpleDateFormat("yyyy-MM-dd");
    
    private String eventName;
    private String className;
    private String carName;
    private String classPosition;
    private String rawPosition;
    private String paxPosition;
    
    private List<ClassTableRow> classTable = new ArrayList();
    
    
    public AnalyzedEvent(Events e, String driver, String className, String carName, String classPosition, String rawPosition, String paxPosition, List<Object[]> competitorRuns)
    {
        this.eventName = e.getEventLocation() + " " + dbFormat.format(e.getEventDate());
        this.className = className;
        this.carName = carName;
        this.classPosition = classPosition;
        this.rawPosition = rawPosition;
        this.paxPosition = paxPosition;
        
        double lastTime = 0;
        double topTime = 0;
        for(int x = 0; x < competitorRuns.size(); x++)
        {
            if(x == 0)
            {
                classTable.add(new ClassTableRow(x+1, String.valueOf(competitorRuns.get(x)[1]), String.valueOf(competitorRuns.get(x)[2]), (double)competitorRuns.get(x)[0], 0.000, 0.000));
                lastTime = (double)competitorRuns.get(x)[0];
                topTime = (double)competitorRuns.get(x)[0];
                
            }
            else
            {
                classTable.add(new ClassTableRow(x+1, String.valueOf(competitorRuns.get(x)[1]), String.valueOf(competitorRuns.get(x)[2]), (double)competitorRuns.get(x)[0], lastTime-(double)competitorRuns.get(x)[0], topTime-(double)competitorRuns.get(x)[0]));
                lastTime = (double)competitorRuns.get(x)[0];
            }
            
        }
    }
    
    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getCarName() {
        return carName;
    }

    public void setCarName(String carName) {
        this.carName = carName;
    }

    public String getClassPosition() {
        return classPosition;
    }

    public void setClassPosition(String classPosition) {
        this.classPosition = classPosition;
    }

    public String getRawPosition() {
        return rawPosition;
    }

    public void setRawPosition(String rawPosition) {
        this.rawPosition = rawPosition;
    }

    public String getPaxPosition() {
        return paxPosition;
    }

    public void setPaxPosition(String paxPosition) {
        this.paxPosition = paxPosition;
    }

    public List<ClassTableRow> getClassTable() {
        return classTable;
    }

    public void setClassTable(List<ClassTableRow> classTable) {
        this.classTable = classTable;
    }

    

 
    
    
}
