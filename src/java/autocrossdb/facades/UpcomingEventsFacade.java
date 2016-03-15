/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package autocrossdb.facades;

import autocrossdb.entities.UpcomingEvents;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author rmcconville
 */
@Stateless
public class UpcomingEventsFacade extends AbstractFacade<UpcomingEvents> {
    @PersistenceContext(unitName = "AutoxDBPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public UpcomingEventsFacade() {
        super(UpcomingEvents.class);
    }
    
}
