package com.spatialtranscriptomics.filecontroller;

import com.spatialtranscriptomics.exceptions.*;
import com.spatialtranscriptomics.file.File;
import com.spatialtranscriptomics.model.LastModifiedDate;
import com.spatialtranscriptomics.service.FileService;
import com.spatialtranscriptomics.util.DateOperations;
import com.spatialtranscriptomics.util.HTTPOperations;
import com.spatialtranscriptomics.util.StringOperations;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * The FileController is the base class for all Controllers that serve files. If you want to create a Controller
 * that serves files for the entity Foo you need to subclass this class and create a FooFileController. In the
 * FooFileController is where you add logic that is specific to getting, storing and deleting Foo entities.
 */
public abstract class FileController {

    private static Class getCurrentClass() {
        // Gets the current class. Will return the class of the subclass we are in.
        return new Object() { }.getClass().getEnclosingClass();
    }

    /**
     * Creates a logger with the class whose name is the name of the subclass.
     * @return
     */
    private static Logger createLogger() {

        return Logger.getLogger(getCurrentClass());
    }

    protected static Logger getLog() {
        return logger;
    }

    // Automatically handle the content type.

    // Think about also adding Etag support.
    // In GridFS we have access to the MD5 hash of the file. This is perfect for being used as an Etag.

    @Autowired
    private FileService fileService;

    /**
     * GET /files/[entity]/{filename}
     *
     * Gets a file.
     *
     * HEAD /files/[entity]/{filename}
     *
     * Gets the headers for the file.
     *
     * @param filename The filename of the file that should be returned.
     */
    public ResponseEntity<InputStreamResource> getFile(@PathVariable String filename,
                                                       @RequestHeader(value="If-None-Match", required = false)
                                                       String requestIfNoneMatchHeader,
                                                       @RequestHeader(value="Accept", required = false)
                                                       String requestAcceptHeader,
                                                       @RequestHeader(value="If-Modified-Since", required = false)
                                                       String requestIfModifiedSinceHeader) throws IOException {
        // Check that it is a valid filename.
        validateFilename(filename);

        // Access control.
        // Check that the user is allowed to access the file.

        // Add content type
        File file = fileService.getFile(filename);

        // File not found
        if(file == null) {
            String message = String.format("The %s with filename %s was not found.", getEntityName(), filename);
            throw new CustomNotFoundException(message);
        }

        // Check accept header
        // We do not support content negotiation since each file is only of a certain type.
        validateAcceptHeader(requestAcceptHeader, file);

        // Check Etag.
        if(hasSameEtag(file.getEtag(), requestIfNoneMatchHeader, filename)) {
            String message = String.format("The %s with filename %s has not been modified", getEntityName(), filename);
            throw new CustomNotModifiedException(message);
        }

        if(!serverHasNewerFile(file.getUploadDate(), requestIfModifiedSinceHeader, filename)) {
            String message = String.format("The %s with filename %s has not been modified", getEntityName(), filename);
            throw new CustomNotModifiedException(message);
        }

        Date uploadDate = file.getUploadDate();

        String contentType = file.getContentType();

        InputStream storedFileInputStream = file.getInputStream();

        if(storedFileInputStream == null) {
            String message = String.format("Problem getting input stream for file %s. This should never happen.", filename);
            throw new CustomInternalServerErrorException(message);
        }

        logger.debug(String.format("Successfully retrieved %s with id %s to file store.", getEntityName(), filename));

        InputStreamResource storedFileInputStreamResource = new InputStreamResource(storedFileInputStream);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentLength(file.getLength());
        httpHeaders.set("Content-Type", contentType);
        httpHeaders.set("Last-Modified", DateOperations.getHTTPDate(uploadDate));
        httpHeaders.set("Cache-Control", "public, must-revalidate, no-transform");
        httpHeaders.set("Vary", "Accept-Encoding");

        return new ResponseEntity<InputStreamResource>(storedFileInputStreamResource, httpHeaders, HttpStatus.OK);
    }

