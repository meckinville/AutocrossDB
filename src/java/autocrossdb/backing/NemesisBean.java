/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package autocrossdb.backing;

import autocrossdb.component.Nemesis;
import autocrossdb.entities.Runs;
import java.util.ArrayList;
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
    
    private long progress;
    
    @PersistenceContext
    private EntityManager em;
    
    @PostConstruct
    public void init()
    {
        progress = 0;
    }
    
    public void findNemesis()
    {
        
        Map<String, Nemesis> nemesisMap = new LinkedHashMap();
        List<Object[]> ourRuns = em.createQuery("SELECT min(r.runTime), r.runEventUrl FROM Runs r WHERE r.runDriverName = :driver AND r.runOffcourse = 'N' group by r.runEventUrl order by r.runEventUrl.eventDate ASC ").setParameter("driver", driver).getResultList();
        
        
        List<List<Object[]>> allEvents = new ArrayList();
        for(Object[] o : ourRuns)
        {
            List<Object[]> tempList = new ArrayList();
            tempList = em.createQuery("SELECT min(r.runTime), r.runDriverName, r.runEventUrl FROM Runs r WHERE r.runDriverName != :driver AND r.runOffcourse = 'N' AND r.runEventUrl = :event group by r.runDriverName, r.runEventUrl having ((:ourTime - min(r.runTime)) < 1) AND ((:ourTime - min(r.runTime)) > -1) order by r.runEventUrl.eventDate asc").setParameter("driver", driver).setParameter("event", o[1]).setParameter("ourTime", o[0]).getResultList();
            for(Object[] x : tempList)
            {
                Nemesis temp = nemesisMap.get(x[1].toString());
                if(temp == null)
                {
                    temp = new Nemesis(x[1].toString(), 1);
                    nemesisMap.put(x[1].toString(), temp);
                }
                else
                {
                    temp.setValue(temp.getValue() + 1);
                }
            }
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
