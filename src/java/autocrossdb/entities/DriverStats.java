/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package autocrossdb.entities;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Ryan
 */
@Entity
@Table(name = "driver_stats")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "DriverStats.findAll", query = "SELECT d FROM DriverStats d")
    , @NamedQuery(name = "DriverStats.findByDsYear", query = "SELECT d FROM DriverStats d WHERE d.driverStatsPK.dsYear = :dsYear")
    , @NamedQuery(name = "DriverStats.findByDsName", query = "SELECT d FROM DriverStats d WHERE d.driverStatsPK.dsName = :dsName")
    , @NamedQuery(name = "DriverStats.findByDsClass", query = "SELECT d FROM DriverStats d WHERE d.driverStatsPK.dsClass = :dsClass")
    , @NamedQuery(name = "DriverStats.findByDsEvents", query = "SELECT d FROM DriverStats d WHERE d.dsEvents = :dsEvents")
    , @NamedQuery(name = "DriverStats.findByDsRawWins", query = "SELECT d FROM DriverStats d WHERE d.dsRawWins = :dsRawWins")
    , @NamedQuery(name = "DriverStats.findByDsPaxWins", query = "SELECT d FROM DriverStats d WHERE d.dsPaxWins = :dsPaxWins")
    , @NamedQuery(name = "DriverStats.findByDsCones", query = "SELECT d FROM DriverStats d WHERE d.dsCones = :dsCones")
    , @NamedQuery(name = "DriverStats.findByDsOffcourses", query = "SELECT d FROM DriverStats d WHERE d.dsOffcourses = :dsOffcourses")
    , @NamedQuery(name = "DriverStats.findByDsRuns", query = "SELECT d FROM DriverStats d WHERE d.dsRuns = :dsRuns")})
public class DriverStats implements Serializable {

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected DriverStatsPK driverStatsPK;
    @Basic(optional = false)
    @NotNull
    @Column(name = "DS_EVENTS")
    private int dsEvents;
    @Basic(optional = false)
    @NotNull
    @Column(name = "DS_RAW_WINS")
    private int dsRawWins;
    @Basic(optional = false)
    @NotNull
    @Column(name = "DS_PAX_WINS")
    private int dsPaxWins;
    @Basic(optional = false)
    @NotNull
    @Column(name = "DS_CONES")
    private int dsCones;
    @Basic(optional = false)
    @NotNull
    @Column(name = "DS_OFFCOURSES")
    private int dsOffcourses;
    @Basic(optional = false)
    @NotNull
    @Column(name = "DS_RUNS")
    private int dsRuns;

    public DriverStats() {
    }

    public DriverStats(DriverStatsPK driverStatsPK) {
        this.driverStatsPK = driverStatsPK;
    }

    public DriverStats(DriverStatsPK driverStatsPK, int dsEvents, int dsRawWins, int dsPaxWins, int dsCones, int dsOffcourses, int dsRuns) {
        this.driverStatsPK = driverStatsPK;
        this.dsEvents = dsEvents;
        this.dsRawWins = dsRawWins;
        this.dsPaxWins = dsPaxWins;
        this.dsCones = dsCones;
        this.dsOffcourses = dsOffcourses;
        this.dsRuns = dsRuns;
    }

    public DriverStats(int dsYear, String dsName, String dsClass) {
        this.driverStatsPK = new DriverStatsPK(dsYear, dsName, dsClass);
    }

    public DriverStatsPK getDriverStatsPK() {
        return driverStatsPK;
    }

    public void setDriverStatsPK(DriverStatsPK driverStatsPK) {
        this.driverStatsPK = driverStatsPK;
    }

    public int getDsEvents() {
        return dsEvents;
    }

    public void setDsEvents(int dsEvents) {
        this.dsEvents = dsEvents;
    }

    public int getDsRawWins() {
        return dsRawWins;
    }

    public void setDsRawWins(int dsRawWins) {
        this.dsRawWins = dsRawWins;
    }

    public int getDsPaxWins() {
        return dsPaxWins;
    }

    public void setDsPaxWins(int dsPaxWins) {
        this.dsPaxWins = dsPaxWins;
    }

    public int getDsCones() {
        return dsCones;
    }

    public void setDsCones(int dsCones) {
        this.dsCones = dsCones;
    }

    public int getDsOffcourses() {
        return dsOffcourses;
    }

    public void setDsOffcourses(int dsOffcourses) {
        this.dsOffcourses = dsOffcourses;
    }

    public int getDsRuns() {
        return dsRuns;
    }

    public void setDsRuns(int dsRuns) {
        this.dsRuns = dsRuns;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (driverStatsPK != null ? driverStatsPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof DriverStats)) {
            return false;
        }
        DriverStats other = (DriverStats) object;
        if ((this.driverStatsPK == null && other.driverStatsPK != null) || (this.driverStatsPK != null && !this.driverStatsPK.equals(other.driverStatsPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "autocrossdb.entities.DriverStats[ driverStatsPK=" + driverStatsPK + " ]";
    }
    
}