    /**
     *  a) If the request would normally result in anything other than a
     *     200 (OK) status, or if the passed If-Modified-Since date is
     *     invalid, the response is exactly the same as for a normal GET.
     *     A date which is later than the server's current time is
     *     invalid.
     *  b) If the variant has been modified since the If-Modified-Since
     *     date, the response is exactly the same as for a normal GET.
     *  c) If the variant has not been modified since a valid If-
     *     Modified-Since date, the server SHOULD return a 304 (Not
     *     Modified) response.
     *
     * http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.25
     * @param uploadDate
     * @param requestIfModifiedSinceHeader
     */
    protected boolean serverHasNewerFile(Date uploadDate, String requestIfModifiedSinceHeader, String filename) {

        if(uploadDate == null) {
            String message = String.format("Last modified time for %s with filename %s is null. This should never happen.",
                    getEntityName(), filename);
            throw new CustomInternalServerErrorException(message);
        }

        // If the If-Modified-Header is invalid we should return the new resource.
        // We should not throw an exception.
        if(StringOperations.isBlank(requestIfModifiedSinceHeader)) {
            return true;
        }

        String uploadDateStr = DateOperations.getHTTPDate(uploadDate).trim();

        // Check if the actual string representations match. This deals with the case where the dates differ in the
        // nano second range. If the dates are exactly equal then the resource has not been modified.
        if(uploadDateStr.equals(requestIfModifiedSinceHeader)) {
            return false;
        }

        DateTime uploadDateTime = new DateTime(uploadDate);
        DateTime requestDateTime = null;

        try {
            requestDateTime = DateOperations.parseHTTPDate(requestIfModifiedSinceHeader);
        } catch (IllegalArgumentException ex) {

            // If we have problem parsing the date then according to RFC2616 we should
            // return the complete resource.
            String message = String.format("Invalid format for If-Modified-Since header for %s with filename %s.",
                    getEntityName(), filename);
            logger.warn(message);
            return true;
        }

        return uploadDateTime.isAfter(requestDateTime);
    }

    /**
     * Validates that the Content-Type of the file matches a content type in the incoming Accept header
     * if the Accept header exists in the request.
     * @param requestAcceptHeader
     * @param file
     */
    private void validateAcceptHeader(String requestAcceptHeader, File file) {
        if(requestAcceptHeader == null) {
            // Nothing to validate.
            return;
        }

        // Blank or empty accept headers are not allowed.
        if(StringOperations.isBlank(requestAcceptHeader)) {
            throw new CustomNotAcceptableException("Accept headers containing only whitespace are not allowed.");
        }

        String fileContentType = file.getContentType();

        for(String acceptableContentType : HTTPOperations.parseAcceptHeader(requestAcceptHeader)) {
            if(fileContentType.equals(acceptableContentType)) {
                // Found a valid content type.
                return;
            }
        }

        String message = String.format("The content type for this %s is %s. This content type is not specified in the Accept header",
                getEntityName(), fileContentType);
        throw new CustomNotAcceptableException(message);
    }

    /**
     * Checks to see if the request and the file in the file store have the same Etag.
     * @param fileEtag
     * @param requestIfNoneMatchHeader
     * @param filename
     * @return
     */
    protected boolean hasSameEtag(String fileEtag, String requestIfNoneMatchHeader, String filename) {
        if(fileEtag == null) {
            String message = String.format("Etag from file store for %s with filename %s is null. This should never happen.",
                    getEntityName(), filename);
            logger.error(message);
            throw new CustomInternalServerErrorException(message);
        }

        // Client did not specify an Etag. Return the resource.
        if(requestIfNoneMatchHeader == null) {
            return false;
        }

        return fileEtag.equals(requestIfNoneMatchHeader);
    }

