package com.st.controller;

import com.st.exceptions.BadRequestResponse;
import com.st.exceptions.CustomBadRequestException;
import com.st.exceptions.CustomInternalServerErrorException;
import com.st.exceptions.CustomInternalServerErrorResponse;
import com.st.exceptions.CustomNotFoundException;
import com.st.exceptions.CustomNotModifiedException;
import com.st.exceptions.NotFoundResponse;
import com.st.exceptions.NotModifiedResponse;
import com.st.model.ImageAlignment;
import com.st.model.LastModifiedDate;
import com.st.serviceImpl.DatasetServiceImpl;
import com.st.serviceImpl.ImageAlignmentServiceImpl;
import com.st.serviceImpl.ImageServiceImpl;
import com.st.util.DateOperations;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import javax.validation.Valid;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * This class is Spring MVC controller class for the API endpoint
 * "rest/imagealignment". It implements the methods available at this endpoint.
 */
@Repository
@Controller
@RequestMapping("/rest/imagealignment")
public class ImageAlignmentController {

    @SuppressWarnings("unused")
    private static final Logger logger = Logger.getLogger(ImageAlignmentController.class);

    @Autowired
    ImageAlignmentServiceImpl imagealignmentService;

    @Autowired
    ImageServiceImpl imageService;
    
    @Autowired
    DatasetServiceImpl datasetService;

    /**
     * GET|HEAD /imagealignment/
     * GET|HEAD /imagealignment/?chip={chipId}
     * 
     * List and list for chip.
     * @param chipId chip ID.
     * @return the list
     */
    @Secured({"ROLE_CM", "ROLE_ADMIN"})
    @RequestMapping(method = {RequestMethod.GET, RequestMethod.HEAD})
    public @ResponseBody
    List<ImageAlignment> list(@RequestParam(value = "chip", required = false) String chipId) {
        List<ImageAlignment> imagealignments = null;
        if (chipId != null) {
            logger.info("Returning list of image alignments for chip " + chipId);
            imagealignments = imagealignmentService.findByChip(chipId);
        } else {
            logger.info("Returning list of image alignments");
            imagealignments = imagealignmentService.list();
        }
        if (imagealignments == null) {
            logger.info("Returning empty list of image alignments");
            throw new CustomNotFoundException("No imagealignments found or you dont have permissions to access them.");
        }
        return imagealignments;
    }

