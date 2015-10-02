package com.spatialtranscriptomics.filecontroller;

import com.spatialtranscriptomics.exceptions.*;
import com.spatialtranscriptomics.file.TestFile;
import com.spatialtranscriptomics.service.FileService;
import com.spatialtranscriptomics.util.DateOperations;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Tests the FileController base class.
 */
public class FileControllerTest {

    private static final String VALID_FILENAME = "foo.dat";
    private static final String BAD_FILENAME = "slash/not_allowed.dat";
    private static final String FIRST_VALID_CONTENT_TYPE = "application/foo";
    private static final String SECOND_VALID_CONTENT_TYPE = "application/bar";
    private static final String INVALID_CONTENT_TYPE = "invalid_content_type";
    private static final String[] VALID_CONTENT_TYPES = { FIRST_VALID_CONTENT_TYPE, SECOND_VALID_CONTENT_TYPE};

    private FileController fileController;
    private FileService fileService;

    @Before
    public void setUp() {
        fileController = new TestFileController();
        fileController.setValidContentTypes(VALID_CONTENT_TYPES);
        fileService = mock(FileService.class);
        fileController.setFileService(fileService);
    }

    private InputStream getTestInputStream(String input) {
        return new ByteArrayInputStream(input.getBytes());
    }

    //
    // Test getFile method
    //

    /**
     * Test that you can not get a file with a bad filename.
     */
    @Test(expected = CustomBadRequestException.class)
    public void testGetFileWithBadFilename() throws IOException {
        final String etag = null;
        final String acceptHeader = String.format("%s, %s", FIRST_VALID_CONTENT_TYPE, SECOND_VALID_CONTENT_TYPE);
        final String requestIfModifiedSinceHeader = null;
        fileController.getFile(BAD_FILENAME, etag, acceptHeader, requestIfModifiedSinceHeader);
    }

    /**
     * Test that you can get an error for a missing file.
     */
    @Test(expected = CustomNotFoundException.class)
    public void testGetMissingFile() throws IOException {
        final String etag = null;
        final String acceptHeader = String.format("%s, %s", FIRST_VALID_CONTENT_TYPE, SECOND_VALID_CONTENT_TYPE);
        final String requestIfModifiedSinceHeader = null;

        when(fileService.getFile(VALID_FILENAME)).thenReturn(null);

        fileController.getFile(VALID_FILENAME, etag, acceptHeader, requestIfModifiedSinceHeader);
    }

    /**
     * Test that you get an error with a bad accept header.
     */
    @Test(expected = CustomNotAcceptableException.class)
    public void testBlankAcceptHeader() throws IOException {
        final String etag = null;
        final String acceptHeader = "   ";
        final String requestIfModifiedSinceHeader = null;

        TestFile testFile = new TestFile();
        testFile.setContentType(FIRST_VALID_CONTENT_TYPE);

        when(fileService.getFile(VALID_FILENAME)).thenReturn(testFile);

        fileController.getFile(VALID_FILENAME, etag, acceptHeader, requestIfModifiedSinceHeader);
    }

    /**
     * Tests that we get an error when we try to get a file where the accept header
     * does not contain the content type of the file.
     */
    @Test(expected = CustomNotAcceptableException.class)
    public void testNonMatchingAcceptHeader() throws IOException{
        final String etag = null;
        final String acceptHeader = INVALID_CONTENT_TYPE;
        final String requestIfModifiedSinceHeader = null;

        TestFile testFile = new TestFile();
        testFile.setContentType(FIRST_VALID_CONTENT_TYPE);

        when(fileService.getFile(VALID_FILENAME)).thenReturn(testFile);

        fileController.getFile(VALID_FILENAME, etag, acceptHeader, requestIfModifiedSinceHeader);
    }

    /**
     * Tests that we get an error if the Etag from the file store is null.
     */
    @Test(expected = CustomInternalServerErrorException.class)
    public void testNullFileStoreEtag() throws IOException {
        final String requestIfNoneMatchHeader = "12345";
        final String acceptHeader = FIRST_VALID_CONTENT_TYPE;
        final String requestIfModifiedSinceHeader = null;

        TestFile testFile = new TestFile();
        testFile.setContentType(FIRST_VALID_CONTENT_TYPE);
        testFile.setEtag(null); // Redundant but this is specifically what we want to test.

        when(fileService.getFile(VALID_FILENAME)).thenReturn(testFile);

        fileController.getFile(VALID_FILENAME, requestIfNoneMatchHeader, acceptHeader, requestIfModifiedSinceHeader);
    }

