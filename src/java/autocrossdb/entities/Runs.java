/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package autocrossdb.entities;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author rmcconville
 */
@Entity
@Table(name = "runs")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Runs.findAll", query = "SELECT r FROM Runs r"),
    @NamedQuery(name = "Runs.findByRunId", query = "SELECT r FROM Runs r WHERE r.runId = :runId"),
    @NamedQuery(name = "Runs.findByRunDriverName", query = "SELECT r FROM Runs r WHERE r.runDriverName = :runDriverName"),
    @NamedQuery(name = "Runs.findByRunCarName", query = "SELECT r FROM Runs r WHERE r.runCarName = :runCarName"),
    @NamedQuery(name = "Runs.findByRunNumber", query = "SELECT r FROM Runs r WHERE r.runNumber = :runNumber"),
    @NamedQuery(name = "Runs.findByRunTime", query = "SELECT r FROM Runs r WHERE r.runTime = :runTime"),
    @NamedQuery(name = "Runs.findByRunPaxTime", query = "SELECT r FROM Runs r WHERE r.runPaxTime = :runPaxTime"),
    @NamedQuery(name = "Runs.findByRunOffcourse", query = "SELECT r FROM Runs r WHERE r.runOffcourse = :runOffcourse"),
    @NamedQuery(name = "Runs.findByRunCones", query = "SELECT r FROM Runs r WHERE r.runCones = :runCones"),
    @NamedQuery(name = "Runs.findBestRawByEvent", query = "SELECT r.runDriverName, r.runCarName, r.runClassName.className, min(r.runTime) FROM Runs r where r.runEventUrl.eventUrl = :eventUrl AND r.runOffcourse = 'N' GROUP BY r.runDriverName ORDER BY min(r.runTime) asc"),
    @NamedQuery(name = "Runs.findTotalDriversAtEvent", query = "SELECT distinct(r.runDriverName) from Runs r where r.runEventUrl.eventUrl = :eventUrl"),
    @NamedQuery(name = "Runs.findBestPaxByEvent", query = "SELECT r.runDriverName, r.runCarName, r.runClassName.className, min(r.runPaxTime) FROM Runs r where r.runEventUrl.eventUrl = :eventUrl AND r.runOffcourse = 'N' GROUP BY r.runDriverName ORDER BY min(r.runPaxTime) asc"),
    @NamedQuery(name = "Runs.findTotalConesHitAtEvent", query = "SELECT sum(r.runCones) FROM Runs r where r.runEventUrl.eventUrl = :eventUrl"),
    @NamedQuery(name = "Runs.findTopConeKiller", query = "SELECT r.runDriverName, sum(r.runCones), r.runCarName, r.runClassName.className FROM Runs r where r.runEventUrl.eventUrl = :eventUrl GROUP BY r.runDriverName ORDER BY sum(r.runCones) desc"),
    @NamedQuery(name = "Runs.findNoviceChamp", query = "SELECT r.runDriverName, r.runCarName, r.runClassName.className, min(r.runTime) FROM Runs r where r.runEventUrl.eventUrl = :eventUrl AND r.runClassName.className = 'NS' AND r.runOffcourse = 'N' GROUP BY r.runDriverName ORDER BY min(r.runTime) asc"),
    @NamedQuery(name = "Runs.findBestRunForDriver", query = "SELECT min(r.runTime) FROM Runs r where r.runEventUrl.eventUrl = :eventUrl AND r.runDriverName = :driverName AND r.runOffcourse = 'N'"),
    @NamedQuery(name = "Runs.findCommonEventsForDriversRaw", query = "SELECT a.runEventUrl.eventDate, a.runEventUrl.eventLocation, a.runDriverName, min(a.runTime) from Runs a, Runs b where a.runEventUrl = b.runEventUrl AND a.runDriverName in :driverList AND b.runDriverName in :driverList AND a.runDriverName != b.runDriverName AND a.runEventUrl.eventDate > :startDate AND a.runEventUrl.eventDate < :endDate AND a.runOffcourse = 'N' GROUP BY a.runDriverName, a.runEventUrl ORDER BY a.runEventUrl.eventDate ASC"),
    @NamedQuery(name = "Runs.findCommonEventsForDriversPax", query = "SELECT a.runEventUrl.eventDate, a.runEventUrl.eventLocation, a.runDriverName, min(a.runPaxTime) from Runs a, Runs b where a.runEventUrl = b.runEventUrl AND a.runDriverName in :driverList AND b.runDriverName in :driverList AND a.runDriverName != b.runDriverName AND a.runEventUrl.eventDate > :startDate AND a.runEventUrl.eventDate < :endDate AND a.runOffcourse = 'N' GROUP BY a.runDriverName, a.runEventUrl ORDER BY a.runEventUrl.eventDate ASC"),
    @NamedQuery(name = "Runs.findCommonEventsForDriversCones", query = "SELECT a.runEventUrl.eventDate, a.runEventUrl.eventLocation, a.runDriverName, sum(a.runCones) from Runs a, Runs b where a.runEventUrl.eventUrl = b.runEventUrl.eventUrl AND a.runDriverName in :driverList AND b.runDriverName in :driverList AND a.runDriverName != b.runDriverName AND a.runEventUrl.eventDate > :startDate AND a.runEventUrl.eventDate < :endDate AND a.runNumber = b.runNumber GROUP BY a.runDriverName, a.runEventUrl ORDER BY a.runEventUrl.eventDate ASC")})
