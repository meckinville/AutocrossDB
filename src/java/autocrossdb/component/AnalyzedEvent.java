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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


/**
 *
 * @author rmcconville
 */
public class AnalyzedEvent 
{
    private DateFormat dbFormat = new SimpleDateFormat("yyyy-MM-dd");
    private DateFormat webFormat = new SimpleDateFormat("MM-dd-yyyy");
    
    private String eventName;
    private String className;
    private String carName;
    private String classPosition;
    private String rawPosition;
    private String paxPosition;
    private String bestRunNumber;
    private String bestTimeIgnoringCones;
    private String conesKilled;
    
    
    private List<ClassTableRow> classTable = new ArrayList();
    private List<ClassTableRow> rawTable = new ArrayList();
    private List<ClassTableRow> paxTable = new ArrayList();
    private List<ClassTableRow> noConesTable = new ArrayList();
    
    private Map<String,Double> noConesMap;
    
    public AnalyzedEvent(Events e, List<Runs> yourRuns, String classPosition, String rawPosition, String paxPosition, String bestRunNumber, long conesKilled, double bestTimeIgnoringCones, List<Object[]> competitorRuns, List<Object[]> rawRuns, List<Object[]> paxRuns, List<Runs> noConesQuery)
    {
        this.eventName = e.getEventLocation() + " " + webFormat.format(e.getEventDate());
        this.className = yourRuns.get(0).getRunClassName().getClassName();
        this.carName = yourRuns.get(0).getRunCarName();
        this.classPosition = classPosition;
        this.rawPosition = rawPosition;
        this.paxPosition = paxPosition;
        this.bestRunNumber = bestRunNumber;
        this.conesKilled = new Long(conesKilled).toString();
        this.bestTimeIgnoringCones = String.format("%.3f", bestTimeIgnoringCones);
        
        
        double lastTime = 0;
        double topTime = 0;
        for(int x = 0; x < competitorRuns.size(); x++)
        {
            if(x == 0)
            {
                classTable.add(new ClassTableRow(x+1, String.valueOf(competitorRuns.get(x)[1]), String.valueOf(competitorRuns.get(x)[2]), className, 0, (double)competitorRuns.get(x)[0], 0.000, 0.000));
                lastTime = (double)competitorRuns.get(x)[0];
                topTime = (double)competitorRuns.get(x)[0];
                
            }
            else
            {
                classTable.add(new ClassTableRow(x+1, String.valueOf(competitorRuns.get(x)[1]), String.valueOf(competitorRuns.get(x)[2]), className, 0, (double)competitorRuns.get(x)[0], lastTime-(double)competitorRuns.get(x)[0], topTime-(double)competitorRuns.get(x)[0]));
                lastTime = (double)competitorRuns.get(x)[0];
            }
            
        }
        
        for(int x = 0; x < rawRuns.size(); x++)
        {
            if(x == 0)
            {
                rawTable.add(new ClassTableRow(x+1, String.valueOf(rawRuns.get(x)[1]), String.valueOf(rawRuns.get(x)[2]), String.valueOf(rawRuns.get(x)[3]), 0, (double)rawRuns.get(x)[0], 0.000, 0.000));
                lastTime = (double)rawRuns.get(x)[0];
                topTime = (double)rawRuns.get(x)[0];
            }
            else
            {
                rawTable.add(new ClassTableRow(x+1, String.valueOf(rawRuns.get(x)[1]), String.valueOf(rawRuns.get(x)[2]), String.valueOf(rawRuns.get(x)[3]), 0, (double)rawRuns.get(x)[0], lastTime-(double)rawRuns.get(x)[0], topTime-(double)rawRuns.get(x)[0]));
                lastTime = (double)rawRuns.get(x)[0];
            }
        }
        
        for(int x = 0; x < paxRuns.size(); x++)
        {
            if(x == 0)
            {
                paxTable.add(new ClassTableRow(x+1, String.valueOf(paxRuns.get(x)[1]), String.valueOf(paxRuns.get(x)[2]), String.valueOf(paxRuns.get(x)[3]), 0, (double)paxRuns.get(x)[0], 0.000, 0.000));
                lastTime = (double)paxRuns.get(x)[0];
                topTime = (double)paxRuns.get(x)[0];
            }
            else
            {
                paxTable.add(new ClassTableRow(x+1, String.valueOf(paxRuns.get(x)[1]), String.valueOf(paxRuns.get(x)[2]), String.valueOf(paxRuns.get(x)[3]), 0, (double)paxRuns.get(x)[0], lastTime-(double)paxRuns.get(x)[0], topTime-(double)paxRuns.get(x)[0]));
                lastTime = (double)paxRuns.get(x)[0];
            }
        }
        
        noConesMap = new HashMap();
        List<Runs> runsList = new ArrayList();
        for(int x = 0; x < noConesQuery.size(); x++)
        {
            if(noConesMap.get(noConesQuery.get(x).getRunDriverName()) == null)
            {
                noConesMap.put(noConesQuery.get(x).getRunDriverName(), noConesQuery.get(x).getRunTime() - (2 * noConesQuery.get(x).getRunCones()));
                runsList.add(noConesQuery.get(x));
            }
            else
            {
                if(noConesMap.get(noConesQuery.get(x).getRunDriverName()) > (noConesQuery.get(x).getRunTime() - (2 * noConesQuery.get(x).getRunCones())))
                {
                    noConesMap.put(noConesQuery.get(x).getRunDriverName(), noConesQuery.get(x).getRunTime() - (2 * noConesQuery.get(x).getRunCones()));
                    runsList.add(noConesQuery.get(x));
                }
            }
        }
        noConesMap = sortByComparator(noConesMap);
        int x = 0;
        for(Map.Entry<String,Double> row : noConesMap.entrySet())
        {
            String car = "";
            String cls = "";
            int cones = 0;
            
            for(int y = 0; y < runsList.size(); y++)
            {
                if(runsList.get(y).getRunDriverName().equals(row.getKey()))
                {
                    car = runsList.get(y).getRunCarName();
                    cls = runsList.get(y).getRunClassName().getClassName();
                    
                    if(runsList.get(y).getRunTime() - (2 * runsList.get(y).getRunCones()) == row.getValue())
                    {
                        cones = runsList.get(y).getRunCones();
                    }
                }
            }
            if(x == 0)
            {
                
                
                noConesTable.add(new ClassTableRow(x+1, row.getKey(), car, cls, cones, row.getValue(), 0.000, 0.000));
                lastTime = row.getValue();
                topTime = row.getValue();
            }
            else
            {
                noConesTable.add(new ClassTableRow(x+1, row.getKey(), car, cls, cones, row.getValue(), lastTime-row.getValue(), topTime-row.getValue()));
                lastTime = row.getValue();
            }
            x++;
        }
    }
    
