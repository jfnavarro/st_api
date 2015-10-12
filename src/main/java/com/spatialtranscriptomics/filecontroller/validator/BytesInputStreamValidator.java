package com.spatialtranscriptomics.filecontroller.validator;

import org.apache.commons.io.IOUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * The BytesInputStreamValidator is an abstract base class for writing InputStreamValidators.
 * It downloads the entire stream to a temporary byte array which is then validated using
 * a template method that is implemented in a subclass.
 */
public abstract class BytesInputStreamValidator implements InputStreamValidator {

    /**
     * Validates that the input stream is valid by reading the input stream into a byte array
     * and then validating the byte array.
     * @param inputStream The input stream that should be validated.
     * @param contentType The content type of the incoming input stream.
     * @param filename The filename of the file which is to be validated.
     * @return
     * @throws IOException
     */
    @Override
    public InputStream validate(InputStream inputStream, String contentType, String filename) throws IOException {
        byte[] bytes = IOUtils.toByteArray(inputStream);

        validateBytes(bytes, contentType, filename);

        return new ByteArrayInputStream(bytes);
    }

    /**
     * Validates that the given byte array is valid.
     * @param bytes
     * @param contentType
     * @param filename
     * @throws IOException
     */
    protected abstract void validateBytes(byte[] bytes, String contentType, String filename) throws IOException;
}
