/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package autocrossdb.backing;

import autocrossdb.entities.Classes;
import autocrossdb.entities.Events;
import autocrossdb.entities.Runs;
import autocrossdb.facades.ClassesFacade;
import autocrossdb.facades.EventsFacade;
import autocrossdb.facades.RunsFacade;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.bean.ViewScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author rmcconville
 */
@ManagedBean(name="eventLoader")
@ViewScoped
public class EventLoaderBean 
{
    @EJB
    private ClassesFacade classesFacade;
    @EJB
    private RunsFacade runsFacade;
    @EJB
    private EventsFacade eventsFacade;
    @PersistenceContext
    private EntityManager em;

    private String url;
    private String club;
    private String location;
    private Date date;
    private String points;
    private String growlMessage;
    
    private long progress;
    private long completeProgress;
    private double progDoub;
    
    private Events eventToWrite;
    private Classes classToWrite;
    private Runs runToWrite;
    private Collection<Runs> runsCollection;
    
    private DateFormat webFormat;
    
    @PostConstruct
    public void init()
    {
        webFormat = new SimpleDateFormat("MM-dd-yyyy");
    }
    
    public void loadEvent()
    {
        if(url.contains("cfrsolo2"))
        {
            loadCFREvent();
        }
        else if(url.contains("martinscc"))
        {
            loadMartinEvent();
        }
        else if(url.contains("buccaneerregion"))
        {
            loadBuccaneerEvent();
        }
        else if(url.contains("gulfcoastautocrossers"))
        {
            loadGulfCoastEvent();
        }
    }
    
