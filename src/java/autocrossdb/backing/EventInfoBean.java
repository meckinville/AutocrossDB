/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package autocrossdb.backing;

import autocrossdb.entities.Events;
import autocrossdb.entities.Runs;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.model.SelectItem;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

/**
 *
 * @author rmcconville
 */

@ManagedBean(name="eventInfo")
public class EventInfoBean 
{
    private String event;
    private List<SelectItem> events;
    private int totalDrivers;
    private String averageTime;
    private String topRawName;
    private String topRawTime;
    private String topRawCar;
    private String topRawClass;
    private String topPaxName;
    private String topPaxTime;
    private String topPaxCar;
    private String topPaxClass;
    private String totalCones;
    private String topConeKillerName;
    private String topConeKillerCones;
    private String topConeKillerCar;
    private String topConeKillerClass;
    private String noviceChampName;
    private String noviceChampTime;
    private String noviceChampCar;
    private String noviceChampClass;
    @PersistenceContext
    private EntityManager em;
    
    private DateFormat webFormat;
    

    @PostConstruct
    public void init()
    {
        webFormat = new SimpleDateFormat("MM-dd-yyyy");
        try
        {
            TypedQuery<Events> query = em.createNamedQuery("Events.findAll", Events.class);
            List<Events> eventsList = query.getResultList();
            events = new ArrayList<SelectItem>();
            for(int x = 0; x < eventsList.size(); x++)
            {
                events.add(new SelectItem(eventsList.get(x).getEventUrl(), eventsList.get(x).getEventClubName() + " " + webFormat.format(eventsList.get(x).getEventDate()) + " " + eventsList.get(x).getEventLocation()));
            }
            
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        
    }
    
    public void loadInfo()
    {
        if(event != null)
        {
            List<Object[]> intQuery = em.createNamedQuery("Runs.findTotalDriversAtEvent", Object[].class).setParameter("eventUrl", event).getResultList();
            totalDrivers = intQuery.size();
            
            TypedQuery<Double> doubleQuery = em.createQuery("SELECT min(r.runTime) FROM Runs r where r.runEventUrl.eventUrl = :eventUrl and r.runOffcourse='N' group by r.runDriverName order by min(r.runTime) asc", Double.class).setParameter("eventUrl", event);
            List<Double> doubleResults = doubleQuery.getResultList();
            double sum = 0;
            for(Double d : doubleResults)
            {
                sum += d;
            }
            double tempAvg = sum / doubleResults.size();
            tempAvg = (double)Math.round(tempAvg * 1000d)/1000d;
            averageTime = String.valueOf(tempAvg);
            
            TypedQuery<Object[]> objectQuery = em.createNamedQuery("Runs.findBestRawByEvent", Object[].class).setParameter("eventUrl", event);
            List<Object[]> objectResults = objectQuery.getResultList();
            topRawName = objectResults.get(0)[0].toString();
            topRawCar = objectResults.get(0)[1].toString();
            topRawClass = objectResults.get(0)[2].toString();
            topRawTime = objectResults.get(0)[3].toString();
            
            objectQuery = em.createNamedQuery("Runs.findBestPaxByEvent", Object[].class).setParameter("eventUrl", event);
            objectResults = objectQuery.getResultList();
            topPaxName = objectResults.get(0)[0].toString();
            topPaxCar = objectResults.get(0)[1].toString();
            topPaxClass = objectResults.get(0)[2].toString();
            topPaxTime = objectResults.get(0)[3].toString();
            
            TypedQuery<Long> longQuery = em.createNamedQuery("Runs.findTotalConesHitAtEvent", Long.class).setParameter("eventUrl", event);
            List<Long> longResults = longQuery.getResultList();
            totalCones = String.valueOf(longResults.get(0));
            
            objectQuery = em.createNamedQuery("Runs.findTopConeKiller", Object[].class).setParameter("eventUrl", event);
            objectResults = objectQuery.getResultList();
            topConeKillerName = objectResults.get(0)[0].toString();
            topConeKillerCones = objectResults.get(0)[1].toString();
            topConeKillerCar = objectResults.get(0)[2].toString();
            topConeKillerClass = objectResults.get(0)[3].toString();
            
            objectQuery = em.createNamedQuery("Runs.findNoviceChamp", Object[].class).setParameter("eventUrl", event);
            objectResults = objectQuery.getResultList();
            if(objectResults.size() == 0)
            {
                noviceChampName = "No novices.";
                noviceChampTime = "N/A";
                noviceChampClass = "N/A";
                noviceChampCar = "N/A";
            }
            else
            {
                noviceChampName = objectResults.get(0)[0].toString();
                noviceChampCar = objectResults.get(0)[1].toString();
                noviceChampClass = objectResults.get(0)[2].toString();
                noviceChampTime = objectResults.get(0)[3].toString();
            }
            
        }
    }
    
    /*
    public void loadInfo()
    {
        if(event != null)
        {
            try
            {
                Statement stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery("select avg(fastest) from (select run_driver_name, min(run_time) as fastest from runs where run_event_url='" + event + "' and run_offcourse='false' group by run_driver_name order by min(run_time) asc) run2");
                if(rs.next())
                {
                    double tempAvgTime = rs.getDouble("avg(fastest)");
                    tempAvgTime = (double)Math.round(tempAvgTime * 1000d) / 1000d;
                    averageTime = Double.toString(tempAvgTime);
                }
                rs = stmt.executeQuery("select run_driver_name, run_car_name, run_class_name, min(run_time) from runs where run_event_url='" + event + "' and run_offcourse='false' group by run_driver_name order by min(run_time) asc");
                if(rs.next())
                {
                    topRaw = rs.getString("run_class_name") + " " + rs.getString("run_car_name") + " " + rs.getString("run_driver_name") + " " + rs.getDouble("min(run_time)");
                }
                rs = stmt.executeQuery("select run_driver_name, run_car_name, run_class_name, min(run_pax_time) from runs where run_event_url = '" + event + "' and run_offcourse='false' group by run_driver_name order by min(run_pax_time) asc");
                if(rs.next())
                {
                    topPax = rs.getString("run_class_name") + " " + rs.getString("run_car_name") + " " + rs.getString("run_driver_name") + " " + rs.getDouble("min(run_pax_time)");
                }
                rs = stmt.executeQuery("select sum(run_cones) from runs where run_event_url = '" + event + "'");
                if(rs.next())
                {
                    totalCones = Integer.toString(rs.getInt("sum(run_cones)"));
                }         
                rs = stmt.executeQuery("select run_driver_name, sum(run_cones) from runs where run_event_url = '" + event + "' group by run_driver_name order by sum(run_cones) desc");
                if(rs.next())
                {
                    topConeKiller = rs.getString("run_driver_name") + " " + rs.getInt("sum(run_cones)");
                }
                rs = stmt.executeQuery("select run_driver_name, run_car_name, run_class_name, min(run_time) from runs where run_event_url='" + event + "' and run_offcourse='false' and run_class_name='NS' group by run_driver_name order by min(run_time) asc");
                if(rs.next())
                {
                    noviceChamp = rs.getString("run_class_name") + " " + rs.getString("run_car_name") + " " + rs.getString("run_driver_name") + " " + rs.getDouble("min(run_time)");
                }
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
            
            
        }
    }
*/
    public String getEvent() {
        return event;
    }

    public int getTotalDrivers() {
        return totalDrivers;
    }

    public void setTotalDrivers(int totalDrivers) {
        this.totalDrivers = totalDrivers;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public List<SelectItem> getEvents() {
        return events;
    }

    public String getAverageTime() {
        return averageTime;
    }

    public void setAverageTime(String averageTime) {
        this.averageTime = averageTime;
    }

    public String getTotalCones() {
        return totalCones;
    }

    public void setTotalCones(String totalCones) {
        this.totalCones = totalCones;
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

   
    
}
 