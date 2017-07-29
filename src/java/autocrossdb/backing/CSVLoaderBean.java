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
import autocrossdb.facades.EventsFacade;
import autocrossdb.facades.RunsFacade;
import java.io.File;
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
                while(reader.hasNextLine())
                {
                    String line = reader.nextLine();
                    String[] split = line.split(",");
                    int runNumber = 1;
                    int cones = 0;
                    String offcourse = "N";
                    classToWrite = new Classes(split[1]);
                    //Each comma separated value at 3 or higher is a run time
                    for(int x = 3; x < split.length; x++)
                    {
                        //Check if the run contains cone count or offcourse
                        if(split[x].contains("+"))
                        {
                            if(split[x].contains("OFF"))
                            {
                                offcourse = "Y";
                                split[x] = split[x].substring(0, split[x].indexOf("+")-1);
                            }
                            else
                            {   
                                cones = Integer.parseInt(split[x].substring(split[x].indexOf("+")));
                                split[x] = split[x].substring(0, split[x].indexOf("+")-1);
                            }
                        }
                        
                        runsCollection.add(new Runs(null, split[0], split[2], runNumber, Double.parseDouble(split[x]), calculatePax(classToWrite, split[x]), offcourse, cones));
                        runNumber++;
                    }
                    eventToWrite.setRunsCollection(runsCollection);
                    
                    //update driver stats
                    Map<String, DriverStats> statsMap = new HashMap<String, DriverStats>();
                    paxWinner = (String)em.createNamedQuery("Runs.findBestPaxByEvent", Object[].class).setParameter("eventUrl", eventToWrite.getEventId()).getResultList().get(0)[0];
                    rawWinner = (String)em.createNamedQuery("Runs.findBestRawByEvent", Object[].class).setParameter("eventUrl", eventToWrite.getEventId()).getResultList().get(0)[0];
                    
                    for(Runs r : runsCollection)
                    {
                        DriverStats ds = statsMap.get(r.getRunDriverName());

                        if(ds == null)
                        {
                            ds = new DriverStats();
                            DriverStatsPK dsPk = new DriverStatsPK();
                            
                            dsPk.setDsName(r.getRunDriverName());
                            dsPk.setDsClass(r.getRunClassName().getClassName());
                            Calendar cal =  Calendar.getInstance();
                            cal.setTime(date);
                            dsPk.setDsYear(cal.get(Calendar.YEAR));
                            
                            ds.setDriverStatsPK(dsPk);
                            
                            /*
                            
                            
                                LEFT OFF HERE
                            
                            TODO
                            
                            */
                            
                            
                            
                        }
                        else
                        {
                            
                        }
                    }
                    
                }
                if(eventsFacade.find(eventToWrite.getEventId()) == null)
                {
                    eventsFacade.create(eventToWrite);
                    Iterator<Runs> it = runsCollection.iterator();
                    while(it.hasNext())
                    {
                        runsFacade.edit(it.next());
                        progDoub += 100.0 / completeProgress;
                        progress = Math.round(progDoub);
                    }
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
                FacesMessage message = new FacesMessage("Error", "Exception while loading " + file.getFileName());
                FacesContext.getCurrentInstance().addMessage(null, message);
            }
            
        }
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
