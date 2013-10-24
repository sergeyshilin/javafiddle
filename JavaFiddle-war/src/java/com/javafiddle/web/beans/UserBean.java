package com.javafiddle.web.beans;

import javax.annotation.PostConstruct;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import ru.javafiddle.core.ejb.UserManagerLocal;

/**
 * A session-scoped bean for accessing current user status.
 * @author wawilon
 */
@Named(value = "user")
@SessionScoped
public class UserBean extends CommonBean {
    
    @Inject
    private UserManagerLocal um;
    
    /**
     * Creates a new instance of UserBean
     */
    public UserBean() {
        
    }
    
    @PostConstruct
    private void init() {
        // some initializations after the bean is created and all fields are injected
        // load user info by id from database
    }
    
    public boolean isLoggedIn() {
        return getCurrentUserId() != null;
    }
    
    public Long getId() {
        return getCurrentUserId();
    }
    
}
