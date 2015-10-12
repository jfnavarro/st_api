package com.spatialtranscriptomics.filecontroller.validator;

import java.io.IOException;
import java.io.InputStream;

/**
 * The NoValidationInputStreamValidator is an InputStreamValidator that performs no validation of its input.
 */
public class NoValidationInputStreamValidator implements InputStreamValidator {
    @Override
    public InputStream validate(InputStream inputStream, String contentType, String filename) throws IOException {
        return inputStream;
    }
}
