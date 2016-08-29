/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package autocrossdb.component;

import autocrossdb.entities.UpcomingEvents;
import java.util.ArrayList;
import java.util.List;
import org.primefaces.model.map.DefaultMapModel;
import org.primefaces.model.map.LatLng;
import org.primefaces.model.map.MapModel;
import org.primefaces.model.map.Polygon;

/**
 *
 * @author rmcconville
 */
public class UpcomingEventMap 
{
    private double center1;
    private double center2;
    private int zoom;
    private MapModel map;
    private List<LatLng> coordinates;
    private String address;
    
    public UpcomingEventMap()
    {
        
    }
    
    public UpcomingEventMap(UpcomingEvents event)
    {
        address = "";
        map = new DefaultMapModel();
        coordinates = new ArrayList();
        Polygon poly = new Polygon();
        if(event.getUpcomingLocation().equals("OCCC"))
        {
            this.center1 = 28.430318;
            this.center2 = -81.458660;
            this.zoom = 18;
            this.address = "9400 Universal Blvd Orlando, Florida 32819";
            
            
            coordinates.add(new LatLng(28.430768, -81.459362));
            coordinates.add(new LatLng(28.430768, -81.458034));
            coordinates.add(new LatLng(28.429372, -81.458034));
        }
        else if(event.getUpcomingLocation().equals("LAKE TECH"))
        {
            this.center1 = 28.752679;
            this.center2 = -81.742034;
            this.zoom = 18;
            this.address = "13000 Frankies Road Tavares, FL 32778";
            
            coordinates.add(new LatLng(28.753398,-81.742313));
            coordinates.add(new LatLng(28.753398,-81.741696));
            coordinates.add(new LatLng(28.751988, -81.741696));
            coordinates.add(new LatLng(28.751988,-81.742313));
        }
        else if(event.getUpcomingLocation().equals("BROOKSVILLE"))
        {
            this.center1 = 28.469619;
            this.center2 = -82.466974;
            this.zoom = 17;
            this.address = "Runway Dr, Brooksville, FL 34604";
            
            coordinates.add(new LatLng(28.468430,-82.465738));
            coordinates.add(new LatLng(28.468326, -82.466028));
            coordinates.add(new LatLng(28.471382, -82.468711));
            coordinates.add(new LatLng(28.471505, -82.468420));
        }
        else if (event.getUpcomingLocation().equals("DAYTONA (KART TRACK)"))
        {
            this.center1 = 29.189853;
            this.center2 = -81.064726;
            this.zoom = 17;
            this.address = "1801 W International Speedway Blvd, Daytona Beach, FL 32114";
            
            coordinates.add(new LatLng(29.189055, -81.063618));
            coordinates.add(new LatLng(29.188857, -81.065548));
            coordinates.add(new LatLng(29.190752, -81.065925));
            coordinates.add(new LatLng(29.190838, -81.064143));
        }
        else if (event.getUpcomingLocation().equals("SEBRING"))
        {
            this.center1 = 27.449313;
            this.center2 = -81.352930;
            this.zoom = 17;
            this.address = "113 Midway Drive Sebring, Florida 33870";
            
            coordinates.add(new LatLng(27.448597, -81.356136));
            coordinates.add(new LatLng(27.448964, -81.356186));
            coordinates.add(new LatLng(27.448972, -81.349084));
            coordinates.add(new LatLng(27.448562, -81.349094));
        }
        else if (event.getUpcomingLocation().equals("BUCKINGHAM AIRFIELD"))
        {
            this.center1 = 26.637576;
            this.center2 = -81.710500;
            this.zoom = 16;
            this.address = "3987 Sunset Road, Lehigh Acres, FL 33971";
            
            coordinates.add(new LatLng(26.635909, -81.712441));
            coordinates.add(new LatLng(26.636756, -81.712941));
            coordinates.add(new LatLng(26.639554, -81.707520));
            coordinates.add(new LatLng(26.638897, -81.707194));
        }
        else if (event.getUpcomingLocation().equals("FIRM"))
        {
            this.center1 = 29.846382;
            this.center2 = -82.058166;
            this.zoom = 16;
            this.address = "7266 Airport Rd, Starke, FL 32091";
            
            coordinates.add(new LatLng(29.844020, -82.057669));
            coordinates.add(new LatLng(29.843711, -82.059235));
            coordinates.add(new LatLng(29.848615, -82.061249));
            coordinates.add(new LatLng(29.848712, -82.058815));
        }
        else if (event.getUpcomingLocation().equals("DELAND"))
        {
            this.center1 = 29.074177;
            this.center2 = -81.286874;
            this.zoom = 16;
            this.address = "1500 Matt Fair Blvd. DeLand, FL";
            
            coordinates.add(new LatLng(29.075142, -81.287118));
            coordinates.add(new LatLng(29.075129, -81.286716));
            coordinates.add(new LatLng(29.071626, -81.286747));
            coordinates.add(new LatLng(29.071581, -81.287175));
            
        }
        
        for(LatLng l : coordinates)
        {
            poly.getPaths().add(l);
        }

        poly.setStrokeColor("#FF9900");
        poly.setFillColor("#FF9900");
        poly.setStrokeOpacity(0.7);
        poly.setFillOpacity(0.7);

        map.addOverlay(poly);
    }
    
    public UpcomingEventMap(double c1, double c2, int zoom)
    {
        this.center1 = c1;
        this.center2 = c2;
        this.zoom = zoom;
    }

    public double getCenter1() {
        return center1;
    }

    public void setCenter1(double center1) {
        this.center1 = center1;
    }

    public double getCenter2() {
        return center2;
    }

    public void setCenter2(double center2) {
        this.center2 = center2;
    }

    public int getZoom() {
        return zoom;
    }

    public void setZoom(int zoom) {
        this.zoom = zoom;
    }

    public MapModel getMap() {
        return map;
    }

    public void setMap(MapModel map) {
        this.map = map;
    }

    public List<LatLng> getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(List<LatLng> coordinates) {
        this.coordinates = coordinates;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
    
    
}
