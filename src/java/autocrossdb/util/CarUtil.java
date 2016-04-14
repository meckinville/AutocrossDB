/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package autocrossdb.util;

import autocrossdb.component.AwardHelper;
import autocrossdb.entities.Cars;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author rmcconville
 */
public class CarUtil 
{
    List<Cars> cars;
    
    public CarUtil(List<Cars> c)
    {
        this.cars = c;
    }
    
    //query format:
    //0=min run time, 1=driver name, 2=car name
    public List<AwardHelper> getClassWins(List<Object[]> query)
    {
        List<AwardHelper> carAwards = new ArrayList();
        carAwards.add(new AwardHelper(0, "UNKNOWN"));
        for(Object[] o : query)
        {
            //if the vehicle name is blank, skip this vehicle
            if(o[2].toString().equals("") || o[2].toString() == null)
            {
                continue;
            }
            boolean added = false;
            for(Cars c : cars)
            {
                if(o[2].toString().contains(c.getCarMake()))
                {
                    Cars currentMake = c;
                    AwardHelper temp = new AwardHelper(1, currentMake.getCarMake());
                    if(carAwards.contains(temp))
                    {
                        AwardHelper oldAward = carAwards.get(carAwards.indexOf(new AwardHelper(0, currentMake.getCarMake())));
                        oldAward.setValue(oldAward.getValue() + 1);
                    }
                    else
                    {
                        carAwards.add(temp);
                    }
                    added = true;
                    break;
                }
                else
                {
                    String alternate = c.getCarAlternatives();
                    if(alternate != null)
                    {
                        String[] alternatives = alternate.split(",");
                        for(int x = 0; x < alternatives.length; x++)
                        {
                            if(o[2].toString().contains(alternatives[x]))
                            {
                                Cars currentMake = c;
                                AwardHelper temp = new AwardHelper(1, currentMake.getCarMake());
                                if(carAwards.contains(temp))
                                {
                                    AwardHelper oldAward = carAwards.get(carAwards.indexOf(new AwardHelper(0, currentMake.getCarMake())));
                                    oldAward.setValue(oldAward.getValue() + 1);
                                }
                                else
                                {
                                    carAwards.add(temp);
                                }
                                added = true;
                                break;
                            }
                        }
                    }
                            
                    
                }
            }
            if(!added)
            {
                AwardHelper oldUnknown = carAwards.get(carAwards.indexOf(new AwardHelper(0, "UNKNOWN")));
                oldUnknown.setValue(oldUnknown.getValue() + 1);
            }
        }
        Collections.sort(carAwards, Collections.reverseOrder());
        return carAwards;
    }
    
    
    //query format:
    //Strings of car names
    public List<AwardHelper> getParticipation(List<String> query)
    {
        
        List<AwardHelper> carAwards = new ArrayList();
        carAwards.add(new AwardHelper(0, "UNKNOWN"));
        for(String o : query)
        {
            //if the vehicle name is blank, skip this vehicle
            if(o.equals("") || o == null)
            {
                continue;
            }
            boolean added = false;
            for(Cars c : cars)
            {
                if(o.contains(c.getCarMake()))
                {
                    AwardHelper temp = new AwardHelper(1, c.getCarMake());
                    if(carAwards.contains(temp))
                    {
                        AwardHelper oldAward = carAwards.get(carAwards.indexOf(new AwardHelper(0, c.getCarMake())));
                        oldAward.setValue(oldAward.getValue() + 1);
                    }
                    else
                    {
                        carAwards.add(temp);
                    }
                    added=true;
                    break;
                }
                else
                {
                    String alternate = c.getCarAlternatives();
                    if(alternate != null)
                    {
                        String[] alternatives = alternate.split(",");
                        for(int x = 0; x < alternatives.length; x++)
                        {
                            if(o.contains(alternatives[x]))
                            {
                                Cars currentMake = c;
                                AwardHelper temp = new AwardHelper(1, currentMake.getCarMake());
                                if(carAwards.contains(temp))
                                {
                                    AwardHelper oldAward = carAwards.get(carAwards.indexOf(new AwardHelper(0, currentMake.getCarMake())));
                                    oldAward.setValue(oldAward.getValue() + 1);
                                }
                                else
                                {
                                    carAwards.add(temp);
                                }
                                added = true;
                                break;
                            }
                        }
                    }
                }
            }
            if(!added)
            {
                AwardHelper oldUnknown = carAwards.get(carAwards.indexOf(new AwardHelper(0, "UNKNOWN")));
                oldUnknown.setValue(oldUnknown.getValue() + 1);
            }
        }
        Collections.sort(carAwards, Collections.reverseOrder());
        return carAwards;
    }
}
