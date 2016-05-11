package com.st.exceptions;

/**
 * This class defines the JSON error response body for the CustomInternalServerErrorException
 * NOTE: This response is likely to be truncated and the response body be empty!!!!
 */
public class CustomInternalServerErrorResponse {
    
    public String error;
    public String error_description;

    /**
     * Constructor.
     * @param msg message.
     */
    public CustomInternalServerErrorResponse(String msg) {
            this.error = "Internal server error";
            this.error_description = msg;
    }
}
