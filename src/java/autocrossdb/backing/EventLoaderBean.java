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
    }
    
    public void loadCFREvent()
    {
        try
        {
            //Status bar tracker
            progress = 0;
            
            Document results = Jsoup.connect(url).get();
            String raceDate = "";
            String raceLocation = ""; //between second and third dash
            String driverName = "";
            String carName = "";

            Elements tables = results.select("table");
            Element infoTable = tables.first();
            Elements infoRows = infoTable.select("th");
            for(int x = 0; x < infoRows.size(); x++)
            {
                if(infoRows.get(x).text().length() > 9)
                {
                    if(infoRows.get(x).text().substring(infoRows.get(x).text().length()-10, infoRows.get(x).text().length()).matches("[0-1][0-9]-[0-3][0-9]-[0-9][0-9][0-9][0-9]"))
                    {
                        raceDate = infoRows.get(x).text().substring(infoRows.get(x).text().length()-10, infoRows.get(x).text().length());
                        int firstDash = infoRows.get(x).text().indexOf("-");
                        int secondDash = infoRows.get(x).text().indexOf("-", firstDash+1);
                        int thirdDash = infoRows.get(x).text().indexOf("-", secondDash+1);
                        raceLocation = infoRows.get(x).text().substring(secondDash+1, thirdDash).trim().toUpperCase();
                        break;
                    }
                }
            }
            eventToWrite = new Events(url, club, raceLocation, webFormat.parse(raceDate), points);
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
                    Elements driverCell = results.select("tr > td:containsOwn(" + driverName + ")");
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
                        if(raceDate.contains("2016"))
                        {
                            paxTime = Double.parseDouble(time) * classToWrite.getClass2016Pax();
                        }
                        if(raceDate.contains("2015"))
                        {
                            paxTime = Double.parseDouble(time) * classToWrite.getClass2015Pax();
                        }
                        if(raceDate.contains("2014"))
                        {
                            paxTime = Double.parseDouble(time) * classToWrite.getClass2014Pax();
                        }
                        if(raceDate.contains("2013"))
                        {
                            paxTime = Double.parseDouble(time) * classToWrite.getClass2013Pax();
                        }
                        paxTime = (double)Math.round(paxTime * 1000d)/1000d;
                        
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
            
            String eventDate = "";
            String eventLocation = "";
            String eventClub = "";
            String driverName = "";
            String clsName = "";
            String carName = "";
            int runNumber = 1;
            Classes classToWrite = new Classes();
            
            ArrayList<Runs> runsCollection = new ArrayList();
            
            for(Element row : rows)
            {
                Elements columns = row.select("td");
                if(columns.first().text().length() > 0)
                {
                    if(Character.isDigit(columns.first().text().charAt(0)))
                    {
                        runNumber = 1;
                        clsName = columns.get(1).select("span").text();
                        classToWrite = classesFacade.find(clsName);
                        driverName = columns.get(3).select("span").text();
                        carName = columns.get(4).select("span").text();
                        System.out.println("Class = " + columns.get(1).select("span").text());
                        System.out.println("Driver = " + columns.get(3).select("span").text());
                        System.out.println("Car = " + columns.get(4).select("span").text());
                        
                        for(int x = 5; x < columns.size()-1; x++)
                        {
                            String columnText = columns.get(x).select("span").text();

                            //if the column has a period it must be a time
                            if(columnText.contains("."))
                            {
                                //if the time contains a + sign it must have a cone count or off course
                                if(columnText.contains("+"))
                                {
                                    //check for offcourse
                                    if(columnText.substring(columnText.indexOf("+")+1).equals("OFF"))
                                    {
                                        String runTime = columnText;
                                        double paxTime = 0;
                                        if(eventDate.contains("2016"))
                                        {
                                            paxTime = Double.parseDouble(runTime) * classToWrite.getClass2016Pax();
                                        }
                                        if(eventDate.contains("2015"))
                                        {
                                            paxTime = Double.parseDouble(runTime) * classToWrite.getClass2015Pax();
                                        }
                                        if(eventDate.contains("2014"))
                                        {
                                            paxTime = Double.parseDouble(runTime) * classToWrite.getClass2014Pax();
                                        }
                                        if(eventDate.contains("2013"))
                                        {
                                            paxTime = Double.parseDouble(runTime) * classToWrite.getClass2013Pax();
                                        }
                                        runsCollection.add(new Runs(null, driverName.replace("'", "").toUpperCase(), carName.replace("'", "").toUpperCase(), runNumber, Double.parseDouble(runTime), paxTime, "Y", 0));
                                    }
                                    //else if not off course it must be cones
                                    else
                                    {
                                        int cones = Integer.parseInt(columnText.substring(columnText.indexOf("+")+1));
                                        double runTime = Double.parseDouble(columnText);
                                        runTime += 2 * cones;
                                        double paxTime = 0;
                                        if(eventDate.contains("2016"))
                                        {
                                            paxTime = runTime * classToWrite.getClass2016Pax();
                                        }
                                        if(eventDate.contains("2015"))
                                        {
                                            paxTime = runTime * classToWrite.getClass2015Pax();
                                        }
                                        if(eventDate.contains("2014"))
                                        {
                                            paxTime = runTime * classToWrite.getClass2014Pax();
                                        }
                                        if(eventDate.contains("2013"))
                                        {
                                            paxTime = runTime * classToWrite.getClass2013Pax();
                                        }
                                        runsCollection.add(new Runs(null, driverName.replace("'", "").toUpperCase(), carName.replace("'", "").toUpperCase(), runNumber, runTime, paxTime, "N", cones));
                                    
                                    }
                                }
                                else
                                {
                                    String runTime = columnText;
                                    double paxTime = 0;
                                    if(eventDate.contains("2016"))
                                    {
                                        paxTime = Double.parseDouble(runTime) * classToWrite.getClass2016Pax();
                                    }
                                    if(eventDate.contains("2015"))
                                    {
                                        paxTime = Double.parseDouble(runTime) * classToWrite.getClass2015Pax();
                                    }
                                    if(eventDate.contains("2014"))
                                    {
                                        paxTime = Double.parseDouble(runTime) * classToWrite.getClass2014Pax();
                                    }
                                    if(eventDate.contains("2013"))
                                    {
                                        paxTime = Double.parseDouble(runTime) * classToWrite.getClass2013Pax();
                                    }
                                    runsCollection.add(new Runs(null, driverName.replace("'", "").toUpperCase(), carName.replace("'", "").toUpperCase(), runNumber, Double.parseDouble(runTime), paxTime, "N", 0));
                                    runNumber++;
                                }
                                
                            }
                            //else if the column does not have a period but it says OFF it is an off course
                            else if(columnText.equalsIgnoreCase("OFF"))
                            {
                                runsCollection.add(new Runs(null, driverName.replace("'", "").toUpperCase(), carName.replace("'", "").toUpperCase(), runNumber, 999.999, 999.999, "Y", 0));
                                runNumber++;
                            }
                        }
                    }
                }
                
                /*
                YOU LEFT OFF HERE RYAN
                
                NEED TO ADD TO RUNS COLLECTION FOR THESE EXTRA COLUMN VALUES
                */
                else
                {
                    Elements extraColumns = row.select("td");
                    for(int y = 0; y < extraColumns.size()-1; y++)
                    {
                        String columnText = columns.get(y).select("span").text();

                        if(columnText.contains("."))
                        {
                            System.out.println("Run Time = " + columnText);
                            runNumber++;
                        }

                        else if(columnText.equalsIgnoreCase("OFF"))
                        {
                            System.out.println("Off course = " + columnText);
                            runNumber++;
                        }
                    }
                }
            }
            
            
            
            progress = 100;
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
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
}
