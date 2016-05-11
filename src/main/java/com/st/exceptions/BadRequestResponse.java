package com.st.exceptions;

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
