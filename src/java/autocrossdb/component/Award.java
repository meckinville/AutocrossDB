/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package autocrossdb.component;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author rmcconville
 */
public class Award 
{
    List<String> awardStrings;
    String awardTitle;
    String awardInfo;
    
    public Award(String title, String info)
    {
        this.awardStrings = new ArrayList();
        this.awardTitle = title;
        this.awardInfo = info;
    }
    
    public void add(String s)
    {
        awardStrings.add(s);
    }
    
    public int size()
    {
        return awardStrings.size();
    }

    public List<String> getAwardStrings() {
        return awardStrings;
    }

    public void setAwardStrings(List<String> awardStrings) {
        this.awardStrings = awardStrings;
    }

    public String getAwardTitle() {
        return awardTitle;
    }

    public void setAwardTitle(String awardTitle) {
        this.awardTitle = awardTitle;
    }

    public String getAwardInfo() {
        return awardInfo;
    }

    public void setAwardInfo(String awardInfo) {
        this.awardInfo = awardInfo;
    }
    
    
}
