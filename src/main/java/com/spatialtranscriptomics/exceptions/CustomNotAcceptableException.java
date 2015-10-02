package com.spatialtranscriptomics.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * The CustomNotAcceptableException is used when you want to return a 406 Not Acceptable
 * status code. This will mostly happen when content negotiation fails.
 */
@ResponseStatus(value= HttpStatus.NOT_ACCEPTABLE)
public class CustomNotAcceptableException extends RuntimeException {

    /**
     * Constructor.
     * @param message message.
     */
    public CustomNotAcceptableException(String message) {
        super(message);
    }
}