    /**
     * Tests that we get a not modified exception if the If-None-Match header matches the files Etag.
     */
    @Test(expected = CustomNotModifiedException.class)
    public void testSameEtag() throws IOException {
        final String fileEtag = "12345";
        final String requestIfNoneMatchHeader = fileEtag;
        final String acceptHeader = FIRST_VALID_CONTENT_TYPE;
        final String requestIfModifiedSinceHeader = null;

        TestFile testFile = new TestFile();
        testFile.setContentType(FIRST_VALID_CONTENT_TYPE);
        testFile.setEtag(fileEtag);

        when(fileService.getFile(VALID_FILENAME)).thenReturn(testFile);

        fileController.getFile(VALID_FILENAME, requestIfNoneMatchHeader, acceptHeader, requestIfModifiedSinceHeader);
    }

    /**
     * Tests that we get a not modified exception if we use the exact same If-Modified-Since header as the upload date.
     */
    @Test(expected = CustomNotModifiedException.class)
    public void testSameLastModifiedHeader() throws IOException {
        final String fileEtag = "12345";
        final String requestIfNoneMatchHeader = null;
        final String acceptHeader = FIRST_VALID_CONTENT_TYPE;
        final Date date = new Date();
        final String requestIfModifiedSinceHeader = DateOperations.getHTTPDate(date);

        TestFile testFile = new TestFile();
        testFile.setContentType(FIRST_VALID_CONTENT_TYPE);
        testFile.setEtag(fileEtag);
        testFile.setUploadDate(date);

        when(fileService.getFile(VALID_FILENAME)).thenReturn(testFile);

        fileController.getFile(VALID_FILENAME, requestIfNoneMatchHeader, acceptHeader, requestIfModifiedSinceHeader);
    }

    /**
     * Tests that we get an error if the file InputStream is null.
     */
    @Test(expected = CustomInternalServerErrorException.class)
    public void testNullFileInputStream() throws IOException {
        final String fileEtag = "12345";
        final String requestIfNoneMatchHeader = null;
        final String acceptHeader = FIRST_VALID_CONTENT_TYPE;
        final String requestIfModifiedSinceHeader = null;

        TestFile testFile = new TestFile();
        testFile.setContentType(FIRST_VALID_CONTENT_TYPE);
        testFile.setEtag(fileEtag); // Redundant but this is specifically what we want to test.

        when(fileService.getFile(VALID_FILENAME)).thenReturn(testFile);

        fileController.getFile(VALID_FILENAME, requestIfNoneMatchHeader, acceptHeader, requestIfModifiedSinceHeader);
    }

    /**
     * Tests that it is possible to get a file.
     */
    @Test
    public void testGetFile() throws IOException {
        final String testFileContents = "The contents of the test file";
        final String fileEtag = "12345";
        final String requestIfNoneMatchHeader = null;
        final String acceptHeader = FIRST_VALID_CONTENT_TYPE;
        final long contentLength = testFileContents.length();
        final Date expectedUploadDate = new Date();
        final String requestIfModifiedSinceHeader = null;

        TestFile testFile = new TestFile();
        testFile.setContentType(FIRST_VALID_CONTENT_TYPE);
        testFile.setEtag(fileEtag); // Redundant but this is specifically what we want to test.
        testFile.setInputStream(getTestInputStream(testFileContents));
        testFile.setLength(contentLength);
        testFile.setUploadDate(expectedUploadDate);

        when(fileService.getFile(VALID_FILENAME)).thenReturn(testFile);

        ResponseEntity<InputStreamResource> responseEntity
                = fileController.getFile(VALID_FILENAME, requestIfNoneMatchHeader, acceptHeader, requestIfModifiedSinceHeader);

        // Validate contents of the file.
        byte[] actualBytes = IOUtils.toByteArray(responseEntity.getBody().getInputStream());
        byte[] expectedBytes = testFileContents.getBytes();

        assertArrayEquals(expectedBytes, actualBytes);

        // Validate headers.
        HttpHeaders actualHttpHeaders = responseEntity.getHeaders();

        // Validate content length
        assertEquals(testFileContents.getBytes().length, actualHttpHeaders.getContentLength());

        // Validate content type
        assertHttpHeader(FIRST_VALID_CONTENT_TYPE, "Content-Type", actualHttpHeaders);

        // Validate Last-Modified
        assertHttpHeader(DateOperations.getHTTPDate(expectedUploadDate), "Last-Modified", actualHttpHeaders);

        // Validate Cache-Control
        assertHttpHeader("public, must-revalidate, no-transform", "Cache-Control", actualHttpHeaders);

        // Validate Vary
        assertHttpHeader("Accept-Encoding", "Vary", actualHttpHeaders);
    }

