package com.spatialtranscriptomics.model;

/**
 * This interface defines the IMongoUserDetails model. This is used internally,
 * and only dictates convenience methods.
 */
public interface IMongoUserDetails {

    public boolean isContentManager();

    public boolean isAdmin();

    public boolean isUser();

}
