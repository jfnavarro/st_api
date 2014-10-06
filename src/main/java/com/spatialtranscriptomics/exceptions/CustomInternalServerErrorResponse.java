/*
 * Copyright (C) 2014 Spatial Transcriptomics AB
 * Read LICENSE for more information about licensing terms
 * Contact: Jose Fernandez Navarro <jose.fernandez.navarro@scilifelab.se>
 */
package com.spatialtranscriptomics.exceptions;

/**
 * This class defines the error response object returned as JSON
 * by the CustomInternalServerErrorException
 * NOTE: This response is likely to be truncated and not the response body be empty!!!!
 */
public class CustomInternalServerErrorResponse {
    
    public String error;
    public String error_description;

    public CustomInternalServerErrorResponse(String msg) {
            this.error = "Internal server error";
            this.error_description = msg;
    }
}
