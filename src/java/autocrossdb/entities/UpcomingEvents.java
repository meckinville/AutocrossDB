/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package autocrossdb.entities;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author rmcconville
 */
@Entity
@Table(name = "upcoming_events")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "UpcomingEvents.findAll", query = "SELECT u FROM UpcomingEvents u"),
    @NamedQuery(name = "UpcomingEvents.findByUpcomingLocation", query = "SELECT u FROM UpcomingEvents u WHERE u.upcomingLocation = :upcomingLocation"),
    @NamedQuery(name = "UpcomingEvents.findByUpcomingClub", query = "SELECT u FROM UpcomingEvents u WHERE u.upcomingClub = :upcomingClub"),
    @NamedQuery(name = "UpcomingEvents.findByUpcomingDate", query = "SELECT u FROM UpcomingEvents u WHERE u.upcomingDate = :upcomingDate"),
    @NamedQuery(name = "UpcomingEvents.findByUpcomingType", query = "SELECT u FROM UpcomingEvents u WHERE u.upcomingType = :upcomingType"),
    @NamedQuery(name = "UpcomingEvents.findByUpcomingRegistration", query = "SELECT u FROM UpcomingEvents u WHERE u.upcomingRegistration = :upcomingRegistration"),
    @NamedQuery(name = "UpcomingEvents.findByUpcomingId", query = "SELECT u FROM UpcomingEvents u WHERE u.upcomingId = :upcomingId")})
public class UpcomingEvents implements Serializable {
    private static final long serialVersionUID = 1L;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 45)
    @Column(name = "UPCOMING_LOCATION")
    private String upcomingLocation;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 45)
    @Column(name = "UPCOMING_CLUB")
    private String upcomingClub;
    @Basic(optional = false)
    @NotNull
    @Column(name = "UPCOMING_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date upcomingDate;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 45)
    @Column(name = "UPCOMING_TYPE")
    private String upcomingType;
    @Size(max = 120)
    @Column(name = "UPCOMING_REGISTRATION")
    private String upcomingRegistration;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "UPCOMING_ID")
    private Integer upcomingId;

    public UpcomingEvents() {
    }

    public UpcomingEvents(Integer upcomingId) {
        this.upcomingId = upcomingId;
    }

    public UpcomingEvents(Integer upcomingId, String upcomingLocation, String upcomingClub, Date upcomingDate, String upcomingType) {
        this.upcomingId = upcomingId;
        this.upcomingLocation = upcomingLocation;
        this.upcomingClub = upcomingClub;
        this.upcomingDate = upcomingDate;
        this.upcomingType = upcomingType;
    }

    public String getUpcomingLocation() {
        return upcomingLocation;
    }

    public void setUpcomingLocation(String upcomingLocation) {
        this.upcomingLocation = upcomingLocation;
    }

    public String getUpcomingClub() {
        return upcomingClub;
    }

    public void setUpcomingClub(String upcomingClub) {
        this.upcomingClub = upcomingClub;
    }

    public Date getUpcomingDate() {
        return upcomingDate;
    }

    public void setUpcomingDate(Date upcomingDate) {
        this.upcomingDate = upcomingDate;
    }

    public String getUpcomingType() {
        return upcomingType;
    }

    public void setUpcomingType(String upcomingType) {
        this.upcomingType = upcomingType;
    }

    public String getUpcomingRegistration() {
        return upcomingRegistration;
    }

    public void setUpcomingRegistration(String upcomingRegistration) {
        this.upcomingRegistration = upcomingRegistration;
    }

    public Integer getUpcomingId() {
        return upcomingId;
    }

    public void setUpcomingId(Integer upcomingId) {
        this.upcomingId = upcomingId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (upcomingId != null ? upcomingId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof UpcomingEvents)) {
            return false;
        }
        UpcomingEvents other = (UpcomingEvents) object;
        if ((this.upcomingId == null && other.upcomingId != null) || (this.upcomingId != null && !this.upcomingId.equals(other.upcomingId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "autocrossdb.entities.UpcomingEvents[ upcomingId=" + upcomingId + " ]";
    }
    
}
