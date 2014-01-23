/*
 * Copyright (C) 2012 Spatial Transcriptomics AB
 * Read LICENSE for more information about licensing terms
 * Contact: Jose Fernandez Navarro <jose.fernandez.navarro@scilifelab.se>
 */

package com.spatialtranscriptomics.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * This class is used to return an Exception with a customized JSON response (see BadRequestResponse)
 * Used in Controllers
 */
@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class CustomBadRequestException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3398438496792631729L;

	public CustomBadRequestException(String message) {
		super(message);
	}
}
