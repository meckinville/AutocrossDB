/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package autocrossdb.backing;

import autocrossdb.component.Nemesis;
import autocrossdb.entities.Runs;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author rmcconville
 */
@ManagedBean(name="nemesis")
@ViewScoped
public class NemesisBean 
{
    private String driver;
    private List<Nemesis> nemesisList;
    
    private Date startDate;
    private Date endDate;
    
    private long progress;
    
    @PersistenceContext
    private EntityManager em;
    
    @PostConstruct
    public void init()
    {
        Calendar now = Calendar.getInstance();
        endDate = now.getTime();
        now.set(Calendar.MONTH, now.get(Calendar.MONTH)-12);
        startDate = now.getTime();
        progress = 0;
    }
    
    public void findNemesis()
    {
        
        Map<String, Nemesis> nemesisMap = new LinkedHashMap();
        List<Object[]> ourRuns = em.createQuery("SELECT min(r.runTime), min(r.runPaxTime), r.runEventUrl, r.runClassName.className FROM Runs r WHERE r.runDriverName = :driver AND r.runOffcourse = 'N' and r.runEventUrl.eventDate > :startDate and r.runEventUrl.eventDate < :endDate group by r.runEventUrl order by r.runEventUrl.eventDate ASC ").setParameter("driver", driver).setParameter("startDate", startDate).setParameter("endDate", endDate).getResultList();
        
        
        List<List<Object[]>> allEvents = new ArrayList();
        for(Object[] o : ourRuns)
        {
            progress += 10;
            List<Object[]> tempList = new ArrayList();
            
            //find all drivers within 1 second raw
            tempList = em.createQuery("SELECT min(r.runTime), min(r.runPaxTime), r.runDriverName, r.runEventUrl, r.runClassName.className FROM Runs r WHERE r.runDriverName != :driver AND r.runOffcourse = 'N' AND r.runEventUrl = :event group by r.runDriverName, r.runEventUrl having ((:ourTime - min(r.runTime)) < 1) AND ((:ourTime - min(r.runTime)) > -1) order by r.runEventUrl.eventDate asc").setParameter("driver", driver).setParameter("event", o[2]).setParameter("ourTime", o[0]).getResultList();
            
            /*
            something is wrong in here. not sure exactly what.
            if i do a nemesis finder on myself i find that terry is my biggest nemesis
            but it says we've only attended one event together.
            */
            for(Object[] x : tempList)
            {
                Nemesis temp = nemesisMap.get(x[2].toString());
                
                double rawDiff = (double)o[0] - (double)x[0];
                double paxDiff = (double)o[1] - (double)x[1];
                
                if(temp == null)
                {
                    temp = new Nemesis(x[2].toString(), rawDiff, paxDiff);
                    nemesisMap.put(x[2].toString(), temp);
                }
                else
                {
                    temp.setRawDiff(temp.getRawDiff() + rawDiff);
                    temp.setPaxDiff(temp.getPaxDiff() + paxDiff);
                    temp.setEventsTogether(temp.getEventsTogether() + 1);
                }
            }
        }
        
        nemesisList = new ArrayList();
        for(String key : nemesisMap.keySet())
        {
            if(nemesisMap.get(key).getValue() < 1)
            {
                nemesisList.add(nemesisMap.get(key));
            }
            
        }
        Collections.sort(nemesisList);
        progress = 100;
    }
    
    public List<String> completeDriverText(String query)
    {
        List<String> results = new ArrayList<String>();
        List<String> driverList = em.createQuery("SELECT distinct(r.runDriverName) FROM Runs r ", String.class).getResultList();
        for(int x = 0; x < driverList.size(); x++)
        {
            if(driverList.get(x).contains(query.toUpperCase()))
            {
                results.add(driverList.get(x));
            }
        }
        return results;
    }

    public void onCompleteLoad()
    {
        progress = 0;
    }
    
    public String getDriver() {
        return driver;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }

    public long getProgress() {
        return progress;
    }

    public void setProgress(long progress) {
        this.progress = progress;
    }

    public List<Nemesis> getNemesisList() {
        return nemesisList;
    }

    public void setNemesisList(List<Nemesis> nemesisList) {
        this.nemesisList = nemesisList;
    }
    
    
}
