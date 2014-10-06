/*
 * Copyright (C) 2014 Spatial Transcriptomics AB
 * Read LICENSE for more information about licensing terms
 * Contact: Jose Fernandez Navarro <jose.fernandez.navarro@scilifelab.se>
 */

package com.spatialtranscriptomics.exceptions;

/**
 * This class defines the error response object returned as JSON by the CustomBadRequestException
 */
public class BadRequestResponse {

	public String error;
	public String error_description;

	public BadRequestResponse(String msg) {
		this.error = "Bad request";
		this.error_description = msg;
	}
}
