/*
 * Copyright (C) 2012 Spatial Transcriptomics AB
 * Read LICENSE for more information about licensing terms
 * Contact: Jose Fernandez Navarro <jose.fernandez.navarro@scilifelab.se>
 */

package com.spatialtranscriptomics.model;

import org.joda.time.DateTime;

/**
 * This interface defines the LastModifiedDate model. Applications that use the API must
 * implement the same model.
 */

public interface ILastModifiedDate {
    
    public DateTime getLast_modified();
    
    public void setLast_modified(DateTime lastModified);
}