    /**
     * PUT /files/[entity]/{filename}
     *
     * Stores a file for the given file name. Creates a new file if no file exists. Updates an existing
     * file if there is already a file there.
     *
     * TODO: Take care of entity collision. Right now if two entity types with the same filename are stored then
     * one of the files will overwrite the other. This is because right now all entities are stored under
     * the same filename space. There are two ways of fixing this.
     *
     * 1) Add a prefix to the filename. For example if we are trying to save an image called foo.dat then we could
     * internally store that under "image/foo.dat".
     *
     * 2) Change the location where the files are stored for each of the entity types.
     *
     * I think I will try going with alternative
     *
     * @param filename
     * @param contentType
     * @throws IOException
     */
    public ResponseEntity<String> storeFile(@PathVariable String filename, InputStream requestInputStream,
                          @RequestHeader(value = "Content-Type") String contentType) throws IOException {
        validateFilename(filename);
        validateContentType(contentType, filename);

        // Access control should be added in the subclasses.

        logger.debug(String.format("About to store %s with filename %s in the file store.", getEntityName(), filename));

        InputStream validatedInputStream = validateInputStream(requestInputStream);

        // Store the file.
        fileService.storeFile(validatedInputStream, filename, contentType);

        logger.debug(String.format("Successfully stored %s with id %s in file store.", getEntityName(), filename));

        return new ResponseEntity<String>(HttpStatus.NO_CONTENT);
    }

    /**
     * Validates that the given content type is a valid content type for this specific FileController.
     * @param contentType
     * @param filename
     */
    protected void validateContentType(String contentType, String filename) {

        if(StringOperations.isBlank(contentType)) {
            // This is an error case. You are required to specify the content type of the thing you are trying
            // to store.

            String message = String.format("No Content-Type specified when trying to store %s.", filename);
            throw new CustomBadRequestException(message);
        }

        if(!isValidContentType(contentType)) {

            String message = String.format("%s is not a valid content type when trying to store %s with filename %s.",
                    contentType, getEntityName(), filename);
            throw new CustomBadRequestException(message);
        }
    }

    /**
     * Validates that the given input stream contains a valid
     * @param inputStream
     * @return
     */
    private InputStream validateInputStream(InputStream inputStream) throws IOException {

        // TODO: Add actual validation of the inputStream.
        return inputStream;
    }

    private boolean isValidContentType(String contentType) {
        return this.validContentTypes.contains(contentType);
    }

    public void setValidContentTypes(String[] validContentTypes) {
        if(validContentTypes == null || validContentTypes.length == 0) {
            throw new IllegalArgumentException("Valid content types can not be empty.");
        }

        for(String contentType : validContentTypes) {
            if(StringOperations.isBlank(contentType)) {
                throw new IllegalArgumentException("Content type can not be blank.");
            }
        }

        this.validContentTypes = new HashSet<String>(Arrays.asList(validContentTypes));
    }

    /**
     * Validates that the given filename is valid.
     * @param filename
     */
    protected void validateFilename(String filename) {

        if(StringOperations.isBlank(filename)) {
            throw new CustomBadRequestException("Filename can not be blank");
        }

        // Should not contain / characters. (This will mess up the Spring request mapping.)
        if(filename.contains("/")) {
            String message = String.format("Filename can not contain the / character. Filename: %s", filename);
            throw new CustomBadRequestException(message);
        }

        // TODO: Filename validation.
        // Should only contain alpha numeric?
    }

    /**
     * DELETE /files/[entity]/{filename}
     *
     * Deletes a file from the file store.
     *
     * @param filename The filename of the file that should be deleted.
     */
    public ResponseEntity<String> deleteFile(String filename) {

        validateFilename(filename);

        // Access control.

        // Constraint control.
        validateDelete(filename);

        // What if you delete something that is not there?

        // Delete the file.
        fileService.deleteFile(filename);

        logger.debug(String.format("Successfully deleted %s with id %s from file store.", getEntityName(), filename));

        return new ResponseEntity<String>(HttpStatus.OK);
    }

