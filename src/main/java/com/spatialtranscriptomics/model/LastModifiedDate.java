/*
 * Copyright (C) 2012 Spatial Transcriptomics AB
 * Read LICENSE for more information about licensing terms
 * Contact: Jose Fernandez Navarro <jose.fernandez.navarro@scilifelab.se>
 */


package com.spatialtranscriptomics.model;

import org.joda.time.DateTime;

/**
 * Wraps a last modified date into JSON.
 * Enables JSON access to only last modified date as a subset of a bigger JSON entity.
 */
public class LastModifiedDate implements ILastModifiedDate {
    
    DateTime last_modified;
    
    /**
     * Default constructor is needed by Jackson, in
     * case other constructors are added.
     */
    public LastModifiedDate() {}
    
    
    public LastModifiedDate(DateTime date) {
        last_modified =  date;
    }
    
    @Override
    public DateTime getLast_modified() {
        return last_modified;
    }
    
    @Override
    public void setLast_modified(DateTime lastModified) {
        this.last_modified = lastModified;
    }
}
