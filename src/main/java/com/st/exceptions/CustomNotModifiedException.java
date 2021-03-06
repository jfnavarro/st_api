package com.st.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception class used for returning HTTP response type
 * "304 Not Modified".
 */
@ResponseStatus(value = HttpStatus.NOT_MODIFIED)
public class CustomNotModifiedException extends RuntimeException {

    /**
     * Auto-generated serial number.
     */
    private static final long serialVersionUID = 3398348494792632720L;

    /**
     * Constructor.
     * @param message message.
     */
    public CustomNotModifiedException(String message) {
        super(message);
    }
}
