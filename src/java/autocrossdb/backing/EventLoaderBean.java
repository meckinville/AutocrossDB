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
            Date raceDate;
            
            Elements tables = results.select("table");
            /*
            Element headerTable = tables.first();
            Elements dateRows = headerTable.select("span:matchesOwn([0-9][0-9]-[0-9][0-9]");
            Element dateSpan = dateRows.first();
            String dateSubstring = dateSpan.text().substring(dateSpan.text().length()-10);
            dateSubstring = dateSubstring.trim();
            while(!Character.isDigit(dateSubstring.charAt(0)))
            {
                dateSubstring = dateSubstring.substring(1);
            }
            if(dateSubstring.substring(0,dateSubstring.indexOf("-")).length() < 2)
            {
                dateSubstring = "0" + dateSubstring;
            }
            if(dateSubstring.length() == 8)
            {
                raceDate = shortFormat.parse(dateSubstring);
            }
            else
            {
                raceDate = webFormat.parse(dateSubstring);
            }
            eventToWrite = new Events(url, club, null, raceDate, points);
            */
            Element driverTable = tables.get(1);
            Elements driverRows = driverTable.select("tr");
            String driverName = "";
            String carName = "";
            String clsStr = "";
            
            for(Element row : driverRows)
            {
                Elements driverCells = row.select("td");
                for(Element cell : driverCells)
                {
                    if(cell.hasAttr("colspan"))
                    {
                        clsStr = cell.select("a[name]").first().attr("name");
                        System.out.println(clsStr);
                        break;
                    }
                    else if(cell.text().contains(" "))
                    {
                        driverName = cell.text();
                        System.out.println("Driver Name: " + driverName);
                    }
                }
            }
            
            //regex to find run times .?[0-9][0-9][.][0-9][0-9][0-9]
            
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
