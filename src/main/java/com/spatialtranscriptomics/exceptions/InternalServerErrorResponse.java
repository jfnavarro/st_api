/*
 * Copyright (C) 2012 Spatial Transcriptomics AB
 * Read LICENSE for more information about licensing terms
 * Contact: Jose Fernandez Navarro <jose.fernandez.navarro@scilifelab.se>
 */

package com.spatialtranscriptomics.exceptions;

/**
 * This class defines the error response object returned as JSON by the CustomInternalServerErrorException
 */

public class InternalServerErrorResponse {

	public String error;
	public String error_description;

	public InternalServerErrorResponse(String msg) {
		this.error = "Internal Server Error";
		this.error_description = msg;
	}
}
