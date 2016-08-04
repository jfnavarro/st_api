package com.st.model;

/**
 * This interface defines the Account id model. Applications that use the API must
 * implement the same model.
 */
public interface IAccountId {

    public String getId();

    public void setId(String id);

    public String getUsername();

    public void setUsername(String username);
}
