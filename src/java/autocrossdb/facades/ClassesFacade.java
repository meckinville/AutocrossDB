/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package autocrossdb.facades;

import autocrossdb.entities.Classes;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author rmcconville
 */
@Stateless
public class ClassesFacade extends AbstractFacade<Classes> {
    @PersistenceContext(unitName = "AutoxDBPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ClassesFacade() {
        super(Classes.class);
    }
    
}
