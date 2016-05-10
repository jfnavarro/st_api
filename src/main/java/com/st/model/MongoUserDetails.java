package com.st.model;

import java.util.ArrayList;
import java.util.Collection;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * This class maps the Spring Security interface UserDetails.class to a MongoDB
 * document (by extending the Account model). This is used internally by the
 * API, and contains information required for authentication and authorization
 * by Spring Security.
 *
 * See also class MongoUserDetailsServiceImpl.
 */
@Document(collection = "account")
public class MongoUserDetails extends Account implements UserDetails, IMongoUserDetails {

    /**
     * Default constructor is needed by Jackson, in
     * case other constructors are added.
     */
    public MongoUserDetails() {}
    
    /**
     * Auto-generated ID for inheritance.
     */
    private static final long serialVersionUID = 6756025876358143006L;

    /*
     * Method for the Interface
     * org.springframework.security.core.userdetails.UserDetails. This is not
     * persisted to the DB.
     */
    @Transient
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> grantedAuthorities = new ArrayList<GrantedAuthority>();
        grantedAuthorities.add(new SimpleGrantedAuthority(getRole()));
        return grantedAuthorities;
    }

    /*
     * Method for the Interface
     * org.springframework.security.core.userdetails.UserDetails. This is not
     * persisted to the DB
     */
    @Override
    @Transient
    public boolean isAccountNonExpired() {
        return true;
    }

    /*
     * Method for the Interface
     * org.springframework.security.core.userdetails.UserDetails. This is not
     * persisted to the DB
     */
    @Override
    @Transient
    public boolean isAccountNonLocked() {
        return true;
    }

    /*
     * Method for the Interface
     * org.springframework.security.core.userdetails.UserDetails. This is not
     * persisted to the DB
     */
    @Override
    @Transient
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /*
     * Convenience method for use in ServiceImpl classes. Not persisted to DB
     */
    @Override
    @Transient
    public boolean isContentManager() {
        return role.equals("ROLE_CM");
    }

    /*
     * Convenience method for use in ServiceImpl classes. Not persisted to DB
     */
    @Override
    @Transient
    public boolean isAdmin() {
        return role.equals("ROLE_ADMIN");
    }

    /*
     * Convenience method for use in ServiceImpl classes. Not persisted to DB
     */
    @Override
    @Transient
    public boolean isUser() {
        return role.equals("ROLE_USER");
    }

}
