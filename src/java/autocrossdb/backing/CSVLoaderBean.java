/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package autocrossdb.backing;

import autocrossdb.entities.Classes;
import autocrossdb.entities.DriverStats;
import autocrossdb.entities.DriverStatsPK;
import autocrossdb.entities.Events;
import autocrossdb.entities.Runs;
import autocrossdb.facades.ClassesFacade;
import autocrossdb.facades.DriverStatsFacade;
import autocrossdb.facades.EventsFacade;
import autocrossdb.facades.RunsFacade;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.primefaces.model.UploadedFile;

/**
 *
 * @author rmcconville
 */

@ManagedBean(name="csvLoader")
@ViewScoped
public class CSVLoaderBean 
{
    @EJB
    private ClassesFacade classesFacade;
    @EJB
    private RunsFacade runsFacade;
    @EJB
    private EventsFacade eventsFacade;
    @EJB
    private DriverStatsFacade driverStatsFacade;
    @PersistenceContext
    private EntityManager em;

    private UploadedFile file;
    private String club;
    private String location;
    private Date date;
    private String points;
    private String growlMessage;
    private String paxWinner;
    private String rawWinner;
    
    private int totalCones = 0;
    private int totalOffcourse = 0;
    private int runCount = 0;
    private int totalDrivers = 0;
    
    private long progress;
    private long completeProgress;
    private double progDoub;
    
    private Events eventToWrite;
    private Classes classToWrite;
    private Collection<Runs> runsCollection;
    
    private DateFormat webFormat;
    
    @PostConstruct
    public void init()
    {
        webFormat = new SimpleDateFormat("MM-dd-yyyy");
    }
    
