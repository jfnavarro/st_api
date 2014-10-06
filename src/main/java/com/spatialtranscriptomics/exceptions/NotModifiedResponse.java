/*
 * Copyright (C) 2014 Spatial Transcriptomics AB
 * Read LICENSE for more information about licensing terms
 * Contact: Jose Fernandez Navarro <jose.fernandez.navarro@scilifelab.se>
 */
package com.spatialtranscriptomics.exceptions;

/**
 * This class defines the error response object returned as JSON by the CustomNotModifiedException.
 * NOTE: This response is likely to be truncated and not the response body be empty!!!!
 */
public class NotModifiedResponse {
    
    public String error;
    public String error_description;

    public NotModifiedResponse(String msg) {
        this.error = "Not modified";
        this.error_description = msg;
    }
}