    public void loadCFREvent()
    {
        try
        {
            //Status bar tracker
            progress = 0;
            
            Document results = Jsoup.connect(url).get();
            //String date = "";
            //String raceLocation = ""; //between second and third dash
            String driverName = "";
            String carName = "";

            Elements tables = results.select("table");
            Element infoTable = tables.first();
            Elements infoRows = infoTable.select("th");
            /*for(int x = 0; x < infoRows.size(); x++)
            {
                if(infoRows.get(x).text().length() > 9)
                {
                    if(infoRows.get(x).text().substring(infoRows.get(x).text().length()-10, infoRows.get(x).text().length()).matches("[0-1][0-9]-[0-3][0-9]-[0-9][0-9][0-9][0-9]"))
                    {
                        date = infoRows.get(x).text().substring(infoRows.get(x).text().length()-10, infoRows.get(x).text().length());
                        int firstDash = infoRows.get(x).text().indexOf("-");
                        int secondDash = infoRows.get(x).text().indexOf("-", firstDash+1);
                        int thirdDash = infoRows.get(x).text().indexOf("-", secondDash+1);
                        raceLocation = infoRows.get(x).text().substring(secondDash+1, thirdDash).trim().toUpperCase();
                        break;
                    }
                }
            }*/
            eventToWrite = new Events(url, club, location, date, points);
            Element driversTable = tables.get(1);
            Elements driverRows = driversTable.select("tr:has(td)");
            runsCollection = new ArrayList<Runs>();
            
            completeProgress = driverRows.size() * 2;
            for(int x = 1; x < driverRows.size(); x++)
            {
                progDoub += 100.0 / completeProgress;
                progress = Math.round(progDoub);
                Elements driverCells = driverRows.get(x).select("td");
                String clsStr = driverCells.get(1).text().replace("'", "");
                driverName = driverCells.get(3).text();
                
                if(driverName.equals("") || driverName == null)
                {
                    driverName = driverCells.get(2).text();
                    carName = driverCells.get(3).text();
                    if(driverName.equals("") || driverName == null)
                    {
                        continue;
                    }
                }
                else
                {
                    carName = driverCells.get(4).text();
                }
                
                
                
                
                if(clsStr.equals(""))
                {
                    Elements driverCell = results.select("tr > td:matchesOwn(^" + driverName + "$)");
                    Element driverRow = driverCell.first().parent();
                    while(driverRow.previousElementSibling() != null)
                    {
                        driverRow = driverRow.previousElementSibling();
                        if(driverRow.select("th").size() > 0)
                        {
                            Element classRow = driverRow.select("th").first();
                            Element classLink = classRow.select("a[name]").first();
                            clsStr = classLink.attr("name");
                            break;
                        }
                    }
                }
                
                if(clsStr.contains("NS"))
                {
                    clsStr = "NS";
                }
                if(clsStr.endsWith("L") || clsStr.endsWith("l"))
                {
                    clsStr = clsStr.substring(0,clsStr.length()-1);
                }
                if(clsStr.equals("CAMC"))
                {
                    clsStr = "CAM-C";
                }
                clsStr = clsStr.toUpperCase();
                classToWrite = classesFacade.find(clsStr);
                int position = 1;
                
                if(driverName.contains(","))
                {
                    driverName = driverName.substring(driverName.indexOf(",")+1) + " " + driverName.substring(0, driverName.indexOf(","));
                    driverName = driverName.trim();
                }
                
                for(int y = 5; y < driverCells.size()-2; y++)
                {
                    //if we're on the last column... check the next row to see if the driver field is blank.
                    //if it is blank, the results must be on 2 different lines.
                    if(y == driverCells.size()-2)
                    {
                        Elements extraDriverCells = null;
                        if(driverRows.size() > x+1)
                        {
                            extraDriverCells = driverRows.get(x+1).select("td");
                        }
                        
                        if(extraDriverCells != null && (extraDriverCells.first().text().equals("") || extraDriverCells.first().text() == null || extraDriverCells.first().text().equals(" ")))
                        {
                            for(Element td : extraDriverCells)
                            {
                                if(td.text().length() > 5)
                                {
                                    if(td.text().substring(0,6).matches("[0-9][0-9].[0-9][0-9][0-9]"))
                                    {
                                        String time = td.text();
                                        String offcourse = "N";
                                        int cones = 0;
                                        double paxTime = 0;
                                        if(time.contains("+"))
                                        {
                                            if(time.substring(time.indexOf("+")+1).equals("DNF"))
                                            {
                                                offcourse = "Y";
                                            }
                                            else
                                            {
                                                cones = Integer.parseInt(time.substring(time.indexOf("+")+1));
                                            }

                                            time = time.substring(0, time.indexOf("+")-1);
                                            
                                            if(cones > 0)
                                            {
                                                double tempRunTime = Double.parseDouble(time);
                                                tempRunTime += 2 * cones;
                                                time = Double.toString(tempRunTime);
                                            }
                                        }
                                        paxTime = calculatePax(classToWrite, time);
                                        runToWrite = new Runs(null, driverName.replace("'", "").toUpperCase(), carName.replace("'", "").toUpperCase(),  position, Double.parseDouble(time), paxTime, offcourse, cones);
                                        position++;
                                        runToWrite.setRunClassName(classToWrite);
                                        runToWrite.setRunEventUrl(eventToWrite);
                                        runsCollection.add(runToWrite);
                                    }
                                }
                                
                            }
                        }
                        
                    }
                    
                    if(driverCells.get(y).text().length() >= 6 && driverCells.get(y).text().contains("."))
                    {
                        String time = driverCells.get(y).text();
                        String offcourse = "N";
                        int cones = 0;
                        double paxTime = 0;
                        if(time.contains("+"))
                        {
                            if(time.substring(time.indexOf("+")+1).equalsIgnoreCase("OFF") || time.substring(time.indexOf("+")+1).equalsIgnoreCase("DNF") || time.substring(time.indexOf("+")+1).equalsIgnoreCase("DSQ"))
                            {
                                offcourse = "Y";
                            }
                            else
                            {
                                cones = Integer.parseInt(time.substring(time.indexOf("+")+1));
                            }
                            time = time.substring(0, time.indexOf("+"));
                            if(cones > 0)
                            {
                                double tempRunTime = Double.parseDouble(time);
                                tempRunTime += 2 * cones;
                                time = Double.toString(tempRunTime);
                            }
                        }
                        paxTime = calculatePax(classToWrite, time);
                        
                        if(time.length() > 0)
                        {
                            runToWrite = new Runs(null, driverName.replace("'", "").toUpperCase(), carName.replace("'", "").toUpperCase(),  position, Double.parseDouble(time), paxTime, offcourse, cones);
                            position++;
                            runToWrite.setRunClassName(classToWrite);
                            runToWrite.setRunEventUrl(eventToWrite);
                            runsCollection.add(runToWrite);
                        }
                        
                    }
                }
            }
            eventToWrite.setRunsCollection(runsCollection);
            if(eventsFacade.find(eventToWrite.getEventUrl()) == null)
            {
                eventsFacade.create(eventToWrite);
                Iterator<Runs> it = runsCollection.iterator();
                while(it.hasNext())
                {
                    runsFacade.edit(it.next());
                    progDoub += 100.0 / completeProgress;
                    progress = Math.round(progDoub);
                }
                String paxWinner = (String)em.createNamedQuery("Runs.findBestPaxByEvent", Object[].class).setParameter("eventUrl", eventToWrite.getEventUrl()).getResultList().get(0)[0];
                String rawWinner = (String)em.createNamedQuery("Runs.findBestRawByEvent", Object[].class).setParameter("eventUrl", eventToWrite.getEventUrl()).getResultList().get(0)[0];
                eventToWrite.setPaxWinner(paxWinner);
                eventToWrite.setRawWinner(rawWinner);
                eventsFacade.edit(eventToWrite);
                        
                progress = 100;
                growlMessage = "Loaded Event: " + eventToWrite.getEventLocation() + " " + webFormat.format(eventToWrite.getEventDate());
            }
            else
            {
                progress = 100;
                growlMessage = eventToWrite.getEventLocation() + " " + webFormat.format(eventToWrite.getEventDate()) + " has already been loaded.";
            }
          
        }
        catch(Exception e)
        {
            growlMessage = "Error loading event.";
            e.printStackTrace();
        }
    }
    
