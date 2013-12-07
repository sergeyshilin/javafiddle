package com.javafiddle.core.ejb;

import com.javafiddle.core.jpa.UserAccountType;
import com.javafiddle.core.jpa.UserProfile;
import javax.ejb.Local;

@Local
public interface UserManagerLocal {
    
    UserProfile findById(long profileId);
    
    UserProfile findByNickname(String nickname);
    
    UserProfile createAccount(UserAccountType accountType, String login, String password);
    
    UserProfile validateAccount(UserAccountType accountType, String login, String password);
    
    UserProfile updateProfile(UserProfile userProfile);
    
    boolean addAccount(UserProfile profile, UserAccountType accountType, String login, String password);
    
    boolean checkAccountExistance(UserAccountType accountType, String login);
    
}