public class Runs implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "RUN_ID")
    private Integer runId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 45)
    @Column(name = "RUN_DRIVER_NAME")
    private String runDriverName;
    @Basic(optional = true)
    @Size(min = 0, max = 45)
    @Column(name = "RUN_CAR_NAME")
    private String runCarName;
    @Basic(optional = false)
    @NotNull
    @Column(name = "RUN_NUMBER")
    private int runNumber;
    @Basic(optional = false)
    @NotNull
    @Column(name = "RUN_TIME")
    private double runTime;
    @Basic(optional = false)
    @NotNull
    @Column(name = "RUN_PAX_TIME")
    private double runPaxTime;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 10)
    @Column(name = "RUN_OFFCOURSE")
    private String runOffcourse;
    @Basic(optional = false)
    @NotNull
    @Column(name = "RUN_CONES")
    private int runCones;
    @JoinColumn(name = "RUN_EVENT_URL", referencedColumnName = "EVENT_URL")
    @ManyToOne(optional = false)
    private Events runEventUrl;
    @JoinColumn(name = "RUN_CLASS_NAME", referencedColumnName = "CLASS_NAME")
    @ManyToOne(optional = false)
    private Classes runClassName;

    public Runs() {
    }

    public Runs(Integer runId) {
        this.runId = runId;
    }

    public Runs(Integer runId, String runDriverName, String runCarName, int runNumber, double runTime, double runPaxTime, String runOffcourse, int runCones) {
        this.runId = runId;
        this.runDriverName = runDriverName;
        this.runCarName = runCarName;
        this.runNumber = runNumber;
        this.runTime = runTime;
        this.runPaxTime = runPaxTime;
        this.runOffcourse = runOffcourse;
        this.runCones = runCones;
    }

    public Integer getRunId() {
        return runId;
    }

    public void setRunId(Integer runId) {
        this.runId = runId;
    }

    public String getRunDriverName() {
        return runDriverName;
    }

    public void setRunDriverName(String runDriverName) {
        this.runDriverName = runDriverName;
    }

    public String getRunCarName() {
        return runCarName;
    }

    public void setRunCarName(String runCarName) {
        this.runCarName = runCarName;
    }

    public int getRunNumber() {
        return runNumber;
    }

    public void setRunNumber(int runNumber) {
        this.runNumber = runNumber;
    }

    public double getRunTime() {
        return runTime;
    }

    public void setRunTime(double runTime) {
        this.runTime = runTime;
    }

    public double getRunPaxTime() {
        return runPaxTime;
    }

    public void setRunPaxTime(double runPaxTime) {
        this.runPaxTime = runPaxTime;
    }

    public String getRunOffcourse() {
        return runOffcourse;
    }

    public void setRunOffcourse(String runOffcourse) {
        this.runOffcourse = runOffcourse;
    }

    public int getRunCones() {
        return runCones;
    }

    public void setRunCones(int runCones) {
        this.runCones = runCones;
    }

    public Events getRunEventUrl() {
        return runEventUrl;
    }

    public void setRunEventUrl(Events runEventUrl) {
        this.runEventUrl = runEventUrl;
    }

    public Classes getRunClassName() {
        return runClassName;
    }

    public void setRunClassName(Classes runClassName) {
        this.runClassName = runClassName;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (runId != null ? runId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Runs)) {
            return false;
        }
        Runs other = (Runs) object;
        if ((this.runId == null && other.runId != null) || (this.runId != null && !this.runId.equals(other.runId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "autocrossdb.entities.Runs[ runId=" + runId + " ]";
    }
    
}
