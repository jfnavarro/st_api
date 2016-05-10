/*
 * Copyright (C) 2014 Spatial Transcriptomics AB
 * Read LICENSE for more information about licensing terms
 * Contact: Jose Fernandez Navarro <jose.fernandez.navarro@scilifelab.se>
 */

package com.st.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * This exception class is used to return a HTTP response "500 Internal Server
 * Error" with a customized
 * JSON response (see InternalServerErrorResponse).
 * NOTE: It is possible that the body will be truncated, and only the
 * header message reach the client.
 * Used in Controllers.
 */
@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
public class CustomInternalServerErrorException extends RuntimeException {

	/**
	 * Auto-gen ID.
	 */
	private static final long serialVersionUID = -2878259537987353629L;

        /**
         * Constructor.
         * @param message message. 
         */
	public CustomInternalServerErrorException(String message) {
		super(message);
	}
}
