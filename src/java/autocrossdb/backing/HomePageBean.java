/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package autocrossdb.backing;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;

/**
 *
 * @author rmcconville
 */
@ManagedBean(name="homePage")
public class HomePageBean 
{
    private List<String> images;
    
    @PostConstruct
    public void init()
    {
        images = new ArrayList<String>();
        images.add("1.jpg");
        images.add("2.jpg");
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }
    
    
}