    /**
     * GET|HEAD /files/[entity]/lastmodified/{id}
     *
     * Returns the last modified date of a file.
     *
     * TODO: This method should not be here. Instead the client should just make a HEAD http request for the file resource
     * and look at the returned headers.
     *
     * @param filename the image name.
     * @return The date and time the image was last modified.
     */
    public LastModifiedDate getLastModified(@PathVariable String filename) {

        LastModifiedDate lastModifiedDate = fileService.getLastModified(filename);

        if(lastModifiedDate == null) {
            String message = String.format("Failed to find last modified time of %s %s.",
                    getEntityName(), filename);
            logger.debug(message);
        }

        logger.debug(String.format("Returning last modified time of %s %s",
                getEntityName(), filename));

        return lastModifiedDate;
    }

    /**
     * Throws an exception if this object can not be deleted for some reason.
     * @param filename
     */
    protected abstract void validateDelete(String filename);

    public void setFileService(FileService fileService) {
        this.fileService = fileService;
    }

    protected String getEntityName() {
        return ENTITY_NAME;
    }

    private Set<String> validContentTypes;

    // I can not do a validate add since we are working on an input stream.
    // To validate the input stream you have to read the stream twice.
    // Once to validate it and once to save it to the file store.
    //
    // You can get around this by reading in all the bytes of the file
    // into memory. However eventually we will have really large files.
    // So this might cause issues.
    //
    // I'm reluctant to design an API that we know will break in the future.
    //
    // We might want to think about having two states of our files in the
    // file store.
    //
    // Unvalidated - The file is stored but it has not been validated to be correct.
    //               It should then logically be disabled.
    // Validated - The file is validated as being a valid file. It can then be enabled.
    //

    // You could then have a service method called validate that could do the following.
    // 1. Read the file and validate that it is a valid file. (This would probably
    //    be done using streaming since the files are so big.)
    // 2. Mark the file as being valid in the file metadata in the file store.

    private static final Logger logger = createLogger();
    private static final String ENTITY_NAME = "file";

    //
    // Exception handling
    //
    // TODO: Move these out to a global exception handler with a @ControllerAdvice annotation.
    @ExceptionHandler(CustomNotModifiedException.class)
    @ResponseStatus(value = HttpStatus.NOT_MODIFIED)
    public @ResponseBody
    NotModifiedResponse handleNotModifiedException(CustomNotModifiedException ex) {
        return new NotModifiedResponse(ex.getMessage());
    }

    @ExceptionHandler(CustomNotFoundException.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public @ResponseBody
    NotFoundResponse handleNotFoundException(CustomNotFoundException ex) {
        return new NotFoundResponse(ex.getMessage());
    }

    @ExceptionHandler(CustomBadRequestException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public @ResponseBody
    BadRequestResponse handleBadRequestException(CustomBadRequestException ex) {
        logger.warn(ex);
        return new BadRequestResponse(ex.getMessage());
    }

    @ExceptionHandler(CustomNotAcceptableException.class)
    @ResponseStatus(value = HttpStatus.NOT_ACCEPTABLE)
    NotAcceptableResponse handleNotAcceptableException(CustomNotAcceptableException ex) {
        logger.warn(ex);
        return new NotAcceptableResponse(ex.getMessage());
    }

    @ExceptionHandler(CustomInternalServerErrorException.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public @ResponseBody
    CustomInternalServerErrorResponse handleInternalServerException(CustomInternalServerErrorException ex) {
        logger.error(ex);
        return new CustomInternalServerErrorResponse(ex.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public @ResponseBody
    CustomInternalServerErrorResponse handleRuntimeException(RuntimeException ex) {
        String message = String.format("Unknown exception in %s: %s",
                getClass().getSimpleName(), ex.getMessage());
        logger.error(message);
        return new CustomInternalServerErrorResponse(ex.getMessage());
    }
}