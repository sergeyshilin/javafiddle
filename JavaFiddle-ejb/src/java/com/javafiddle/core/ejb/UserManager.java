package com.javafiddle.core.ejb;

import com.javafiddle.core.jpa.UserAccount;
import com.javafiddle.core.jpa.UserAccountType;
import com.javafiddle.core.jpa.UserProfile;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

@Stateless
public class UserManager implements UserManagerLocal {
    @PersistenceContext
    private EntityManager em;

    @Override
    public UserProfile findById(long profileId) {
        return em.find(UserProfile.class, profileId);
    }

    @Override
    public UserProfile findByNickname(String nickname) {
        try {
            return em.createQuery("select u from UserProfile u where u.nickname = :nickname", UserProfile.class)
                .setParameter("nickname", nickname)
                .getSingleResult();
        } catch (NoResultException ex) {
            System.out.println("UserManager.findByNickname() - no result for nickname=" + nickname);
            return null;
        }
    }

    @Override
    public UserProfile createAccount(UserAccountType accountType, String login, String password) {
        if (accountType == UserAccountType.NICKNAME || accountType == UserAccountType.EMAIL) {
            UserProfile profile = new UserProfile();
            profile.setNickname(login);
            em.persist(profile);
            
            addAccount(profile, accountType, login, password);
            
            return profile;
        } else {
            throw new UnsupportedOperationException("Unsupported account type: " + accountType);
        }
    }

    @Override
    public UserProfile validateAccount(UserAccountType accountType, String login, String password) {
        try {
            UserAccount account = em.createQuery("select u from UserAccount u where u.login = :login and u.password = :password and u.accountType = :accountType", UserAccount.class)
                .setParameter("login", login)
                .setParameter("password", password)
                .setParameter("accountType", accountType)
                .getSingleResult();
            return account.getProfile();
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    @Override
    public UserProfile updateProfile(UserProfile userProfile) {
        if (userProfile == null || userProfile.getId() == null)
            throw new RuntimeException();
        return em.merge(userProfile);
    }

    @Override
    public boolean addAccount(UserProfile profile, UserAccountType accountType, String login, String password) {
        if (checkAccountExistance(accountType, login)) {
            UserProfile user = validateAccount(accountType, login, password);
            if (user != null) {
                return false;
            }
            throw new RuntimeException("Account already exists but incorrect password was provided.");
        }
        if (accountType == UserAccountType.NICKNAME || accountType == UserAccountType.EMAIL) {
            UserAccount account = new UserAccount();
            account.setProfile(profile);
            account.setAccountType(accountType);
            account.setLogin(login);
            account.setPassword(password);
            em.persist(account);
            return true;
        } else {
            throw new UnsupportedOperationException("Unsupported account type: " + accountType);
        }
    }

    @Override
    public boolean checkAccountExistance(UserAccountType accountType, String login) {
        try {
            UserAccount result = em.createQuery("select u from UserAccount u where u.login = :login and u.accountType = :accountType", UserAccount.class)
                .setParameter("login", login)
                .setParameter("accountType", accountType)
                .getSingleResult();
            if (result != null)
                return true;
            return false;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }
    
}
