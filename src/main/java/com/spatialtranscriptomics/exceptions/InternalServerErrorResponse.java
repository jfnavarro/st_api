/*
 * Copyright (C) 2014 Spatial Transcriptomics AB
 * Read LICENSE for more information about licensing terms
 * Contact: Jose Fernandez Navarro <jose.fernandez.navarro@scilifelab.se>
 */

package com.spatialtranscriptomics.exceptions;

/**
 * This class defines the JSON error response body for the CustomInternalServerErrorException
 */

public class InternalServerErrorResponse {

    public String error;
    public String error_description;

    /**
     * Constructor.
     * @param msg message.
     */
    public InternalServerErrorResponse(String msg) {
            this.error = "Internal Server Error";
            this.error_description = msg;
    }
}
