/*
 * Copyright (C) 2014 Spatial Transcriptomics AB
 * Read LICENSE for more information about licensing terms
 * Contact: Jose Fernandez Navarro <jose.fernandez.navarro@scilifelab.se>
 */
package com.st.exceptions;

/**
 * This class defines the JSON error response body for the CustomNotModifiedException.
 * NOTE: This response is likely to be truncated and not the response body be empty!!!!
 */
public class NotModifiedResponse {
    
    public String error;
    public String error_description;

    /**
     * Constructor.
     * @param msg 
     */
    public NotModifiedResponse(String msg) {
        this.error = "Not modified";
        this.error_description = msg;
    }
}
