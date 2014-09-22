/*
 * Copyright (C) 2012 Spatial Transcriptomics AB
 * Read LICENSE for more information about licensing terms
 * Contact: Jose Fernandez Navarro <jose.fernandez.navarro@scilifelab.se>
 */
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
