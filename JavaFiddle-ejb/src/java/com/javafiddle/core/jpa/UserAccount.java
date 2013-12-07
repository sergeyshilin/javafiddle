package com.javafiddle.core.jpa;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * Entity class that represents user login account info.
 * Each user can have several login accounts.
 * @author danon
 */
@Entity @Table(name="user_accounts")
public class UserAccount implements Serializable {
    
    @Id @Column
    @GeneratedValue(strategy = GenerationType.TABLE)
    private Long id;
    
    @JoinColumn(name = "profile_id", referencedColumnName = "id", nullable = false, updatable = false)
    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private UserProfile profile;
    
    @Column(name = "account_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private UserAccountType accountType;
    
    @Column(nullable = false, length = 128)
    private String login;
    
    @Column
    private String password;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UserProfile getProfile() {
        return profile;
    }

    public void setProfile(UserProfile userProfile) {
        this.profile = userProfile;
    }

    public UserAccountType getAccountType() {
        return accountType;
    }

    public void setAccountType(UserAccountType accountType) {
        this.accountType = accountType;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + Objects.hashCode(this.id);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final UserAccount other = (UserAccount) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        return true;
    }
    
    
}
