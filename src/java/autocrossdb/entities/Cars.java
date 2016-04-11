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
import javax.persistence.Id;
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
@Table(name = "cars")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Cars.findAll", query = "SELECT c FROM Cars c"),
    @NamedQuery(name = "Cars.findByCarMake", query = "SELECT c FROM Cars c WHERE c.carMake = :carMake"),
    @NamedQuery(name = "Cars.findByCarCountry", query = "SELECT c FROM Cars c WHERE c.carCountry = :carCountry"),
    @NamedQuery(name = "Cars.findByCarAlternatives", query = "SELECT c FROM Cars c WHERE c.carAlternatives = :carAlternatives")})
public class Cars implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 50)
    @Column(name = "car_make")
    private String carMake;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 45)
    @Column(name = "car_country")
    private String carCountry;
    @Size(max = 200)
    @Column(name = "car_alternatives")
    private String carAlternatives;

    public Cars() {
    }

    public Cars(String carMake) {
        this.carMake = carMake;
    }

    public Cars(String carMake, String carCountry) {
        this.carMake = carMake;
        this.carCountry = carCountry;
    }

    public String getCarMake() {
        return carMake;
    }

    public void setCarMake(String carMake) {
        this.carMake = carMake;
    }

    public String getCarCountry() {
        return carCountry;
    }

    public void setCarCountry(String carCountry) {
        this.carCountry = carCountry;
    }

    public String getCarAlternatives() {
        return carAlternatives;
    }

    public void setCarAlternatives(String carAlternatives) {
        this.carAlternatives = carAlternatives;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (carMake != null ? carMake.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Cars)) {
            return false;
        }
        Cars other = (Cars) object;
        if ((this.carMake == null && other.carMake != null) || (this.carMake != null && !this.carMake.equals(other.carMake))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "autocrossdb.entities.Cars[ carMake=" + carMake + " ]";
    }
    
}
