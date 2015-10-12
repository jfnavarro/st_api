package com.spatialtranscriptomics.filecontroller.validator;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.*;

/**
 * Created by henriktreadup on 10/9/15.
 */
public class BytesInputStreamValidatorTest {

    private InputStream getInputStream(byte[] bytes) {
        return new ByteArrayInputStream(bytes);
    }

    @Test
    public void testBytesInputStreamValidator() throws IOException {
        final byte[] expectedBytes = "This is the test data".getBytes();
        final String exptectedContentType = "Expected-Content-Type";
        final String expectedFilename = "The expected filename";
        final InputStream testInputStream = getInputStream(expectedBytes);

        TestBytesInputStreamValidator bytesInputStreamValidator = new TestBytesInputStreamValidator(expectedBytes,
                exptectedContentType, expectedFilename);

        bytesInputStreamValidator.validate(testInputStream, exptectedContentType, expectedFilename);
        assertTrue(bytesInputStreamValidator.validationWasCalled());
    }


    private static class TestBytesInputStreamValidator extends BytesInputStreamValidator {

        public TestBytesInputStreamValidator(byte[] expectedBytes, String expectedContentType, String expectedFilename) {
            this.expectedBytes = expectedBytes;
            this.expectedContentType = expectedContentType;
            this.expectedFilename = expectedFilename;
        }

        @Override
        protected void validateBytes(byte[] bytes, String contentType, String filename) {
            assertFalse(validationCalled);
            assertArrayEquals(expectedBytes, bytes);
            assertEquals(expectedContentType, contentType);
            assertEquals(expectedFilename, filename);
            validationCalled = true;
        }

        private byte[] expectedBytes;
        private String expectedContentType;
        private String expectedFilename;
        private boolean validationCalled = false;

        public boolean validationWasCalled() {
            return validationCalled;
        }
    }


}