    private void assertHttpHeader(String expectedValue, String header, HttpHeaders httpHeaders) {
        List<String> actualHeaderList = httpHeaders.get(header);
        assertEquals(1, actualHeaderList.size());
        assertEquals(expectedValue, actualHeaderList.get(0));
    }

    //
    // Test storeFile method
    //

    /**
     * Test that it is possible to store an normal file.
     */
    @Test
    public void testStoreFile() throws IOException {

        final String testString = "This is the test string.";

        InputStream testInputStream = getTestInputStream(testString);

        fileController.storeFile(VALID_FILENAME, testInputStream, FIRST_VALID_CONTENT_TYPE);

        verify(fileService).storeFile(same(testInputStream), eq(VALID_FILENAME), eq(FIRST_VALID_CONTENT_TYPE));
    }

    /**
     * Test that storing a file with a bad filename will fail.
     */
    @Test(expected = CustomBadRequestException.class)
    public void testStoreFileWithBadName() throws IOException {
        final String testString = "This is the test string.";

        InputStream testInputStream = getTestInputStream(testString);

        fileController.storeFile(BAD_FILENAME, testInputStream, FIRST_VALID_CONTENT_TYPE);
    }

    /**
     * Test that storing a file with a bad content type will fail.
     */
    @Test(expected = CustomBadRequestException.class)
    public void testStoreFileWithBadContentType() throws IOException {
        final String testString = "This is the test string.";

        InputStream testInputStream = getTestInputStream(testString);

        fileController.storeFile(BAD_FILENAME, testInputStream, INVALID_CONTENT_TYPE);
    }

    //
    // Test deleteFile method
    //

    /**
     * Test that it is possible to delete a normal file.
     */
    @Test
    public void testDeleteFile() {
        // Tell the mock to do nothing when the delete method gets called.
        doNothing().when(fileService).deleteFile(VALID_FILENAME);

        // Call the method that is to be tested.
        fileController.deleteFile(VALID_FILENAME);

        // Verify that the delete method was called on the FileService.
        verify(fileService).deleteFile(VALID_FILENAME);
    }

    /**
     * Test that you get an exception when trying to delete a file with a bad filename.
     */
    @Test(expected = CustomBadRequestException.class)
    public void testDeleteFileWithBadFilename() {
        // Call the method that is to be tested.
        fileController.deleteFile(BAD_FILENAME);
    }

    //
    // Test validateFilename
    //

    /**
     * Test that validating a null filename throws an exception.
     */
    @Test(expected = CustomBadRequestException.class)
    public void testNullFilename() {
        PublicValidateFilenameFileController fileController = new PublicValidateFilenameFileController();
        fileController.validateFilename(null);
    }

    /**
     * Test that validating an empty filename throws an exception.
     */
    @Test(expected = CustomBadRequestException.class)
    public void testEmptyFilename() {
        PublicValidateFilenameFileController fileController = new PublicValidateFilenameFileController();
        fileController.validateFilename("");
    }

    /**
     * Test that validating a blank filename throws an exception.
     */
    @Test(expected = CustomBadRequestException.class)
    public void testBlankFilename() {
        PublicValidateFilenameFileController fileController = new PublicValidateFilenameFileController();
        fileController.validateFilename("   ");
    }

    /**
     * Test class used to for testing the validate filename method.
     * TODO: Perhaps we should move out the filename validator to its own separate class.
     * Testing a private method is kind of a code smell.
     */
    private class PublicValidateFilenameFileController extends FileController {

        @Override
        public void validateFilename(String filename) {
            super.validateFilename(filename);
        }

        @Override
        protected void validateDelete(String filename) {
            // Do nothing.
        }
    }

    //
    // Test validateContentType
    //
    @Test
    public void testValidContentType() {
        PublicValidateContentTypeFileController fileController = new PublicValidateContentTypeFileController();
        fileController.setValidContentTypes(VALID_CONTENT_TYPES);

        fileController.validateContentType(SECOND_VALID_CONTENT_TYPE, VALID_FILENAME);
        fileController.validateContentType(FIRST_VALID_CONTENT_TYPE, VALID_FILENAME);
    }

    @Test(expected = CustomBadRequestException.class)
    public void testInvalidContentType() {
        PublicValidateContentTypeFileController fileController = new PublicValidateContentTypeFileController();
        fileController.setValidContentTypes(VALID_CONTENT_TYPES);

        fileController.validateContentType(INVALID_CONTENT_TYPE, VALID_FILENAME);
    }

