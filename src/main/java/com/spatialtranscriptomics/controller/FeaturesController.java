/*
 * Copyright (C) 2012 Spatial Transcriptomics AB
 * Read LICENSE for more information about licensing terms
 * Contact: Jose Fernandez Navarro <jose.fernandez.navarro@scilifelab.se>
 */

package com.spatialtranscriptomics.controller;

import com.spatialtranscriptomics.exceptions.BadRequestResponse;
import com.spatialtranscriptomics.exceptions.CustomBadRequestException;
import com.spatialtranscriptomics.exceptions.CustomInternalServerErrorException;
import com.spatialtranscriptomics.exceptions.CustomInternalServerErrorResponse;
import com.spatialtranscriptomics.exceptions.CustomNotFoundException;
import com.spatialtranscriptomics.exceptions.CustomNotModifiedException;
import com.spatialtranscriptomics.exceptions.NotFoundResponse;
import com.spatialtranscriptomics.exceptions.NotModifiedResponse;
import com.spatialtranscriptomics.model.FeaturesMetadata;
import com.spatialtranscriptomics.model.LastModifiedDate;
import com.spatialtranscriptomics.serviceImpl.FeaturesServiceImpl;
import com.spatialtranscriptomics.serviceImpl.MongoUserDetailsServiceImpl;
import com.spatialtranscriptomics.util.DateOperations;
import static com.spatialtranscriptomics.util.DateOperations.checkIfModified;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * This class is Spring MVC controller class for the API endpoint "rest/features". 
 * It implements the methods available at this endpoint.
 */

@Controller
@RequestMapping("/rest/features")
public class FeaturesController {

    private static final Logger logger = Logger.getLogger(FeaturesController.class);

    @Autowired
    FeaturesServiceImpl featuresService;

    @Autowired
    MongoUserDetailsServiceImpl customUserDetailsService;

    /**
     * GET|HEAD /features/
     *
     * Lists features metadata.
     *
     * @return the list.
     */
    @Secured({"ROLE_CM", "ROLE_ADMIN"})
    @RequestMapping(method = {RequestMethod.GET, RequestMethod.HEAD})
    public @ResponseBody
    List<FeaturesMetadata> listMetadata() {
        
        List<FeaturesMetadata> metadata = featuresService.listMetadata();
        if (metadata == null) {
            logger.info("Returning empty list of features metadata");
            throw new CustomNotFoundException("No metadata found or you don't "
                    + "have permissions to access");
        }
        
        logger.info("Returning list of features metadata");
        return metadata;
    }

    /**
     * Returns the zipped features payload as a file.
     *
     * @param id dataset ID.
     * @param response HTTP response containing the file.
     * @param ifModifiedSince last modified tag.
     */
    @Secured({"ROLE_CM", "ROLE_USER", "ROLE_ADMIN"})
    @RequestMapping(value = "{id}", method = {RequestMethod.GET, RequestMethod.HEAD})
    public void getAsFile(
            @PathVariable String id, 
            HttpServletResponse response,
            @RequestHeader(value="If-Modified-Since", defaultValue="") String ifModifiedSince) {
        
        try {
            FeaturesMetadata meta = featuresService.getMetadata(id);
            InputStream is = featuresService.find(id);
            if (meta == null || is == null) {
                logger.info("Failed to return features as JSON for dataset " + id);
                throw new CustomNotFoundException("A features file for a dataset with ID " 
                        + id + " does not exist, or you don't have permissions to access");
            }
            
            // Check if already newest.
            DateTime reqTime = DateOperations.parseHTTPDate(ifModifiedSince);
            if (reqTime != null && !checkIfModified(meta.getLastModified(), reqTime)) {
                logger.info("Not returning features as JSON for dataset " + id + " since not modified");
                throw new CustomNotModifiedException("This features file has not been modified");
            }
            
            // Copy raw stream into response.
            IOUtils.copy(is, response.getOutputStream());
            response.setContentType("application/json");
            response.addHeader("Content-Encoding", "gzip");
            response.addHeader("Cache-Control", "public, must-revalidate, no-transform");
            response.addHeader("Vary", "Accept-Encoding");
            response.addHeader("Last-modified", DateOperations.getHTTPDateSafely(meta.getLastModified()));
            logger.info("Returning features as raw gzip file for dataset " + id);
            response.flushBuffer();
        } catch (IOException ex) {
            logger.error("Error writing features file to output stream with file " + id, ex);
            throw new RuntimeException("IOError writing features file to HTTP response", ex);
        }
    }

    /**
     * PUT /features/
     * 
     * Adds a gziped JSON features file to S3
     * 
     * @param id the features file name (must be dataset ID).
     * @param features the compressed features file.
     */
    @Secured({"ROLE_CM", "ROLE_ADMIN"})
    @RequestMapping(value = "{id:.+}", method = RequestMethod.PUT)
    public @ResponseBody void add(
            @PathVariable String id, 
            @RequestBody HttpEntity<byte[]> features) {
        // this {id:.+} is a workaround for a spring bug that truncates path
        // variables containing a dot
  
        //add the features file, if present already update
        featuresService.addUpdate(id, features.getBody());
        logger.info("Succesfully added BufferedImage image " + id);
    }
    
    /**
     * DELETE /features/{id}
     * 
     * Deletes a features file.
     * @param id the dataset ID.
     */
    @Secured({"ROLE_CM", "ROLE_ADMIN"})
    @RequestMapping(value = "{id}", method = RequestMethod.DELETE)
    public @ResponseBody void delete(@PathVariable String id) {
        featuresService.delete(id);
        logger.info("Successfully deleted features file for dataset " + id);
    }

    /**
     * GET|HEAD /features/lastmodified/{id}
     * 
     * Finds last modified timestamp of features.
     * @param id the dataset ID.
     * @return the timestamp.
     */
    @Secured({"ROLE_CM", "ROLE_USER", "ROLE_ADMIN"})
    @RequestMapping(value = "/lastmodified/{id}", method = {RequestMethod.GET, RequestMethod.HEAD})
    public @ResponseBody LastModifiedDate getLastModified(@PathVariable String id) {
        
        FeaturesMetadata feat = featuresService.getMetadata(id);
        if (feat == null) {
            logger.info("Failed to return last modified time of features file for dataset " + id);
            throw new CustomNotFoundException("A features file with ID " + id + 
                    " doesn't exist or you don't have permissions to access");
        }
        
        logger.info("Returning last modified time of features file for dataset " + id);
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
        logger.warn(ex);
        return new BadRequestResponse(ex.getMessage());
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
        logger.error("Unknown error in features controller: " + ex.getMessage());
        return new CustomInternalServerErrorResponse(ex.getMessage());
    }
}