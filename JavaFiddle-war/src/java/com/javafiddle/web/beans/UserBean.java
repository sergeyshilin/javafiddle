package com.javafiddle.web.beans;

import com.javafiddle.core.ejb.UserManagerLocal;
import com.javafiddle.core.jpa.UserProfile;
import com.javafiddle.web.utils.JSFHelper;
import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

@Named
@SessionScoped
public class UserBean implements Serializable {
    
    private Long userId;
    private UserProfile profile;
    
    @Inject
    private UserManagerLocal um;
    
    @PostConstruct
    private void init() {
        userId = new JSFHelper().getCurrentUserId();
        if (userId != null)
            profile = um.findById(userId);
    }

    public Long getUserId() {
        return userId;
    }

    public UserProfile getProfile() {
        return profile;
    }
    
}
