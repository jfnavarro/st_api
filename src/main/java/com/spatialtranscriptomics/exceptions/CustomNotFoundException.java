/*
 * Copyright (C) 2012 Spatial Transcriptomics AB
 * Read LICENSE for more information about licensing terms
 * Contact: Jose Fernandez Navarro <jose.fernandez.navarro@scilifelab.se>
 */

package com.spatialtranscriptomics.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * This class is used to return an Exception with a customized JSON response (see NotFoundResponse)
 * Used in Controllers
 */
@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class CustomNotFoundException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7644203240854556553L;

	public CustomNotFoundException(String message) {
		super(message);
	}
}
