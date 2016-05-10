/*
 * Copyright (C) 2014 Spatial Transcriptomics AB
 * Read LICENSE for more information about licensing terms
 * Contact: Jose Fernandez Navarro <jose.fernandez.navarro@scilifelab.se>
 */
package com.st.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * This exception class returns a HTTP response "404 Not Found"
 * with a customized JSON response.
 * (see NotFoundResponse) Used in Controllers.
 */
@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class CustomNotFoundException extends RuntimeException {

    /** Auto-gen ID. */
    private static final long serialVersionUID = 7644203240854556553L;

    /**
     * Constructor.
     * @param message message.
     */
    public CustomNotFoundException(String message) {
        super(message);
    }
}