    public void upload() 
    {
        //Desired CSV format:
        //driver,class,car,run1,run2,run3,run4,etc
        if(file != null) 
        {
            try
            {
                Scanner reader = new Scanner(file.getInputstream());
                
                eventToWrite = new Events(null, club, location, date, points);
                runsCollection = new ArrayList<Runs>();
                Map<String, DriverStats> statsMap = new HashMap<String, DriverStats>();
                
                while(reader.hasNextLine())
                {
                    String line = reader.nextLine();
                    String[] split = line.split(",");
                    int runNumber = 1;
                    int cones = 0;
                    totalDrivers += 1;
                    String offcourse = "N";
                    classToWrite = classesFacade.find(split[1]);
                    //Each comma separated value at 3 or higher is a run time
                    for(int x = 3; x < split.length; x++)
                    {
                        //Check if the run contains cone count or offcourse
                        if(split[x].contains("+"))
                        {
                            if(split[x].contains("OFF") || split[x].contains("DNF"))
                            {
                                offcourse = "Y";
                                split[x] = split[x].substring(0, split[x].indexOf("+")-1);
                                totalOffcourse += 1;
                            }
                            else
                            {   
                                cones = Integer.parseInt(split[x].substring(split[x].indexOf("+")));
                                split[x] = split[x].substring(0, split[x].indexOf("+")-1);
                                totalCones += cones;
                            }
                        }
                        Runs runToWrite = new Runs(null, split[0], split[2], runNumber, Double.parseDouble(split[x]) + 2 * cones, calculatePax(classToWrite, split[x] + (2 * cones)), offcourse, cones);
                        runToWrite.setRunClassName(classToWrite);
                        runToWrite.setRunEventId(eventToWrite);
                        runsCollection.add(runToWrite);
                        if(runNumber >= runCount)
                        {
                            runCount = runNumber;
                        }
                        runNumber++;
                        
                        
                    }
                    eventToWrite.setRunsCollection(runsCollection);
                    eventToWrite.setEventCones(totalCones);
                    eventToWrite.setEventOffcourses(totalOffcourse);
                    eventToWrite.setEventRunsPer(runCount);
                    eventToWrite.setEventDrivers(totalDrivers);
                    
                }
                
                //check if event already exists
                Events e = (Events) em.createQuery("SELECT e FROM Events e WHERE e.eventDate = :date AND e.eventClub = :club AND e.eventLocation = :location").setParameter("date", date).setParameter("club", club).setParameter("location", location).getResultList().get(0);
                
                if(e == null && eventsFacade.find(eventToWrite.getEventId()) == null)
                {
                    eventsFacade.create(eventToWrite);
                    Iterator<Runs> it = runsCollection.iterator();
                    while(it.hasNext())
                    {
                        runsFacade.edit(it.next());
                        progDoub += 100.0 / completeProgress;
                        progress = Math.round(progDoub);
                    }
                    paxWinner = (String)em.createNamedQuery("Runs.findBestPaxByEvent", Object[].class).setParameter("eventId", eventToWrite.getEventId()).getResultList().get(0)[0];
                    rawWinner = (String)em.createNamedQuery("Runs.findBestRawByEvent", Object[].class).setParameter("eventId", eventToWrite.getEventId()).getResultList().get(0)[0];
                    eventToWrite.setPaxWinner(paxWinner);
                    eventToWrite.setRawWinner(rawWinner);
                    eventsFacade.edit(eventToWrite);
                    
                    Calendar cal =  Calendar.getInstance();
                    cal.setTime(date);  
                    
                    for(Runs r : runsCollection)
                    {
                        DriverStats ds = statsMap.get(r.getRunDriverName());
                        
                        if(ds == null)
                        {
                            ds = new DriverStats();
                            DriverStatsPK dsPk = new DriverStatsPK();
                            
                            dsPk.setDsName(r.getRunDriverName());
                            dsPk.setDsClass(r.getRunClassName().getClassName());
                            dsPk.setDsYear(cal.get(Calendar.YEAR));
                            ds.setDriverStatsPK(dsPk);
                            
                            //only set events, raw winner, and pax winner on the first run
                            if(r.getRunNumber() == 1)
                            {
                                ds.setDsEvents(1);
                                if(dsPk.getDsName().equals(rawWinner))
                                {
                                    ds.setDsRawWins(1);
                                }
                                else
                                {
                                    ds.setDsRawWins(0);
                                }
                                if(dsPk.getDsName().equals(paxWinner))
                                {
                                    ds.setDsPaxWins(1);
                                }
                                else
                                {
                                    ds.setDsPaxWins(0);
                                }
                            }
                            ds.setDsCones(r.getRunCones());
                            if(r.getRunOffcourse().equals("Y"))
                            {
                                ds.setDsOffcourses(1);
                            }
                            else
                            {
                                ds.setDsOffcourses(0);
                            }
                            ds.setDsRuns(1);
                            
                            statsMap.put(r.getRunDriverName(), ds);
                        }
                        else
                        {
                            DriverStats dsNew = new DriverStats(ds);
                            dsNew.setDsCones(dsNew.getDsCones() + r.getRunCones());
                            if(r.getRunOffcourse().equals("Y"))
                            {
                                dsNew.setDsOffcourses(dsNew.getDsOffcourses() + 1);
                            }
                            dsNew.setDsCones(dsNew.getDsCones() + 1);
                            
                            statsMap.put(r.getRunDriverName(), dsNew);
                        }
                    }
                    
                    for(String key : statsMap.keySet())
                    {
                        DriverStats tempDs = statsMap.get(key);
                        if(driverStatsFacade.find(tempDs.getDriverStatsPK()) == null)
                        {
                            //brand new driver stat entry
                            driverStatsFacade.create(tempDs);
                        }
                        else
                        {
                            DriverStats existingDs = (DriverStats)em.createQuery("SELECT d from Driver_Stats d WHERE d.dsName = :name AND d.dsYear = :year AND d.dsClass = :class").setParameter("name", tempDs.getDriverStatsPK().getDsName()).setParameter("year", tempDs.getDriverStatsPK().getDsYear()).setParameter("class", tempDs.getDriverStatsPK().getDsClass()).getResultList().get(0);
                            driverStatsFacade.edit(combineDriverStats(tempDs, existingDs));
                        }
                    }
               
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
                FacesMessage message = new FacesMessage("Error", "Exception while loading " + file.getFileName());
                FacesContext.getCurrentInstance().addMessage(null, message);
            }
            
        }
    }
    
    private static DriverStats combineDriverStats(DriverStats ds1, DriverStats ds2)
    {
        DriverStats newDs = new DriverStats();
        newDs.setDriverStatsPK(ds1.getDriverStatsPK());
        newDs.setDsCones(ds1.getDsCones() + ds2.getDsCones());
        newDs.setDsEvents(ds1.getDsEvents() + ds2.getDsEvents());
        newDs.setDsOffcourses(ds1.getDsOffcourses() + ds2.getDsOffcourses());
        newDs.setDsPaxWins(ds1.getDsPaxWins() + ds2.getDsPaxWins());
        newDs.setDsRawWins(ds1.getDsRawWins() + ds2.getDsRawWins());
        newDs.setDsRuns(ds1.getDsRuns() + ds2.getDsRuns());
        return newDs;
    }
    
    public double calculatePax(Classes classToWrite, String runTime)
    {
        double paxTime = 0;
        if(webFormat.format(date).contains("2017"))
        {
            paxTime = Double.parseDouble(runTime) * classToWrite.getClass2017Pax();
        }
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

    public UploadedFile getFile() {
        return file;
    }

    public void setFile(UploadedFile file) {
        this.file = file;
    }
    
    
}
