/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package autocrossdb.entities;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author Ryan
 */
@Embeddable
public class DriverStatsPK implements Serializable {

    @Basic(optional = false)
    @NotNull
    @Column(name = "DS_YEAR")
    private int dsYear;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 50)
    @Column(name = "DS_NAME")
    private String dsName;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 5)
    @Column(name = "DS_CLASS")
    private String dsClass;

    public DriverStatsPK() {
    }

    public DriverStatsPK(int dsYear, String dsName, String dsClass) {
        this.dsYear = dsYear;
        this.dsName = dsName;
        this.dsClass = dsClass;
    }

    public int getDsYear() {
        return dsYear;
    }

    public void setDsYear(int dsYear) {
        this.dsYear = dsYear;
    }

    public String getDsName() {
        return dsName;
    }

    public void setDsName(String dsName) {
        this.dsName = dsName;
    }

    public String getDsClass() {
        return dsClass;
    }

    public void setDsClass(String dsClass) {
        this.dsClass = dsClass;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (int) dsYear;
        hash += (dsName != null ? dsName.hashCode() : 0);
        hash += (dsClass != null ? dsClass.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof DriverStatsPK)) {
            return false;
        }
        DriverStatsPK other = (DriverStatsPK) object;
        if (this.dsYear != other.dsYear) {
            return false;
        }
        if ((this.dsName == null && other.dsName != null) || (this.dsName != null && !this.dsName.equals(other.dsName))) {
            return false;
        }
        if ((this.dsClass == null && other.dsClass != null) || (this.dsClass != null && !this.dsClass.equals(other.dsClass))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "autocrossdb.entities.DriverStatsPK[ dsYear=" + dsYear + ", dsName=" + dsName + ", dsClass=" + dsClass + " ]";
    }
    
}
