package ru.javafiddle.core.ejb;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import ru.javafiddle.core.jpa.User;

/**
 *
 * @author wawilon
 */
@Stateless
public class UserManager implements UserManagerLocal {
    
    @PersistenceContext
    private EntityManager em;

    @Override
    public User createUser(String name) {
        User u = new User();
        u.setFirstName(name);
        em.persist(u);
        return u;
    }
}
