package com.spatialtranscriptomics.exceptions;

/**
 * The NotAcceptableResponse is returned in response to a 406 Not Acceptable http status.
 */
public class NotAcceptableResponse {

    public String error;
    public String error_description;

    /**
     * Constructor.
     * @param msg message.
     */
    public NotAcceptableResponse(String msg) {
        this.error = "Not Acceptable";
        this.error_description = msg;
    }
}
