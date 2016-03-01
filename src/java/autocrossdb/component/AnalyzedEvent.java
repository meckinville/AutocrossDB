/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package autocrossdb.component;

import autocrossdb.entities.Events;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 *
 * @author rmcconville
 */
public class AnalyzedEvent
{
    private Events neglectedEvent;
    
    private int totalDrivers;
    private double avgRunTime;
    private long totalCones;
    
    private String topRawName;
    private String topRawTime;
    private String topRawCar;
    private String topRawClass;
    private String topPaxName;
    private String topPaxTime;
    private String topPaxCar;
    private String topPaxClass;
    private String topConeKillerName;
    private String topConeKillerCones;
    private String topConeKillerCar;
    private String topConeKillerClass;
    private String noviceChampName;
    private String noviceChampTime;
    private String noviceChampCar;
    private String noviceChampClass;
    
    private EntityManagerFactory emf;
    private EntityManager em;
    
    
    public void AnalyzedEvent()
    {
        
    }
    
    public AnalyzedEvent(Events e)
    {
        emf = Persistence.createEntityManagerFactory("AutoxDBPU");
        em = emf.createEntityManager();
        this.neglectedEvent = e;
        this.totalDrivers = em.createNamedQuery("Runs.findTotalDriversAtEvent", Object[].class).setParameter("eventUrl", e.getEventUrl()).getResultList().size();
        List<Double> doubleResults = em.createQuery("SELECT min(r.runTime) FROM Runs r where r.runEventUrl.eventUrl = :eventUrl and r.runOffcourse='N' group by r.runDriverName order by min(r.runTime) asc", Double.class).setParameter("eventUrl", e.getEventUrl()).getResultList();
        double sum = 0;
        for(Double d : doubleResults)
        {
            sum += d;
        }
        double tempAvg = sum / doubleResults.size();
        tempAvg = (double)Math.round(tempAvg * 1000d)/1000d;
        this.avgRunTime = tempAvg;
        
        List<Long> coneResults = em.createNamedQuery("Runs.findTotalConesHitAtEvent", Long.class).setParameter("eventUrl", e.getEventUrl()).getResultList();
        totalCones = coneResults.get(0);
    }
    
    public void analyzeEvent()
    {
        List<Object[]> rawResults = em.createNamedQuery("Runs.findBestRawByEvent", Object[].class).setParameter("eventUrl", neglectedEvent.getEventUrl()).getResultList();
        this.topRawName = rawResults.get(0)[0].toString();
        this.topRawCar = rawResults.get(0)[1].toString();
        this.topRawClass = rawResults.get(0)[2].toString();
        this.topRawTime = rawResults.get(0)[3].toString();
        
        List<Object[]> paxResults = em.createNamedQuery("Runs.findBestPaxByEvent", Object[].class).setParameter("eventUrl", neglectedEvent.getEventUrl()).getResultList();
        this.topPaxName = paxResults.get(0)[0].toString();
        this.topPaxCar = paxResults.get(0)[1].toString();
        this.topPaxClass = paxResults.get(0)[2].toString();
        this.topPaxTime = paxResults.get(0)[3].toString();
        
        List<Object[]> coneKillerResults = em.createNamedQuery("Runs.findTopConeKiller", Object[].class).setParameter("eventUrl", neglectedEvent.getEventUrl()).getResultList();
        this.topConeKillerName = coneKillerResults.get(0)[0].toString();
        this.topConeKillerCar = coneKillerResults.get(0)[1].toString();
        this.topConeKillerClass = coneKillerResults.get(0)[2].toString();
        this.topConeKillerCones = coneKillerResults.get(0)[3].toString();
        
        List<Object[]> noviceResults = em.createNamedQuery("Runs.findNoviceChamp", Object[].class).setParameter("eventUrl", neglectedEvent.getEventUrl()).getResultList();
        if(noviceResults.size() == 0)
        {
            this.noviceChampName = "No this.novices.";
            this.noviceChampTime = "N/A";
            this.noviceChampClass = "N/A";
            this.noviceChampCar = "N/A";
        }
        else
        {
            this.noviceChampName = noviceResults.get(0)[0].toString();
            this.noviceChampCar = noviceResults.get(0)[1].toString();
            this.noviceChampClass = noviceResults.get(0)[2].toString();
            this.noviceChampTime = noviceResults.get(0)[3].toString();
        }
    }


