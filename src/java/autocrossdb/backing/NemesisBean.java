/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package autocrossdb.backing;

import autocrossdb.component.Nemesis;
import autocrossdb.entities.Classes;
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
    private String driverClass;
    private String nemesisType;
    private List<Nemesis> nemesisList;
    private List<String> driverClassList;
    
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
        driverClassList = new ArrayList();
        driverClassList.add("Select Driver First");
        progress = 0;
    }
    
    public void findNemesis()
    {
        if(nemesisType != null)
        {
            Map<String, Nemesis> nemesisMap = new LinkedHashMap();
            List<Object[]> ourRuns = em.createQuery("SELECT min(r.runTime), min(r.runPaxTime), r.runEventUrl, r.runClassName FROM Runs r WHERE r.runDriverName = :driver AND r.runOffcourse = 'N' and r.runEventUrl.eventDate > :startDate and r.runEventUrl.eventDate < :endDate and r.runClassName.className = :class group by r.runEventUrl order by r.runEventUrl.eventDate ASC ").setParameter("driver", driver).setParameter("startDate", startDate).setParameter("endDate", endDate).setParameter("class", driverClass).getResultList();


            for(Object[] o : ourRuns)
            {
                progress += 10;
                List<Object[]> tempList = new ArrayList();

                //find all drivers within 1 second raw
                //tempList = em.createQuery("SELECT min(r.runTime), min(r.runPaxTime), r.runDriverName, r.runEventUrl, r.runClassName.className FROM Runs r WHERE r.runDriverName != :driver AND r.runOffcourse = 'N' AND r.runEventUrl = :event group by r.runDriverName, r.runEventUrl having ((:ourTime - min(r.runTime)) < 1) AND ((:ourTime - min(r.runTime)) > -1) order by r.runEventUrl.eventDate asc").setParameter("driver", driver).setParameter("event", o[2]).setParameter("ourTime", o[0]).getResultList();
                tempList = em.createQuery("SELECT min(r.runTime), min(r.runPaxTime), r.runDriverName, r.runEventUrl, r.runClassName, r.runCarName FROM Runs r WHERE r.runDriverName != :driver AND r.runOffcourse = 'N' AND r.runEventUrl = :event group by r.runDriverName, r.runEventUrl order by r.runEventUrl.eventDate asc").setParameter("driver", driver).setParameter("event", o[2]).getResultList();

                for(Object[] x : tempList)
                {
                    Classes tempCls = (Classes)x[4];
                    Nemesis temp = nemesisMap.get(x[2].toString() + " [" + tempCls.getClassName() + "]");

                    double rawDiff = (double)o[0] - (double)x[0];
                    double paxDiff = (double)o[1] - (double)x[1];

                    if(temp == null)
                    {
                        temp = new Nemesis(x[2].toString(), rawDiff, paxDiff, x[5].toString(), (Classes)x[4]);
                        nemesisMap.put(x[2].toString() + " [" + tempCls.getClassName() + "]", temp);
                    }
                    else
                    {
                        temp.addRawDiff(rawDiff);
                        temp.addPaxDiff(paxDiff);
                        temp.addCarDriven(x[5].toString());
                        temp.setEventsTogether(temp.getEventsTogether() + 1);
                    }
                }
            }

            nemesisList = new ArrayList();
            for(String key : nemesisMap.keySet())
            {
                if(nemesisType.equals("Raw"))
                {
                    nemesisMap.get(key).calculateRawValue();
                }

                if(nemesisType.equals("Pax"))
                {
                    nemesisMap.get(key).calculatePaxValue();
                }
                
                if(nemesisMap.get(key).getValue() < 2 && nemesisMap.get(key).getValue() > -2 && nemesisMap.get(key).getEventsTogether() > 2)
                {
                    nemesisList.add(nemesisMap.get(key));
                }
            }
            Collections.sort(nemesisList);
            int rankIndex = 1;
            for(Nemesis n : nemesisList)
            {
               if(rankIndex == 21)
               {
                   break;
               }
               n.setRank(rankIndex);
               rankIndex++;
            }
            if(nemesisList.size() > 19)
            {
                nemesisList = nemesisList.subList(0,20);
            }
            progress = 100;
        }
        else
        {
            progress = 100;
        }
        
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
    
    public void onDriverSelect()
    {
        driverClassList = em.createQuery("SELECT DISTINCT r.runClassName.className from Runs r where r.runDriverName = :driver AND r.runEventUrl.eventDate > :startDate AND r.runEventUrl.eventDate < :endDate").setParameter("driver", driver).setParameter("startDate", startDate).setParameter("endDate", endDate).getResultList();
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

    public String getNemesisType() {
        return nemesisType;
    }

    public void setNemesisType(String nemesisType) {
        this.nemesisType = nemesisType;
    }

    public String getDriverClass() {
        return driverClass;
    }

    public void setDriverClass(String driverClass) {
        this.driverClass = driverClass;
    }

    public List<String> getDriverClassList() {
        return driverClassList;
    }

    public void setDriverClassList(List<String> driverClassList) {
        this.driverClassList = driverClassList;
    }

    
    
    
}