    /**
     * Test class used for testing the validate content type method.
     * TODO: Perhaps we should move out the content type validator to its own separate class.
     * Testing a private method is kind of a code smell.
     */
    private class PublicValidateContentTypeFileController extends FileController {

        @Override
        public void validateContentType(String contentType, String filename) {
            super.validateContentType(contentType, filename);
        }

        @Override
        protected void validateDelete(String filename) {
            // Do nothing
        }
    }

    //
    // Test serverHasNewerFile
    //

    /**
     * Test that a null upload date causes a Internal Server Error.
     */
    @Test(expected = CustomInternalServerErrorException.class)
    public void testWithNullUploadDate() {
        final String filename = "TheFilename.dat";
        final Date uploadDate = null;
        final String requestIfModifiedSince = DateOperations.getHTTPDate(new Date());

        PublicServerHasNewerFileFileController fileFileController = new PublicServerHasNewerFileFileController();
        fileFileController.serverHasNewerFile(uploadDate, requestIfModifiedSince, filename);
    }

    /**
     * Test that a blank If-Modified-Since header causes a new resource to be returned.
     */
    @Test
    public void testWithBlankIfModifiedSinceHeader() {
        final String filename = "TheFilename.dat";
        final Date uploadDate = new Date();
        final String requestIfModifiedSince = "  ";

        PublicServerHasNewerFileFileController fileFileController = new PublicServerHasNewerFileFileController();
        assertTrue(fileFileController.serverHasNewerFile(uploadDate, requestIfModifiedSince, filename));
    }

    /**
     * Test with the exact same upload date and If-Modified-Since header.
     */
    @Test
    public void testWithSameUploadDateAndIfModifiedSinceHeader() {
        final String filename = "TheFilename.dat";
        final Date uploadDate = new Date();
        final String requestIfModifiedSince = DateOperations.getHTTPDate(uploadDate);

        PublicServerHasNewerFileFileController fileFileController = new PublicServerHasNewerFileFileController();
        assertFalse(fileFileController.serverHasNewerFile(uploadDate, requestIfModifiedSince, filename));
    }

    /**
     * Test with garbage If-Modified-Since header.
     */
    @Test
    public void testWithGarbageIfModifiedSinceHeader() {
        final String filename = "TheFilename.dat";
        final Date uploadDate = new Date();
        final String requestIfModifiedSince = "This is not a valid If-Modified-Since header";

        PublicServerHasNewerFileFileController fileFileController = new PublicServerHasNewerFileFileController();
        assertTrue(fileFileController.serverHasNewerFile(uploadDate, requestIfModifiedSince, filename));
    }

    /**
     * Test with newer server resource.
     */
    @Test
    public void testWithNewerServerResource() {
        final String filename = "TheFilename.dat";
        final Date date = new Date();
        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.SECOND, -1);
        final Date beforeDate = calendar.getTime();

        final String requestIfModifiedSince = DateOperations.getHTTPDate(beforeDate);

        PublicServerHasNewerFileFileController fileFileController = new PublicServerHasNewerFileFileController();
        assertTrue(fileFileController.serverHasNewerFile(date, requestIfModifiedSince, filename));
    }

    /**
     * Test with newer server resource.
     */
    @Test
    public void testWithOlderServerResource() {
        final String filename = "TheFilename.dat";
        final Date date = new Date();
        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.SECOND, 1);
        final Date afterDate = calendar.getTime();

        final String requestIfModifiedSince = DateOperations.getHTTPDate(afterDate);

        PublicServerHasNewerFileFileController fileFileController = new PublicServerHasNewerFileFileController();
        assertFalse(fileFileController.serverHasNewerFile(date, requestIfModifiedSince, filename));
    }

    /**
     * Test class used to for testing the serverHasNewerFile method.
     * TODO: Perhaps we should move out the filename validator to its own separate class.
     * Testing a private method is kind of a code smell.
     */
    private class PublicServerHasNewerFileFileController extends FileController {

        @Override
        public boolean serverHasNewerFile(Date uploadDate, String requestIfModifiedSinceHeader, String filename) {
            return super.serverHasNewerFile(uploadDate, requestIfModifiedSinceHeader, filename);
        }

        @Override
        protected void validateDelete(String filename) {
            // Do Nothing
        }
    }

    /**
     * The TestFileController is used for unit testing the abstract FileController class.
     */
    private static class TestFileController extends FileController {

        @Override
        protected void validateDelete(String filename) {
            // Do nothing.
        }
    }
}