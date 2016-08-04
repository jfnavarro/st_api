package com.st.model;

import org.hibernate.validator.constraints.NotBlank;

/** 
 * Just a simple wrapper around an Account's id and username. 
 * Useful when a we want to assign users to datasets or viceversa
 */
public class AccountId implements IAccountId {

    @NotBlank
    public String id;

    @NotBlank
    public String username;
    
    /**
     * Default constructor is needed by Jackson, in
     * case other constructors are added.
     */
    public AccountId() {}
    
    // id is set automatically by MongoDB
    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public void setUsername(String username) {
        this.username = username;
    }

}