    public String getTopRawName() {
        return topRawName;
    }

    public void setTopRawName(String topRawName) {
        this.topRawName = topRawName;
    }

    public String getTopRawTime() {
        return topRawTime;
    }

    public void setTopRawTime(String topRawTime) {
        this.topRawTime = topRawTime;
    }

    public String getTopRawCar() {
        return topRawCar;
    }

    public void setTopRawCar(String topRawCar) {
        this.topRawCar = topRawCar;
    }

    public String getTopRawClass() {
        return topRawClass;
    }

    public void setTopRawClass(String topRawClass) {
        this.topRawClass = topRawClass;
    }

    public String getTopPaxName() {
        return topPaxName;
    }

    public void setTopPaxName(String topPaxName) {
        this.topPaxName = topPaxName;
    }

    public String getTopPaxTime() {
        return topPaxTime;
    }

    public void setTopPaxTime(String topPaxTime) {
        this.topPaxTime = topPaxTime;
    }

    public String getTopPaxCar() {
        return topPaxCar;
    }

    public void setTopPaxCar(String topPaxCar) {
        this.topPaxCar = topPaxCar;
    }

    public String getTopPaxClass() {
        return topPaxClass;
    }

    public void setTopPaxClass(String topPaxClass) {
        this.topPaxClass = topPaxClass;
    }

    public String getTopConeKillerName() {
        return topConeKillerName;
    }

    public void setTopConeKillerName(String topConeKillerName) {
        this.topConeKillerName = topConeKillerName;
    }

    public String getTopConeKillerCones() {
        return topConeKillerCones;
    }

    public void setTopConeKillerCones(String topConeKillerCones) {
        this.topConeKillerCones = topConeKillerCones;
    }

    public String getTopConeKillerCar() {
        return topConeKillerCar;
    }

    public void setTopConeKillerCar(String topConeKillerCar) {
        this.topConeKillerCar = topConeKillerCar;
    }

    public String getTopConeKillerClass() {
        return topConeKillerClass;
    }

    public void setTopConeKillerClass(String topConeKillerClass) {
        this.topConeKillerClass = topConeKillerClass;
    }

    public String getNoviceChampName() {
        return noviceChampName;
    }

    public void setNoviceChampName(String noviceChampName) {
        this.noviceChampName = noviceChampName;
    }

    public String getNoviceChampTime() {
        return noviceChampTime;
    }

    public void setNoviceChampTime(String noviceChampTime) {
        this.noviceChampTime = noviceChampTime;
    }

    public String getNoviceChampCar() {
        return noviceChampCar;
    }

    public void setNoviceChampCar(String noviceChampCar) {
        this.noviceChampCar = noviceChampCar;
    }

    public String getNoviceChampClass() {
        return noviceChampClass;
    }

    public void setNoviceChampClass(String noviceChampClass) {
        this.noviceChampClass = noviceChampClass;
    }

    public Events getNeglectedEvent() {
        return neglectedEvent;
    }

    public void setNeglectedEvent(Events neglectedEvent) {
        this.neglectedEvent = neglectedEvent;
    }

    public int getTotalDrivers() {
        return totalDrivers;
    }

    public void setTotalDrivers(int totalDrivers) {
        this.totalDrivers = totalDrivers;
    }

    public double getAvgRunTime() {
        return avgRunTime;
    }

    public void setAvgRunTime(double avgRunTime) {
        this.avgRunTime = avgRunTime;
    }

    public long getTotalCones() {
        return totalCones;
    }

    public void setTotalCones(long totalCones) {
        this.totalCones = totalCones;
    }

    
    
    
    
}
