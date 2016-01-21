/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package autocrossdb.entities;

import java.io.Serializable;
import java.util.Date;
import java.util.Collection;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author rmcconville
 */
@Entity
@Table(name = "events")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Events.findAll", query = "SELECT e FROM Events e"),
    @NamedQuery(name = "Events.findByEventUrl", query = "SELECT e FROM Events e WHERE e.eventUrl = :eventUrl"),
    @NamedQuery(name = "Events.findByEventClubName", query = "SELECT e FROM Events e WHERE e.eventClubName = :eventClubName"),
    @NamedQuery(name = "Events.findByEventLocation", query = "SELECT e FROM Events e WHERE e.eventLocation = :eventLocation"),
    @NamedQuery(name = "Events.findByEventDate", query = "SELECT e FROM Events e WHERE e.eventDate = :eventDate"),
    @NamedQuery(name = "Events.findByEventType", query = "SELECT e FROM Events e WHERE e.eventType = :eventType"),
    @NamedQuery(name = "Events.findEventsInDateRange", query = "SELECT e FROM Events e where e.eventDate > :startDate AND e.eventDate < :endDate ORDER BY e.eventDate asc")})
public class Events implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 70)
    @Column(name = "EVENT_URL")
    private String eventUrl;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 20)
    @Column(name = "EVENT_CLUB_NAME")
    private String eventClubName;
    @Size(min = 1, max = 25)
    @Column(name = "EVENT_LOCATION")
    private String eventLocation;
    @Basic(optional = false)
    @NotNull
    @Column(name = "EVENT_DATE")
    private Date eventDate;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 10)
    @Column(name = "EVENT_TYPE")
    private String eventType;
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, mappedBy = "runEventUrl")
    private Collection<Runs> runsCollection;
    @Size(min = 1, max = 45)
    @Column(name = "EVENT_PAX_WINNER")
    private String eventPaxWinner;
    @Size(min = 1, max = 45)
    @Column(name = "EVENT_RAW_WINNER")
    private String eventRawWinner;

    public Events() {
    }

    public Events(String eventUrl) {
        this.eventUrl = eventUrl;
    }

    public Events(String eventUrl, String eventClubName, String eventLocation, Date eventDate, String eventType) {
        this.eventUrl = eventUrl;
        this.eventClubName = eventClubName;
        this.eventLocation = eventLocation;
        this.eventDate = eventDate;
        this.eventType = eventType;
    }
    
    public Events(String eventUrl, String eventClubName, String eventLocation, Date eventDate, String eventType, String eventPaxWinner, String eventRawWinner) {
        this.eventUrl = eventUrl;
        this.eventClubName = eventClubName;
        this.eventLocation = eventLocation;
        this.eventDate = eventDate;
        this.eventType = eventType;
        this.eventPaxWinner = eventPaxWinner;
        this.eventRawWinner = eventRawWinner;
    }

    public String getEventUrl() {
        return eventUrl;
    }

    public void setEventUrl(String eventUrl) {
        this.eventUrl = eventUrl;
    }

    public String getEventClubName() {
        return eventClubName;
    }

    public void setEventClubName(String eventClubName) {
        this.eventClubName = eventClubName;
    }

    public String getEventLocation() {
        return eventLocation;
    }

    public String getPaxWinner() {
        return eventPaxWinner;
    }

    public void setPaxWinner(String eventPaxWinner) {
        this.eventPaxWinner = eventPaxWinner;
    }

    public String getRawWinner() {
        return eventRawWinner;
    }

    public void setRawWinner(String eventRawWinner) {
        this.eventRawWinner = eventRawWinner;
    }

    
    public void setEventLocation(String eventLocation) {
        this.eventLocation = eventLocation;
    }

    public Date getEventDate() {
        return eventDate;
    }

    public void setEventDate(Date eventDate) {
        this.eventDate = eventDate;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    @XmlTransient
    public Collection<Runs> getRunsCollection() {
        return runsCollection;
    }

    public void setRunsCollection(Collection<Runs> runsCollection) {
        this.runsCollection = runsCollection;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (eventUrl != null ? eventUrl.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Events)) {
            return false;
        }
        Events other = (Events) object;
        if ((this.eventUrl == null && other.eventUrl != null) || (this.eventUrl != null && !this.eventUrl.equals(other.eventUrl))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "autocrossdb.entities.Events[ eventUrl=" + eventUrl + " ]";
    }
    
}