    private static Map<String, Double> sortByComparator(Map<String, Double> unsortMap) 
    {

        // Convert Map to List
        List<Map.Entry<String, Double>> list = new LinkedList<Map.Entry<String, Double>>(unsortMap.entrySet());

        // Sort list with comparator, to compare the Map values
        Collections.sort(list, new Comparator<Map.Entry<String, Double>>() 
        {
            public int compare(Map.Entry<String, Double> o1, Map.Entry<String, Double> o2) 
            {
                return (o1.getValue()).compareTo(o2.getValue());
            }
        });

        // Convert sorted map back to a Map
        Map<String, Double> sortedMap = new LinkedHashMap<String, Double>();
        for (Iterator<Map.Entry<String, Double>> it = list.iterator(); it.hasNext();) 
        {
            Map.Entry<String, Double> entry = it.next();
            sortedMap.put(entry.getKey(), entry.getValue());
        }
        return sortedMap;
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

    public List<ClassTableRow> getRawTable() {
        return rawTable;
    }

    public void setRawTable(List<ClassTableRow> rawTable) {
        this.rawTable = rawTable;
    }

    public List<ClassTableRow> getPaxTable() {
        return paxTable;
    }

    public void setPaxTable(List<ClassTableRow> paxTable) {
        this.paxTable = paxTable;
    }

    public String getBestRunNumber() {
        return bestRunNumber;
    }

    public void setBestRunNumber(String bestRunNumber) {
        this.bestRunNumber = bestRunNumber;
    }

    public String getBestTimeIgnoringCones() {
        return bestTimeIgnoringCones;
    }

    public void setBestTimeIgnoringCones(String bestTimeIgnoringCones) {
        this.bestTimeIgnoringCones = bestTimeIgnoringCones;
    }

    public String getConesKilled() {
        return conesKilled;
    }

    public void setConesKilled(String conesKilled) {
        this.conesKilled = conesKilled;
    }

    public List<ClassTableRow> getNoConesTable() {
        return noConesTable;
    }

    public void setNoConesTable(List<ClassTableRow> noConesTable) {
        this.noConesTable = noConesTable;
    }

    
    
 
    
    
}