    /**
     * GET|HEAD /imagealignment/{id}
     * 
     * Finds a specified alignment.
     * 
     * @param id the image alignment.
     * @return the alignment.
     */
    @Secured({"ROLE_USER", "ROLE_CM", "ROLE_ADMIN"})
    @RequestMapping(value = "{id}", method = {RequestMethod.GET, RequestMethod.HEAD})
    public @ResponseBody
    HttpEntity<ImageAlignment> get(@PathVariable String id, @RequestHeader(value="If-Modified-Since", defaultValue="") String ifModifiedSince) {
        ImageAlignment imagealignment = imagealignmentService.find(id);
        if (imagealignment == null) {
            logger.info("Failed to return image alignment " + id);
            throw new CustomNotFoundException("An imagealignment with this ID does not exist or you dont have permissions to access it.");
        }
        // Check if already newest.
        DateTime reqTime = DateOperations.parseHTTPDate(ifModifiedSince);
        if (reqTime != null) {
            DateTime resTime = imagealignment.getLast_modified() == null ? new DateTime(2012,1,1,0,0) : imagealignment.getLast_modified();
            // NOTE: Only precision within day.
            resTime = new DateTime(resTime.getYear(), resTime.getMonthOfYear(), resTime.getDayOfMonth(), resTime.getHourOfDay(), resTime.getMinuteOfHour(), resTime.getSecondOfMinute());
            if (!resTime.isAfter(reqTime)) {
                logger.info("Not returning image alignment " + id + " since not modified");
                throw new CustomNotModifiedException("This image alignment has not been modified");
            }
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Cache-Control", "public, must-revalidate, no-transform");
        headers.add("Vary", "Accept-Encoding");
        headers.add("Last-modified", DateOperations.getHTTPDateSafely(imagealignment.getLast_modified()));
        HttpEntity<ImageAlignment> entity = new HttpEntity<ImageAlignment>(imagealignment, headers);
        logger.info("Returning image alignment " + id);
        return entity;
    }

    
    /**
     * GET|HEAD /imagealignment/lastmodified/{id}
     * 
     * Returns the last modified date.
     * 
     * @param id the ID.
     * @return the date.
     */
    @Secured({"ROLE_USER", "ROLE_CM", "ROLE_ADMIN"})
    @RequestMapping(value = "/lastmodified/{id}", method = {RequestMethod.GET, RequestMethod.HEAD})
    public @ResponseBody
    LastModifiedDate getLastModified(@PathVariable String id) {
        ImageAlignment imagealignment = imagealignmentService.find(id);
        if (imagealignment == null) {
            logger.info("Failed to return last modified time of image alignment " + id);
            throw new CustomNotFoundException("An image alignment with this ID does not exist or you dont have permissions to access it.");
        }
        logger.info("Returning last modified time of image alignment " + id);
        return new LastModifiedDate(imagealignment.getLast_modified());
    }

    /**
     * POST /imagealignment/
     * 
     * Adds an image alignment.
     * 
     * @param imagealignment the alignment.
     * @param result the binding.
     * @return the alignment with ID assigned.
     */
    @Secured({"ROLE_CM", "ROLE_ADMIN"})
    @RequestMapping(method = RequestMethod.POST)
    public @ResponseBody
    ImageAlignment add(@RequestBody @Valid ImageAlignment imagealignment, BindingResult result) {
        // ImageAlignment validation
        if (result.hasErrors()) {
            logger.info("Failed to add image alignment. Missing fields?");
            throw new CustomBadRequestException("Image alignment is invalid. Missing required fields?");
        }
        if (imagealignment.getId() != null) {
            logger.info("Failed to add image alignment. ID set by user.");
            throw new CustomBadRequestException("The image alignment you want to add must not have an ID. The ID will be autogenerated.");
        }
        if (imagealignmentService.findByName(imagealignment.getName()) != null) {
            logger.info("Failed to add image alignment. Duplicate name.");
            throw new CustomBadRequestException("An image alignment with this name already exists. Image alignment names are unique.");
        }
        logger.info("Successfully added image alignment " + imagealignment.getId());
        return imagealignmentService.add(imagealignment);
    }

    /**
     * PUT /imagealignment/{id}
     * 
     * Updates an image alignment.
     * 
     * @param id the alignment ID.
     * @param imagealignment the alignment.
     * @param result the binding.
     */
    @Secured({"ROLE_CM", "ROLE_ADMIN"})
    @RequestMapping(value = "{id}", method = RequestMethod.PUT)
    public @ResponseBody
    void update(@PathVariable String id, @RequestBody @Valid ImageAlignment imagealignment,
            BindingResult result) {
        // ImageAlignment validation
        if (result.hasErrors()) {
            logger.info("Failed to update image alignment. Missing fields?");
            throw new CustomBadRequestException("Image alignment is invalid. Missing required fields?");
        }
        if (!id.equals(imagealignment.getId())) {
            logger.info("Failed to update image alignment. ID mismatch.");
            throw new CustomBadRequestException("Image alignment ID in request URL does not match ID in content body.");
        } else if (imagealignmentService.find(id) == null) {
            logger.info("Failed to update image alignment. Duplicate name.");
            throw new CustomBadRequestException("An Image alignment with this ID does not exist or you don't have permissions to access it.");
        } else {
            imagealignmentService.update(imagealignment);
            logger.info("Successfully updated image alignment " + id);
        }
    }

    /**
     * DELETE /imagealignment/{id}
     * 
     * Deletes an alignment.
     * 
     * @param id the alignment ID.
     * @param cascade true to cascade delete dependencies.
     */
    @Secured({"ROLE_CM", "ROLE_ADMIN"})
    @RequestMapping(value = "{id}", method = RequestMethod.DELETE)
    public @ResponseBody
    void delete(@PathVariable String id,
            @RequestParam(value = "cascade", required = false, defaultValue = "true") boolean cascade) {
        if (!imagealignmentService.deleteIsOkForCurrUser(id)) {
            logger.info("Failed to delete image alignment " + id + " Missing permissions.");
            throw new CustomBadRequestException("You do not have permission to delete this image alignment.");
        }
        ImageAlignment imal = imagealignmentService.find(id);
        imagealignmentService.delete(id);
        logger.info("Successfully deleted image alignment " + id);
        if (cascade && imal != null) {
            imagealignmentService.delete(id);
            datasetService.setUnabledForImageAlignment(id);
            HashSet<String> todel = new HashSet<>(1024);
            todel.add(imal.getFigure_blue());
            todel.add(imal.getFigure_red());
            List<ImageAlignment> imals = imagealignmentService.list();
            for (ImageAlignment ia : imals) {
                if (!ia.getId().equals(id)) {
                    todel.remove(ia.getFigure_blue());
                    todel.remove(ia.getFigure_red());
                }
            }
            for (String sid : new ArrayList<>(todel)) {
                imageService.delete(sid);
            }
            logger.info("Successfully cascade-deleted dependencies for image alignment " + id);
        }
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
        logger.error("Unknown error in image alignment controller: " + ex.getMessage());
        return new CustomInternalServerErrorResponse(ex.getMessage());
    }

}
