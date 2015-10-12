package com.spatialtranscriptomics.filecontroller.validator;

import java.io.IOException;
import java.io.InputStream;

/**
 * An InputStreamValidator is used to validate an InputStream of a file that is about to be stored
 * in the file store.
 */
public interface InputStreamValidator {

    /**
     * Validates an input stream. If the incoming InputStream represents an invalid entity
     * then this method will throw a CustomBadArgumentException.
     * @param inputStream The input stream that should be validated.
     * @param contentType The content type of the incoming input stream.
     * @param filename The filename of the file which is to be validated.
     * @return A new input stream which is equivalent to the original InputStream.
     * @throws IOException
     */
    InputStream validate(InputStream inputStream, String contentType, String filename) throws IOException;
}
