package com.javafiddle.core.ejb;

import com.javafiddle.core.jpa.UserAccountType;
import com.javafiddle.core.jpa.UserProfile;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;

/**
 *
 * @author danon
 */
@Startup @Singleton
public class Application {

    private static Long guestUserId = null;
    
    @EJB
    private UserManagerLocal um;
    
    @PostConstruct
    private void init() {
        // create default "guest" account
        initGuestUser();
        
    }
    
    private void initGuestUser() {
        UserProfile guestUser = um.findByNickname("guest");
        if (guestUser == null) {
            guestUser = createGuestUser();
        }
        guestUserId = guestUser.getId();
        System.out.println("Guest User created. ID = " + guestUserId);
    }
    
    private UserProfile createGuestUser() {
        UserProfile guest = um.createAccount(UserAccountType.NICKNAME, "guest", "guest");
        guest.setNickname("guest");
        um.updateProfile(guest);
        return guest; 
    }
    
    public static Long getGuestUserId() {
        return guestUserId;
    } 
    
}
