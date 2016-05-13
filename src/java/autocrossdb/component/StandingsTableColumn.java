/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package autocrossdb.component;

import java.io.Serializable;

/**
 *
 * @author rmcconville
 */
public class StandingsTableColumn implements Serializable
{
    private String header;
    private String value;
    
    public StandingsTableColumn(String h, String v)
    {
        this.header = h;
        this.value = v;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
    
    
}
