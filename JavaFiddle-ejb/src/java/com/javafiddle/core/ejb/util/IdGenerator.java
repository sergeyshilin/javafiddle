package com.javafiddle.core.ejb.util;

import com.javafiddle.core.jpa.LocalIdGenerator;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class IdGenerator implements IdGeneratorLocal {
    
    @PersistenceContext
    private EntityManager em;
    
    @Override
    public long getNextId() {
        LocalIdGenerator idGenerator = new LocalIdGenerator();
        em.persist(idGenerator);
        //Long id = (Long) em.getEntityManagerFactory().getPersistenceUnitUtil().getIdentifier(idGenerator);
        long id = idGenerator.getId();
        em.remove(idGenerator);
        return id;
    }
    
}
