package com.st.model;

import org.joda.time.DateTime;

/**
 * This interface defines the LastModifiedDate model. Applications that use the API must
 * implement the same model.
 */
public interface ILastModifiedDate {
    
    public DateTime getLast_modified();
    
    public void setLast_modified(DateTime lastModified);
}