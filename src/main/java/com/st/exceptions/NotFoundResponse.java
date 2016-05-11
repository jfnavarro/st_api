package com.st.exceptions;

/**
 * This class defines the JSON error response body for the CustomNotFoundException
 */
public class NotFoundResponse {

	public String error;
	public String error_description;

        /**
         * Constructor.
         * @param msg message.
         */
	public NotFoundResponse(String msg) {
		this.error = "Resource not found";
		this.error_description = msg;
	}

}
