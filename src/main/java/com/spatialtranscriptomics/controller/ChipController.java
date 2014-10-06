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
import com.spatialtranscriptomics.model.Chip;
import com.spatialtranscriptomics.model.ImageAlignment;
import com.spatialtranscriptomics.model.LastModifiedDate;
import com.spatialtranscriptomics.serviceImpl.ChipServiceImpl;
import com.spatialtranscriptomics.serviceImpl.DatasetServiceImpl;
import com.spatialtranscriptomics.serviceImpl.ImageAlignmentServiceImpl;
import com.spatialtranscriptomics.util.DateOperations;
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
 * This class is Spring MVC controller class for the API endpoint "rest/chip".
 * It implements the methods available at this endpoint.
 */
@Repository
@Controller
@RequestMapping("/rest/chip")
public class ChipController {

    @SuppressWarnings("unused")
    private static final Logger logger = Logger.getLogger(ChipController.class);

    @Autowired
    ChipServiceImpl chipService;

    @Autowired
    ImageAlignmentServiceImpl imageAlignmentService;

    @Autowired
    DatasetServiceImpl datasetService;

    /**
     * GET|HEAD /chip/
     *
     * Lists chips.
     * @return the list.
     */
    @Secured({"ROLE_CM", "ROLE_ADMIN"})
    @RequestMapping(method = {RequestMethod.GET, RequestMethod.HEAD})
    public @ResponseBody
    List<Chip> list() {
        List<Chip> chips = chipService.list();
        if (chips == null) {
            logger.info("Returning empty list of chips");
            throw new CustomNotFoundException("No chips found or you dont have permissions to access them.");
        }
        logger.info("Returning list of chips");
        return chips;
    }