    public void loadMartinEvent()
    {
        try
        {
            progress = 0;
            Document results = Jsoup.connect(url).get();
            
            Elements tables = results.select("table");
            Element table = tables.get(1);
            
            Elements rows = table.select("tr");
            
            String driverName = "";
            String clsName = "";
            String carName = "";
            int runNumber = 1;
            Classes classToWrite = new Classes();
            Runs runToWrite = new Runs();
            Events eventToWrite = new Events(url, club, location, date, points);
            Element classHeaderRow = null;
            ArrayList<Runs> runsCollection = new ArrayList();
            completeProgress = rows.size() * 2;
            for(Element row : rows)
            { 
                Elements columns = row.select("td");
                
                //if we dont have any td's in this tr we must have th's
                //we will get the th row to use later in case the class
                //information is not available in the row
                if(columns.isEmpty())
                {
                    classHeaderRow = row.select("th:has(a[name])").first();
                    continue;
                }
                if(columns.first().text().length() > 0)
                {
                    if(columns.select("p:has(a[name])").size() > 0)
                    {
                        classHeaderRow = columns.select("p:has(a[name])").first();
                    }
                           
                    
                    if(Character.isDigit(columns.first().text().charAt(0)))
                    {
                        runNumber = 1;
                        clsName = columns.get(1).select("span").text();
                        if(clsName.length() == 0)
                        {
                            clsName = columns.get(1).text();
                        }
                        if(clsName.equalsIgnoreCase("NOV") || clsName.equalsIgnoreCase("NOV2"))
                        {
                            clsName = "NS";
                        }
                        //check to make sure the class exists
                        if(classesFacade.find(clsName) != null)
                        {
                            classToWrite = classesFacade.find(clsName);
                        }
                        //if it does not we must look at the table header to find what class we are
                        else
                        {
                            Element classLink = classHeaderRow.select("a[name]").first();
                            String cls = classLink.attr("name");
                            if(cls.equalsIgnoreCase("NOV")  || clsName.equalsIgnoreCase("NOV2"))
                            {
                                cls = "NS";
                            }
                            classToWrite = classesFacade.find(cls);
                        }
                        
                        driverName = columns.get(3).select("span").text();
                        if(driverName.length() == 0)
                        {
                            driverName = columns.get(3).text();
                        }
                        //prevent double last names from the double spans below
                        String[] driverNames = driverName.split(" ");
                        if(driverNames[driverNames.length-1].equals(driverNames[driverNames.length-2]))
                        {
                            driverName = driverNames[0];
                            for(int i = 1; i < driverNames.length-1;i++)
                            {
                                driverName += " " + driverNames[i];
                            }
                            
                        }
                        driverName = driverName.replace("'", "");
                        driverName = driverName.replace(".", "");
                        
                        carName = columns.get(4).select("span").text();
                        if(carName.length() == 0)
                        {
                            carName = columns.get(4).text();
                        }
                        //prevent the car name from being repeated below
                        String[] carNames = carName.split(" ");
                        if(!(carNames.length < 3))
                        {
                            if(carNames[carNames.length-1].equals(carNames[carNames.length-2]))
                            {
                                carName = carNames[0];
                                for(int i = 1; i < carNames.length-1; i++)
                                {
                                    carName += " " + carNames[i];
                                }
                            }
                        }
                        
                        
                        for(int x = 5; x < columns.size()-1; x++)
                        {
                            String columnText = columns.get(x).select("span").text();
                            if(columnText.length() == 0)
                            {
                                columnText = columns.get(x).text();
                            }

                            //if the column has a period it must be a time
                            //i suck at regexes
                            if(columnText.contains(".") && (columnText.substring(0,6).matches("[0-9][0-9].[0-9][0-9][0-9]") || columnText.substring(0,6).matches("[0-9][0-9][0-9].[0-9][0-9][0-9]")))
                            {
                                //if the time contains a + sign it must have a cone count or off course
                                if(columnText.contains("+"))
                                {
                                    //check for offcourse
                                    if(columnText.substring(columnText.indexOf("+")+1).trim().equals("OFF") || columnText.substring(columnText.indexOf("+")+1).trim().equals("DNF"))
                                    {
                                        String runTime = columnText.substring(0,columnText.indexOf("+"));
                                        double paxTime = calculatePax(classToWrite, runTime);
                                        runToWrite = new Runs(null, driverName.replace("'", "").toUpperCase(), carName.replace("'", "").toUpperCase(), runNumber, Double.parseDouble(runTime), paxTime, "Y", 0);
                                        runToWrite.setRunClassName(classToWrite);
                                        runToWrite.setRunEventUrl(eventToWrite);
                                        runsCollection.add(runToWrite);
                                        runNumber++;
                                    }
                                    //else if not off course it must be cones
                                    else
                                    {
                                        int cones = Integer.parseInt(columnText.substring(columnText.indexOf("+")+1).trim());
                                        double runTime = Double.parseDouble(columnText.substring(0,columnText.indexOf("+")));
                                        runTime += 2 * cones;
                                        double paxTime = calculatePax(classToWrite, String.valueOf(runTime));
                                        runToWrite = new Runs(null, driverName.replace("'", "").toUpperCase(), carName.replace("'", "").toUpperCase(), runNumber, runTime, paxTime, "N", cones);
                                        runToWrite.setRunClassName(classToWrite);
                                        runToWrite.setRunEventUrl(eventToWrite);
                                        runsCollection.add(runToWrite);
                                        runNumber++;
                                    }
                                }
                                else
                                {
                                    String runTime = columnText;
                                    double paxTime = calculatePax(classToWrite, runTime);
                                    runToWrite = new Runs(null, driverName.replace("'", "").toUpperCase(), carName.replace("'", "").toUpperCase(), runNumber, Double.parseDouble(runTime), paxTime, "N", 0);
                                    runToWrite.setRunClassName(classToWrite);
                                    runToWrite.setRunEventUrl(eventToWrite);
                                    runsCollection.add(runToWrite);
                                    runNumber++;
                                }
                                
                            }
                            //else if the column does not have a period but it says OFF it is an off course
                            else if(columnText.trim().equalsIgnoreCase("OFF") || columnText.substring(columnText.indexOf("+")+1).trim().equals("DNF"))
                            {
                                runToWrite = new Runs(null, driverName.replace("'", "").toUpperCase(), carName.replace("'", "").toUpperCase(), runNumber, 999.999, 999.999, "Y", 0);
                                runToWrite.setRunClassName(classToWrite);
                                runToWrite.setRunEventUrl(eventToWrite);
                                runsCollection.add(runToWrite);
                                runNumber++;
                            }
                        }
                    }
                }
                else
                {
                    Elements extraColumns = row.select("td");
                    for(int y = 0; y < extraColumns.size()-1; y++)
                    {
                        String columnText = columns.get(y).select("span").text();
                        if(columnText.length() == 0)
                        {
                            columnText = columns.get(y).text();
                        }
                        //if the column contains a period it must be a time
                        if(columnText.contains("."))
                        {
                            //check to see if there are any cones or off course with this time
                            if(columnText.contains("+"))
                            {
                                //check for offcourse
                                if(columnText.substring(columnText.indexOf("+")+1).trim().equals("OFF"))
                                {
                                    String runTime = columnText.substring(0,columnText.indexOf("+"));
                                    double paxTime = calculatePax(classToWrite, runTime);
                                    runToWrite = new Runs(null, driverName.replace("'", "").toUpperCase(), carName.replace("'", "").toUpperCase(), runNumber, Double.parseDouble(runTime), paxTime, "Y", 0);
                                    runToWrite.setRunClassName(classToWrite);
                                    runToWrite.setRunEventUrl(eventToWrite);
                                    runsCollection.add(runToWrite);
                                    runNumber++;
                                }
                                //else if not off course it must be cones
                                else
                                {
                                    int cones = Integer.parseInt(columnText.substring(columnText.indexOf("+")+1).trim());
                                    double runTime = Double.parseDouble(columnText.substring(0,columnText.indexOf("+")));
                                    runTime += 2 * cones;
                                    double paxTime = calculatePax(classToWrite, String.valueOf(runTime));
                                    runToWrite = new Runs(null, driverName.replace("'", "").toUpperCase(), carName.replace("'", "").toUpperCase(), runNumber, runTime, paxTime, "N", cones);
                                    runToWrite.setRunClassName(classToWrite);
                                    runToWrite.setRunEventUrl(eventToWrite);
                                    runsCollection.add(runToWrite);
                                    runNumber++;
                                }
                            }
                            //else must be just a clean time
                            else
                            {
                                String runTime = columnText;
                                double paxTime = calculatePax(classToWrite, runTime);
                                
                                runToWrite = new Runs(null, driverName.replace("'", "").toUpperCase(), carName.replace("'", "").toUpperCase(), runNumber, Double.parseDouble(runTime), paxTime, "N", 0);
                                runToWrite.setRunClassName(classToWrite);
                                runToWrite.setRunEventUrl(eventToWrite);
                                runsCollection.add(runToWrite);
                                runNumber++;
                            }
                        }
                        //if the column does not contain a period, it might be OFF
                        else if(columnText.equalsIgnoreCase("OFF") || columnText.substring(columnText.indexOf("+")+1).trim().equals("DNF"))
                        {
                            runToWrite = new Runs(null, driverName.replace("'", "").toUpperCase(), carName.replace("'", "").toUpperCase(), runNumber, 999.999, 999.999, "Y", 0);
                            runToWrite.setRunClassName(classToWrite);
                            runToWrite.setRunEventUrl(eventToWrite);
                            runsCollection.add(runToWrite);
                            runNumber++;
                        }
                    }
                }
            }
            eventToWrite.setRunsCollection(runsCollection);
            if(eventsFacade.find(eventToWrite.getEventUrl()) == null)
            {
                eventsFacade.create(eventToWrite);
                Iterator<Runs> it = runsCollection.iterator();
                while(it.hasNext())
                {
                    runsFacade.edit(it.next());
                    progDoub += 100.0 / completeProgress;
                    progress = Math.round(progDoub);
                }
                String paxWinner = (String)em.createNamedQuery("Runs.findBestPaxByEvent", Object[].class).setParameter("eventUrl", eventToWrite.getEventUrl()).getResultList().get(0)[0];
                String rawWinner = (String)em.createNamedQuery("Runs.findBestRawByEvent", Object[].class).setParameter("eventUrl", eventToWrite.getEventUrl()).getResultList().get(0)[0];
                eventToWrite.setPaxWinner(paxWinner);
                eventToWrite.setRawWinner(rawWinner);
                eventsFacade.edit(eventToWrite);
                        
                progress = 100;
                growlMessage = "Loaded Event: " + eventToWrite.getEventLocation() + " " + webFormat.format(eventToWrite.getEventDate());
            }
            else
            {
                progress = 100;
                growlMessage = eventToWrite.getEventLocation() + " " + webFormat.format(eventToWrite.getEventDate()) + " has already been loaded.";
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    
    public void loadBuccaneerEvent()
    {
        try
        {
            progress = 0;
            
            Document results = Jsoup.connect(url).get();
            Element table = results.select("table").first();
            Elements rows = table.select("tr:has(td)");
            
            completeProgress = rows.size() * 2;
            Events eventToWrite = new Events(url, club, location, date, points);
            Classes classToWrite =  new Classes();
            String driverName = "";
            String carName = "";
            int runNumber = 1;
            
            //we check the header row's last column for the word diff
            //if it has the word diff that means we need to stop getting times at
            //size-2. if it does not say diff we stop getting times at size-1.
            Element headerRow = table.select("tr:has(th)").first();
            Element finalHeaderColumn = headerRow.select("th").last();
            int lastTimeColumn = 1;
            if(finalHeaderColumn.text().toLowerCase().contains("diff"))
            {
                lastTimeColumn = 2;
            }
            
            ArrayList<Runs> runsCollection = new ArrayList();
            for(Element row : rows)
            {
                //noCarOffset is initialized at 0. if there is no car information in the results table then the times will start in column 4
                //if there is car information times will start at column 5. if there is no car information we set noCarOffset to 1. noCarOffset is
                //then subtracted from the iterator in the for loop that grabs run times
                int noCarOffset = 0;
                
                
                Elements columns = row.select("td");
                if(columns.get(0).text().length() > 0)
                {
                    if(Character.isDigit(columns.get(0).text().charAt(0)))
                    {
                        String className = columns.get(1).text();
                        if(className.startsWith("p") || className.startsWith("P") || className.startsWith("t") || className.startsWith("T"))
                        {
                            className = className.substring(1);
                        }
                        if(className.charAt(className.length()-1) == 'l' || className.charAt(className.length()-1) == 'L')
                        {
                            className = className.substring(0,className.length()-1);
                        }
                        if(className.equals("camc"))
                        {
                            className = "CAM-C";
                        }
                        if(className.equals("camt"))
                        {
                            className = "CAM-T";
                        }
                        if(className.equals("cams"))
                        {
                            className = "CAM-S";
                        }

                        className = className.toUpperCase();
                        classToWrite = classesFacade.find(className);
                        driverName = columns.get(3).text();
                        //if driver names are backwards (mcconville, ryan) flip them back around
                        if(driverName.contains(","))
                        {
                            String[] driverNames = driverName.split(", ");
                            driverName = driverNames[driverNames.length-1];
                            for(int x = 0; x < driverNames.length-1; x++)
                            {
                                driverName += " " + driverNames[x];
                            }
                        }
                        driverName = driverName.replace("'", "");
                        driverName = driverName.replace(".", "");
                        carName = columns.get(4).text();
                        if(carName.length() > 5)
                        {
                            if(carName.substring(0,6).matches("[0-9][0-9].[0-9][0-9][0-9]") || carName.substring(0,6).matches("[0-9][0-9][0-9].[0-9][0-9]") )
                            {
                                carName = "";
                                noCarOffset = 1;
                            }
                        }
                        
                        
                        runNumber = 1;
                        for(int x = 5-noCarOffset; x < columns.size()-lastTimeColumn; x++)
                        {
                            int cones = 0;
                            String offcourse = "N";
                            String runTime = columns.get(x).text();
                            if(runTime.length() > 5)
                            {
                                //check the run time for a + sign to see if theres cones or off course
                                if(runTime.contains("+"))
                                {
                                    //check for offcourse
                                    if(runTime.substring(runTime.indexOf("+")+1).equalsIgnoreCase("dnf"))
                                    {
                                        offcourse = "Y";
                                        runTime = runTime.substring(0,runTime.indexOf("+"));
                                    }
                                    //else it must be cones
                                    else
                                    {
                                        cones = Integer.parseInt(runTime.substring(runTime.indexOf("+")));
                                        runTime = runTime.substring(0,runTime.indexOf("+"));
                                        double tempRunTime = Double.parseDouble(runTime);
                                        tempRunTime += cones * 2;
                                        runTime = String.valueOf(tempRunTime);
                                    }
                                }
                                double paxTime = calculatePax(classToWrite, runTime);
                                runToWrite = new Runs(null, driverName.toUpperCase(), carName.toUpperCase(), runNumber, Double.parseDouble(runTime), paxTime, offcourse, cones);
                                runToWrite.setRunClassName(classToWrite);
                                runToWrite.setRunEventUrl(eventToWrite);
                                runsCollection.add(runToWrite);
                                runNumber++;
                            }
                        }
                    }
                }
                else
                {
                    for(int y = 5-noCarOffset; y < columns.size()-lastTimeColumn; y++)
                    {
                        int cones = 0;
                        String offcourse = "N";
                        String runTime = columns.get(y).text();
                        if(runTime.length() > 5)
                        {
                            //check the run time for a + sign to see if theres cones or off course
                            if(runTime.contains("+"))
                            {
                                //check for offcourse
                                if(runTime.substring(runTime.indexOf("+")+1).equalsIgnoreCase("dnf"))
                                {
                                    offcourse = "Y";
                                    runTime = runTime.substring(0,runTime.indexOf("+"));
                                }
                                //else it must be cones
                                else
                                {
                                    cones = Integer.parseInt(runTime.substring(runTime.indexOf("+")));
                                    runTime = runTime.substring(0,runTime.indexOf("+"));
                                    double tempRunTime = Double.parseDouble(runTime);
                                    tempRunTime += cones * 2;
                                    runTime = String.valueOf(tempRunTime);
                                }
                            }
                            double paxTime = calculatePax(classToWrite, runTime);
                            runToWrite = new Runs(null, driverName.toUpperCase(), carName.toUpperCase(), runNumber, Double.parseDouble(runTime), paxTime, offcourse, cones);
                            runToWrite.setRunClassName(classToWrite);
                            runToWrite.setRunEventUrl(eventToWrite);
                            runsCollection.add(runToWrite);
                            runNumber++;
                        }
                    }
                }
            }
            eventToWrite.setRunsCollection(runsCollection);
            if(eventsFacade.find(eventToWrite.getEventUrl()) == null)
            {
                eventsFacade.create(eventToWrite);
                Iterator<Runs> it = runsCollection.iterator();
                while(it.hasNext())
                {
                    runsFacade.edit(it.next());
                    progDoub += 100.0 / completeProgress;
                    progress = Math.round(progDoub);
                }
                String paxWinner = (String)em.createNamedQuery("Runs.findBestPaxByEvent", Object[].class).setParameter("eventUrl", eventToWrite.getEventUrl()).getResultList().get(0)[0];
                String rawWinner = (String)em.createNamedQuery("Runs.findBestRawByEvent", Object[].class).setParameter("eventUrl", eventToWrite.getEventUrl()).getResultList().get(0)[0];
                eventToWrite.setPaxWinner(paxWinner);
                eventToWrite.setRawWinner(rawWinner);
                eventsFacade.edit(eventToWrite);
                        
                progress = 100;
                growlMessage = "Loaded Event: " + eventToWrite.getEventLocation() + " " + webFormat.format(eventToWrite.getEventDate());
            }
            else
            {
                progress = 100;
                growlMessage = eventToWrite.getEventLocation() + " " + webFormat.format(eventToWrite.getEventDate()) + " has already been loaded.";
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    
    public void loadGulfCoastEvent()
    {
        try
        {
            progress = 0;
            Document results = Jsoup.connect(url).get();
            Element table = results.select("table:has(tr.rowhigh)").first();
            Elements driverRows = table.select("tr:has(td)");
            
            Events eventToWrite = new Events(url, club, location, date, points);
            Classes classToWrite =  new Classes();
            String driverName = "";
            String carName = "";
            String className = "";
            ArrayList<Runs> runsCollection = new ArrayList();
            completeProgress = driverRows.size();
            
            for(Element row : driverRows)
            {
                Elements columns = row.select("td");
                className = columns.get(1).text();
                if(className.endsWith("L") || className.endsWith("l"))
                {
                    className = className.substring(0,className.length()-1);
                }
                if(className.endsWith("NL") || className.endsWith("nl"))
                {
                    className = "NS";
                }
                if(className.endsWith("N") || className.endsWith("n"))
                {
                    className = "NS";
                }
                classToWrite = classesFacade.find(className);
                driverName = columns.get(3).text().toUpperCase();
                carName = columns.get(4).text().toUpperCase();
                int runNumber = 1;
                for(int x = 5; x < columns.size()-2; x++)
                {
                    String columnText = columns.get(x).text();
                    
                    String offCourse = "N";
                    
                    int cones = 0;
                    double runTime = 0;
                    double paxTime = 0;
                    if(columnText.length() > 5)
                    {
                        if(columnText.substring(0,6).matches("[0-9][0-9].[0-9][0-9][0-9]") || columnText.substring(0,6).matches("[0-9][0-9][0-9].[0-9][0-9]"))
                        {
                            if(columnText.contains("+"))
                            {
                                if(columnText.substring(columnText.indexOf("+")+1).equals("OC") || columnText.substring(columnText.indexOf("+")+1).equals("DNF"))
                                {
                                    offCourse = "Y";
                                }
                                else if(columnText.substring(columnText.indexOf("+")+1).equals("`"))
                                {
                                    cones = 1;
                                }
                                else
                                {
                                    cones = Integer.parseInt(columnText.substring(columnText.indexOf("+")+1));
                                }
                                columnText = columnText.substring(0, columnText.indexOf("+"));
                            }
                            runTime = Double.parseDouble(columnText);
                            runTime += 2 * cones;
                            paxTime = calculatePax(classToWrite, String.valueOf(runTime));
                            
                            Runs runToWrite = new Runs(null, driverName.toUpperCase(), carName.toUpperCase(), runNumber, runTime, paxTime, offCourse, cones);
                            runToWrite.setRunClassName(classToWrite);
                            runToWrite.setRunEventUrl(eventToWrite);
                            runsCollection.add(runToWrite);
                            runNumber++;
                        }
                    }
                }
            }
            progress = 50;
            eventToWrite.setRunsCollection(runsCollection);
            if(eventsFacade.find(eventToWrite.getEventUrl()) == null)
            {
                eventsFacade.create(eventToWrite);
                Iterator<Runs> it = runsCollection.iterator();
                while(it.hasNext())
                {
                    runsFacade.edit(it.next());
                    progress += runsCollection.size() / 100.0;
                }
                String paxWinner = (String)em.createNamedQuery("Runs.findBestPaxByEvent", Object[].class).setParameter("eventUrl", eventToWrite.getEventUrl()).getResultList().get(0)[0];
                String rawWinner = (String)em.createNamedQuery("Runs.findBestRawByEvent", Object[].class).setParameter("eventUrl", eventToWrite.getEventUrl()).getResultList().get(0)[0];
                eventToWrite.setPaxWinner(paxWinner);
                eventToWrite.setRawWinner(rawWinner);
                eventsFacade.edit(eventToWrite);

                progress = 100;
                growlMessage = "Loaded Event: " + eventToWrite.getEventLocation() + " " + webFormat.format(eventToWrite.getEventDate());
            }
            else
            {
                progress = 100;
                growlMessage = eventToWrite.getEventLocation() + " " + webFormat.format(eventToWrite.getEventDate()) + " has already been loaded.";
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    
    public double calculatePax(Classes classToWrite, String runTime)
    {
        double paxTime = 0;
        
        if(webFormat.format(date).contains("2016"))
        {
            paxTime = Double.parseDouble(runTime) * classToWrite.getClass2016Pax();
        }
        if(webFormat.format(date).contains("2015"))
        {
            paxTime = Double.parseDouble(runTime) * classToWrite.getClass2015Pax();
        }
        if(webFormat.format(date).contains("2014"))
        {
            paxTime = Double.parseDouble(runTime) * classToWrite.getClass2014Pax();
        }
        if(webFormat.format(date).contains("2013"))
        {
            paxTime = Double.parseDouble(runTime) * classToWrite.getClass2013Pax();
        }
        paxTime = (double)Math.round(paxTime * 1000d)/1000d;
        
        return paxTime;
    }
    
    public long getProgress()
    {
        if(progress > 100)
        {
            progress = 100;
        }
        return progress;
    }
    
    public void setProgress(long progress)
    {
        this.progress = progress;
    }
    
    public void onCompleteLoad()
    {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(growlMessage));
        progress = 0;
        progDoub = 0;
    }
       
    public String getUrl()
    {
        return this.url;
    }
    
    public void setUrl(String url)
    {
        this.url = url;
    }
    
    public String getClub()
    {
        return this.club;
    }
    
    public void setClub(String club)
    {
        this.club = club;
    }
    
    public String getPoints()
    {
        return this.points;
    }
    
    public void setPoints(String points)
    {
        this.points = points;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    
}
