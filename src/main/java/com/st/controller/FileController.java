package com.st.controller;

import com.st.exceptions.BadRequestResponse;
import com.st.exceptions.CustomBadRequestException;
import com.st.exceptions.CustomInternalServerErrorException;
import com.st.exceptions.CustomInternalServerErrorResponse;
import com.st.exceptions.CustomNotFoundException;
import com.st.exceptions.CustomNotModifiedException;
import com.st.exceptions.NotFoundResponse;
import com.st.exceptions.NotModifiedResponse;
import com.st.model.FileMetadata;
import com.st.model.LastModifiedDate;
import com.st.serviceImpl.FileServiceImpl;
import com.st.serviceImpl.MongoUserDetailsServiceImpl;
import com.st.util.DateOperations;
import java.io.IOException;
import java.io.InputStream;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MultipartFile;

/**
 * This class is Spring MVC controller class for the API endpoint "rest/files". 
 * It implements the methods available at this endpoint.
 */

@Controller
@RequestMapping("/rest/files")
public class FileController {

    private static final Logger logger = Logger.getLogger(FileController.class);

    @Autowired
    FileServiceImpl filesService;

    @Autowired
    MongoUserDetailsServiceImpl customUserDetailsService;
    
    /**
     * Returns the gzipped file requested for the given dataset.
     *
     * @param filename the name of the file
     * @param id dataset ID.
     * @param response HTTP response containing the file.
     * @param ifModifiedSince last modified tag.
     */
    @Secured({"ROLE_CM", "ROLE_USER", "ROLE_ADMIN"})
    @RequestMapping(value = "{id}", method = {RequestMethod.GET, RequestMethod.HEAD}, 
            produces = MediaType.TEXT_PLAIN_VALUE)
    public void getAsFile(
            @PathVariable String id, HttpServletResponse response,
            @RequestParam(value = "filename", required = true) String filename, 
            @RequestHeader(value = "If-Modified-Since", defaultValue = "") String ifModifiedSince) {
        try {
            FileMetadata meta = filesService.getMetadata(filename, id);
            if (meta == null) {
                logger.info("Failed to return meta info for file  " + id);
                throw new CustomNotFoundException("A file for a dataset with "
                        + "this ID does not exist, or you dont have permissions to access it.");                
            }
            InputStream is = filesService.find(filename, id);
            if (is == null) {
                logger.info("Failed to return file  " + id);
                throw new CustomNotFoundException("A file for a dataset with "
                        + "this ID does not exist, or you dont have permissions to access it.");
            }
            // Check if already newest.
            DateTime reqTime = DateOperations.parseHTTPDate(ifModifiedSince);
            if (reqTime != null) {
                DateTime resTime = meta.getLastModified() == null ? 
                        new DateTime(2012,1,1,0,0) : meta.getLastModified();
                // NOTE: Only precision within day.
                resTime = new DateTime(resTime.getYear(), resTime.getMonthOfYear(), 
                        resTime.getDayOfMonth(), resTime.getHourOfDay(), 
                        resTime.getMinuteOfHour(), resTime.getSecondOfMinute());
                if (!resTime.isAfter(reqTime)) {
                    logger.info("Not returning file for dataset " + id + " since not modified");
                    throw new CustomNotModifiedException("This file has not been modified");
                }
            }
            // Copy raw stream into response.
            IOUtils.copy(is, response.getOutputStream());
            response.setContentType(MediaType.TEXT_PLAIN_VALUE);
            response.addHeader("Content-Encoding", "gzip");
            response.addHeader("Cache-Control", "public, must-revalidate, no-transform");
            response.addHeader("Vary", "Accept-Encoding");
            response.addHeader("Last-modified", DateOperations.getHTTPDateSafely(meta.getLastModified()));
            logger.info("Returning file as raw gzip file for dataset " + id);
            response.flushBuffer();
        } catch (IOException ex) {
            logger.error("Error writing file to output stream with file " + id);
            throw new RuntimeException("IOError writing file to HTTP response", ex);
        }
    }

    /**
     * PUT /files/?filename=xx
     * 
     * Adds a features file payload wrapped in JSON.
     * @param id the dataset ID.
     * @param filename the name of the file
     * @param file
     */
    @Secured({"ROLE_CM", "ROLE_ADMIN"})
    @RequestMapping(value = "{id}", method = RequestMethod.PUT)
    public @ResponseBody
    void addOrUpdate(
            @PathVariable String id, 
            @RequestParam(value = "filename", required = true) String filename,
            @RequestBody MultipartFile file) {
        byte[] bytes = null;
        try {
            bytes = file.getBytes();
        } catch (IOException ex) {
            logger.error("Failed to add file for dataset " + id + ". Invalid file?", ex);
            throw new CustomBadRequestException("Failed to add file for dataset " 
                    + id + ". Is the file valid?");
        }
        if (id != null && bytes != null && bytes.length != 0) {
            final boolean updated = filesService.addUpdate(filename, id, bytes);
            if (updated) {
                logger.info("Updated/Added file for dataset " + id);
            } else {
                logger.error("Failed to add file for dataset " + id + ". S3 error");
                throw new CustomBadRequestException("Failed to add file for dataset " 
                        + id + ". Server problem");                
            }
        } else {
            logger.error("Failed to add file for dataset " + id + ". Empty file?");
            throw new CustomBadRequestException("Failed to add file for dataset " 
                    + id + ". Is the file empty?");
        }
    }

    /**
     * DELETE /files/{id}?filename=xx
     * 
     * Deletes a features file.
     * @param id the dataset ID.
     * @param filename the name of the file
     */
    @Secured({"ROLE_CM", "ROLE_ADMIN"})
    @RequestMapping(value = "{id}", method = RequestMethod.DELETE)
    public @ResponseBody
    void delete(
            @PathVariable String id,
            @RequestParam(value = "filename", required = true) String filename) {
        if (filesService.delete(filename, id)) {
            logger.info("Successfully deleted file for dataset " + id);
        } else {
            logger.info("Error deleting file for dataset " + id);
            throw new CustomBadRequestException("Failed to delete file for dataset " + id);
        }
    }

    /**
     * GET|HEAD /files/lastmodified/{id}?filename=xx
     * 
     * Finds last modified timestamp of features.
     * @param id the dataset ID.
     * @param filename the name of the file
     * @return the timestamp.
     */
    @Secured({"ROLE_CM", "ROLE_USER", "ROLE_ADMIN"})
    @RequestMapping(value = "/lastmodified/{id}", method = {RequestMethod.GET, RequestMethod.HEAD})
    public @ResponseBody
    LastModifiedDate getLastModified(
            @PathVariable String id,
            @RequestParam(value = "filename", required = true) String filename) {
        final FileMetadata feat = filesService.getMetadata(filename, id);
        if (feat == null) {
            logger.info("Failed to return last modified time of file for dataset " + id);
            throw new CustomNotFoundException("A file with this id does not "
                    + "exist or you do not have permissions to access it.");
        }
        logger.info("Returning last modified time of file for dataset " + id);
        return new LastModifiedDate(feat.getLastModified());
    }

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
        return new BadRequestResponse(ex.getMessage());
    }
    
    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public @ResponseBody
    CustomInternalServerErrorResponse handleRuntimeException(CustomInternalServerErrorException ex) {
        logger.error("Unknown error in file controller: " + ex.getMessage());
        return new CustomInternalServerErrorResponse(ex.getMessage());
    }
    
}