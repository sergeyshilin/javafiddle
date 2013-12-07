package com.javafiddle.core.ejb.util;

import com.javafiddle.core.jpa.LocalIdGenerator;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * EJB class for id generation.
 * @author danon
 */
@Stateless
public class IdGenerator implements IdGeneratorLocal {
    
    @PersistenceContext
    private EntityManager em;
    
    @Override
    public Long getNextId() {
        LocalIdGenerator idGenerator = new LocalIdGenerator();
        em.persist(idGenerator);
        //Long id = (Long) em.getEntityManagerFactory().getPersistenceUnitUtil().getIdentifier(idGenerator);
        Long id = idGenerator.getId();
        em.remove(idGenerator);
        return id;
    }
    
}
