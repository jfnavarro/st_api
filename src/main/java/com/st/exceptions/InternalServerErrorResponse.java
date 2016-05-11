package com.st.exceptions;

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
