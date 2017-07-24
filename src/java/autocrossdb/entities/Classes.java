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
@Table(name = "classes")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Classes.findAll", query = "SELECT c FROM Classes c"),
    @NamedQuery(name = "Classes.findByClassName", query = "SELECT c FROM Classes c WHERE c.className = :className"),
    @NamedQuery(name = "Classes.findByClass2016Pax", query = "SELECT c FROM Classes c WHERE c.class2016Pax = :class2016Pax"),
    @NamedQuery(name = "Classes.findByClass2015Pax", query = "SELECT c FROM Classes c WHERE c.class2015Pax = :class2015Pax"),
    @NamedQuery(name = "Classes.findByClass2014Pax", query = "SELECT c FROM Classes c WHERE c.class2014Pax = :class2014Pax"),
    @NamedQuery(name = "Classes.findByClass2013Pax", query = "SELECT c FROM Classes c WHERE c.class2013Pax = :class2013Pax")})
public class Classes implements Serializable, Comparable<Classes> {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 20)
    @Column(name = "CLASS_NAME")
    private String className;
    @Basic(optional = false)
    @NotNull
    @Column(name = "CLASS_2017_PAX")
    private double class2017Pax;
    @Basic(optional = false)
    @NotNull
    @Column(name = "CLASS_2016_PAX")
    private double class2016Pax;
    @Basic(optional = false)
    @NotNull
    @Column(name = "CLASS_2015_PAX")
    private double class2015Pax;
    @Basic(optional = false)
    @NotNull
    @Column(name = "CLASS_2014_PAX")
    private double class2014Pax;
    @Basic(optional = false)
    @NotNull
    @Column(name = "CLASS_2013_PAX")
    private double class2013Pax;
    //@OneToMany(cascade = CascadeType.ALL, mappedBy = "runClassName")
    //private Collection<Runs> runsCollection;

    public Classes() {
    }

    public Classes(String className) {
        this.className = className;
    }

    public Classes(String className, double class2016Pax, double class2015Pax, double class2014Pax, double class2013Pax) {
        this.className = className;
        this.class2017Pax = class2017Pax;
        this.class2016Pax = class2016Pax;
        this.class2015Pax = class2015Pax;
        this.class2014Pax = class2014Pax;
        this.class2013Pax = class2013Pax;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public double getClass2016Pax() {
        return class2016Pax;
    }

    public void setClass2016Pax(double class2016Pax) {
        this.class2016Pax = class2016Pax;
    }

    public double getClass2015Pax() {
        return class2015Pax;
    }

    public void setClass2015Pax(double class2015Pax) {
        this.class2015Pax = class2015Pax;
    }

    public double getClass2014Pax() {
        return class2014Pax;
    }

    public void setClass2014Pax(double class2014Pax) {
        this.class2014Pax = class2014Pax;
    }

    public double getClass2013Pax() {
        return class2013Pax;
    }

    public void setClass2013Pax(double class2013Pax) {
        this.class2013Pax = class2013Pax;
    }
    
    public double getClass2017Pax() {
        return class2017Pax;
    }

    public void setClass2017Pax(double class2017Pax) {
        this.class2017Pax = class2017Pax;
    }
/*
    @XmlTransient
    public Collection<Runs> getRunsCollection() {
        return runsCollection;
    }

    public void setRunsCollection(Collection<Runs> runsCollection) {
        this.runsCollection = runsCollection;
    }
*/
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (className != null ? className.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Classes)) {
            return false;
        }
        Classes other = (Classes) object;
        if ((this.className == null && other.className != null) || (this.className != null && !this.className.equals(other.className))) {
            return false;
        }
        return true;
    }
    
    @Override
    public int compareTo(Classes cls)
    {
        return this.className.compareTo(cls.getClassName());
    }

    @Override
    public String toString() {
        return "autocrossdb.entities.Classes[ className=" + className + " ]";
    }
    
}
