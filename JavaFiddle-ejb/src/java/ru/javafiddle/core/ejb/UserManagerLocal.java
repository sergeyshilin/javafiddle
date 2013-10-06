package ru.javafiddle.core.ejb;

import javax.ejb.Local;
import ru.javafiddle.core.jpa.User;

/**
 *
 * @author wawilon
 */
@Local
public interface UserManagerLocal {
    
    User createUser(String name);
    
}
