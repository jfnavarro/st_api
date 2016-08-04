package com.st.controller;

import com.st.exceptions.BadRequestResponse;
import com.st.exceptions.CustomBadRequestException;
import com.st.exceptions.CustomInternalServerErrorException;
import com.st.exceptions.CustomInternalServerErrorResponse;
import com.st.exceptions.CustomNotFoundException;
import com.st.exceptions.CustomNotModifiedException;
import com.st.exceptions.NotFoundResponse;
import com.st.exceptions.NotModifiedResponse;
import com.st.model.FeaturesMetadata;
import com.st.model.S3Resource;
import com.st.model.LastModifiedDate;
import com.st.serviceImpl.FeaturesServiceImpl;
import com.st.serviceImpl.MongoUserDetailsServiceImpl;
import com.st.util.DateOperations;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
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
        List<FeaturesMetadata> l = featuresService.listMetadata();
        if (l == null) {
            logger.info("Returning empty list of features metadata");
            throw new CustomNotFoundException("No metadata found or you dont "
                    + "have permissions to access them.");
        }
        logger.info("Returning list of features metadata");
        return l;
    }

    /**
     * GET|HEAD /features/json/{id}
     * 
     * Finds features payload wrapped in JSON.
     * 
     * @param id the dataset ID.
     * @param ifModifiedSince request timestamp.
     * @return the account.
     */
    @Secured({"ROLE_CM", "ROLE_USER", "ROLE_ADMIN"})
    @RequestMapping(value = "json/{id}", method = {RequestMethod.GET, RequestMethod.HEAD})
    public @ResponseBody
    HttpEntity<S3Resource> getAsJSON(@PathVariable String id, 
            @RequestHeader(value="If-Modified-Since", defaultValue="") String ifModifiedSince) {
        try {
            FeaturesMetadata meta = featuresService.getMetadata(id);
            InputStream is = featuresService.find(id);
            if (meta == null || is == null) {
                logger.info("Failed to return features as JSON for dataset " + id);
                throw new CustomNotFoundException("A features file for a dataset with t"
                        + "his ID does not exist, or you dont have permissions to access it.");
            }
            // Check if already newest.
            DateTime reqTime = DateOperations.parseHTTPDate(ifModifiedSince);
            if (reqTime != null) {
                DateTime resTime = meta.getLastModified() == null ? 
                        new DateTime(2012,1,1,0,0) : meta.getLastModified();
                // NOTE: Only precision within day.
                resTime = new DateTime(resTime.getYear(), 
                        resTime.getMonthOfYear(), resTime.getDayOfMonth(), 
                        resTime.getHourOfDay(), resTime.getMinuteOfHour(), resTime.getSecondOfMinute());
                if (!resTime.isAfter(reqTime)) {
                    logger.info("Not returning features as JSON for dataset " + id + " since not modified");
                    throw new CustomNotModifiedException("This features file has not been modified");
                }
            }
            byte[] bytes = IOUtils.toByteArray(is);
            S3Resource wrap = new S3Resource("application/json", "gzip", id, bytes);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.add("Cache-Control", "public, must-revalidate, no-transform");
            headers.add("Vary", "Accept-Encoding");
            headers.add("Last-modified", DateOperations.getHTTPDateSafely(meta.getLastModified()));
            HttpEntity<S3Resource> entity = new HttpEntity<>(wrap, headers);
            logger.info("Returning features as JSON for dataset " + id);
            return entity;
        } catch (IOException ex) {
            logger.error("Error writing features file to output stream with file " + id);
            throw new CustomInternalServerErrorException("IOError writing features file to output stream");
        }
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
    public void getAsFile(@PathVariable String id, HttpServletResponse response,
            @RequestHeader(value="If-Modified-Since", defaultValue="") String ifModifiedSince) {
        try {
            FeaturesMetadata meta = featuresService.getMetadata(id);
            InputStream is = featuresService.find(id);
            if (meta == null || is == null) {
                logger.info("Failed to return features as JSON for dataset " + id);
                throw new CustomNotFoundException("A features file for a dataset with "
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
                    logger.info("Not returning features as JSON for dataset " + id + " since not modified");
                    throw new CustomNotModifiedException("This features file has not been modified");
                }
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
            logger.error("Error writing features file to output stream with file " + id);
            throw new RuntimeException("IOError writing features file to HTTP response", ex);
        }
    }

    /**
     * PUT /features/
     * 
     * Adds a features file payload wrapped in JSON.
     * @param id the dataset ID.
     * @param feats the features.
     */
    @Secured({"ROLE_CM", "ROLE_ADMIN"})
    @RequestMapping(value = "{id}", method = RequestMethod.PUT)
    public @ResponseBody
    void addOrUpdate(@PathVariable String id, @RequestBody S3Resource feats) {
        byte[] bytes = feats.getFile();
        if (id != null && bytes != null && bytes.length != 0) {
            boolean updated = featuresService.addUpdate(id, bytes);
            logger.info((updated ? "Updated" : "Added") + " features file for dataset " + id);
        } else {
            logger.error("Failed to add features for dataset " + id +". Empty file?");
            throw new CustomBadRequestException("Failed to add features for dataset " + id +". Is the file empty?");
        }
    }

    /**
     * DELETE /features/{id}
     * 
     * Deletes a features file.
     * @param id the dataset ID.
     */
    @Secured({"ROLE_CM", "ROLE_ADMIN"})
    @RequestMapping(value = "{id}", method = RequestMethod.DELETE)
    public @ResponseBody
    void delete(@PathVariable String id) {
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
    public @ResponseBody
    LastModifiedDate getLastModified(@PathVariable String id) {
        FeaturesMetadata feat = featuresService.getMetadata(id);
        if (feat == null) {
            logger.info("Failed to return last modified time of features file for dataset " + id);
            throw new CustomNotFoundException("A features file with this id does not "
                    + "exist or you do not have permissions to access it.");
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
        return new BadRequestResponse(ex.getMessage());
    }
    
    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public @ResponseBody
    CustomInternalServerErrorResponse handleRuntimeException(CustomInternalServerErrorException ex) {
        logger.error("Unknown error in features controller: " + ex.getMessage());
        return new CustomInternalServerErrorResponse(ex.getMessage());
    }
    
}