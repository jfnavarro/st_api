package com.st.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * This exception class returns a header with HTTP response type
 * "400 Bad Request", with a customized JSON response (see BadRequestResponse)
 * Used in Controllers.
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
