package com.javafiddle.web.beans;

import java.util.Date;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.RequestScoped;
import ru.javafiddle.core.ejb.UserManagerLocal;
import ru.javafiddle.core.jpa.User;

/**
 *
 * @author wawilon
 */
@Named(value = "user")
@RequestScoped
public class UserBean {

    @EJB
    private UserManagerLocal um;
    
    private User user;
    
    /**
     * Creates a new instance of UserBean
     */
    public UserBean() {
        
    }
    
    @PostConstruct
    private void init() {
        user = um.createUser(new Date().toString());
    }
    
    public User getUser() {
        return user;
    }
}