    /**
     * GET|HEAD /chip/{id}
     *
     * Finds a chip.
     * @param id the chip ID.
     * @param ifModifiedSince request timestamp.
     * @return the chip.
     */
    @Secured({"ROLE_CM", "ROLE_USER", "ROLE_ADMIN"})
    @RequestMapping(value = "{id}", method = {RequestMethod.GET, RequestMethod.HEAD})
    public @ResponseBody
    HttpEntity<Chip> get(@PathVariable String id, @RequestHeader(value="If-Modified-Since", defaultValue="") String ifModifiedSince) {
        Chip chip = chipService.find(id);
        // Check existence.
        if (chip == null) {
            logger.info("Failed to return chip " + id + ". Permission denied or missing.");
            throw new CustomNotFoundException("A chip with this ID does not exist or you dont have permissions to access it.");
        }
        // Check if already newest.
        DateTime reqTime = DateOperations.parseHTTPDate(ifModifiedSince);
        if (reqTime != null) {
            DateTime resTime = chip.getLast_modified() == null ? new DateTime(2012,1,1,0,0) : chip.getLast_modified();
            // NOTE: Only precision within day.
            resTime = new DateTime(resTime.getYear(), resTime.getMonthOfYear(), resTime.getDayOfMonth(), resTime.getHourOfDay(), resTime.getMinuteOfHour(), resTime.getSecondOfMinute());
            if (!resTime.isAfter(reqTime)) {
                logger.info("Not returning chip " + id + " since not modified");
                throw new CustomNotModifiedException("This chip has not been modified");
            }
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Cache-Control", "public, must-revalidate, no-transform");
        headers.add("Vary", "Accept-Encoding");
        headers.add("Last-modified", DateOperations.getHTTPDateSafely(chip.getLast_modified()));
        HttpEntity<Chip> entity = new HttpEntity<Chip>(chip, headers);
        logger.info("Returning chip " + id);
        return entity;
    }

    /**
     * GET|HEAD /chip/lastmodified/{id}
     *
     * Finds a chip's last modified timestamp.
     * @param id the chip ID.
     * @return the timestamp.
     */
    @Secured({"ROLE_CM", "ROLE_USER", "ROLE_ADMIN"})
    @RequestMapping(value = "/lastmodified/{id}", method = {RequestMethod.GET, RequestMethod.HEAD})
    public @ResponseBody
    LastModifiedDate getLastModified(@PathVariable String id) {
        Chip chip = chipService.find(id);
        if (chip == null) {
            logger.info("Failed to return last modified time of chip " + id);
            throw new CustomNotFoundException("A chip with this ID does not exist or you dont have permissions to access it.");
        }
        logger.info("Returning last modified time of chip " + id);
        return new LastModifiedDate(chip.getLast_modified() == null ? new DateTime(2012,1,1,0,0) : chip.getLast_modified());
    }

    /**
     * POST /chip/
     * 
     * Adds a chip
     * @param chip the chip.
     * @param result binding.
     * @return the chip with ID assigned.
     */
    @Secured({"ROLE_CM", "ROLE_ADMIN"})
    @RequestMapping(method = RequestMethod.POST)
    public @ResponseBody
    Chip add(@RequestBody @Valid Chip chip, BindingResult result) {
        // Data model validation
        if (result.hasErrors()) {
            logger.info("Failed to add chip. Missing fields?");
            throw new CustomBadRequestException("Chip is invalid. Missing required fields?");
        }
        if (chip.getId() != null) {
            logger.info("Failed to add chip. ID set by user.");
            throw new CustomBadRequestException("The chip you want to add must not have an ID. The ID will be autogenerated.");
        } else if (chipService.findByName(chip.getName()) != null) {
            logger.info("Failed to add chip. Duplicate name.");
            throw new CustomBadRequestException("A chip with this name exists already. Chip names are unique.");
        }
        logger.info("Successfully added chip " + chip.getId());
        return chipService.add(chip);
    }

    /**
     * PUT /chip/{id}
     * 
     * Updates a chip.
     * @param id the chip ID.
     * @param chip the chip.
     * @param result binding.
     */
    @Secured({"ROLE_CM", "ROLE_ADMIN"})
    @RequestMapping(value = "{id}", method = RequestMethod.PUT)
    public @ResponseBody
    void update(@PathVariable String id, @RequestBody @Valid Chip chip, BindingResult result) {
        
        // Data model validation
        if (result.hasErrors()) {
            logger.info("Failed to update chip. Missing fields?");
            throw new CustomBadRequestException("Chip is invalid. Missing required fields?");
        }
        if (!id.equals(chip.getId())) {
            logger.info("Failed to update chip. ID mismatch.");
            throw new CustomBadRequestException("Chip ID in request URL does not match ID in content body.");
        } else if (chipService.find(id) == null) {
            logger.info("Failed to update account. Missing or failed permissions.");
            throw new CustomBadRequestException("A chip with this ID does not exist or you don't have permissions to access it.");
        } else if (!chipService.findByName(chip.getName()).getId().equals(id)) {
            logger.info("Failed to update account. Duplicate username.");
            throw new CustomBadRequestException(
                        "Another chip with this name exists already. Names are unique.");
        } else {
            logger.info("Successfully updated chip " + chip.getId());
            chipService.update(chip);
        }
    }

    /**
     * DELETE /chip/{id}
     * 
     * Deletes a chip.
     * @param id the chip ID.
     * @param cascade true to cascade delete.
     */
    @Secured({"ROLE_CM", "ROLE_ADMIN"})
    @RequestMapping(value = "{id}", method = RequestMethod.DELETE)
    public @ResponseBody
    void delete(@PathVariable String id,
            @RequestParam(value = "cascade", required = false, defaultValue = "true") boolean cascade) {
        if (!chipService.deleteIsOK(id)) {
            logger.info("Failed to delete chip " + id + " Missing permissions.");
            throw new CustomBadRequestException("You do not have permission to delete this chip.");
        }

        if (cascade) {
            List<ImageAlignment> imals = imageAlignmentService.deleteForChip(id);
            if (imals != null) {
                for (ImageAlignment imal : imals) {
                    datasetService.setUnabledForImageAlignment(imal.getId());
                }
            }
            logger.info("Successfully cascade-deleted dependencies for chip " + id);
        }
        chipService.delete(id);
        logger.info("Successfully deleted chip " + id);
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
        logger.error("Unknown error in chip controller: " + ex.getMessage());
        return new CustomInternalServerErrorResponse(ex.getMessage());
    }

}
