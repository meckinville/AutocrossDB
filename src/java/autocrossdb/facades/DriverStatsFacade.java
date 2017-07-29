/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package autocrossdb.facades;

import autocrossdb.entities.DriverStats;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author Ryan
 */
@Stateless
public class DriverStatsFacade extends AbstractFacade<DriverStats> {

    @PersistenceContext(unitName = "AutoxDBPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public DriverStatsFacade() {
        super(DriverStats.class);
    }
    
}
