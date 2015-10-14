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
import com.spatialtranscriptomics.model.Dataset;
import com.spatialtranscriptomics.model.LastModifiedDate;
import com.spatialtranscriptomics.model.MongoUserDetails;
import com.spatialtranscriptomics.model.Selection;
import com.spatialtranscriptomics.serviceImpl.DatasetServiceImpl;
import com.spatialtranscriptomics.serviceImpl.MongoUserDetailsServiceImpl;
import com.spatialtranscriptomics.serviceImpl.SelectionServiceImpl;
import com.spatialtranscriptomics.util.DateOperations;
import static com.spatialtranscriptomics.util.DateOperations.checkIfModified;
import static com.spatialtranscriptomics.util.HTTPOperations.getHTTPHeaderWithCache;
import java.util.Iterator;
import java.util.List;
import javax.validation.Valid;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
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
 * "rest/selection". It implements the methods available at this endpoint.
 */

@Repository
@Controller
@RequestMapping("/rest/selection")
public class SelectionController {

    @SuppressWarnings("unused")
    private static final Logger logger = Logger.getLogger(SelectionController.class);

    @Autowired
    SelectionServiceImpl selectionService;

    @Autowired
    DatasetServiceImpl datasetService;

    @Autowired
    MongoUserDetailsServiceImpl customUserDetailsService;

    /**
     * GET|HEAD /selection/
     * GET|HEAD /selection/?account={accountId}
     * GET|HEAD /selection/?dataset={datasetId}
     * 
     * Enabled list / list for account / list for dataset / list for task.
     * @param accountId account ID.
     * @param datasetId dataset ID.
     * @return list.
     */
    @Secured({"ROLE_USER", "ROLE_CM", "ROLE_ADMIN"})
    @RequestMapping(method = {RequestMethod.GET, RequestMethod.HEAD})
    public @ResponseBody
    List<Selection> list(
            @RequestParam(value = "account", required = false) String accountId,
            @RequestParam(value = "dataset", required = false) String datasetId) {
        
        List<Selection> selections = null;
        if (accountId != null) {
            logger.info("Returning list of enabled user's selections for account " + accountId);
            selections = selectionService.findByAccount(accountId);
        } else if (datasetId != null) {
            logger.info("Returning list of enabled user's selections for dataset " + datasetId);
            selections = selectionService.findByDataset(datasetId);
        } else {
            // NOTE: Only current user's selections, even for admin.
            MongoUserDetails currentUser = customUserDetailsService.loadCurrentUser();
            selections = selectionService.findByAccount(currentUser.getId());
            logger.info("Returning list of enabled user's selections");
        }
        
        if (selections == null) {
            logger.info("Returning empty list of enabled user's selections");
            throw new CustomNotFoundException("No selections found or you don't "
                    + "have permissions to access");
        }
        
        Iterator<Selection> i = selections.iterator();
        while (i.hasNext()) {
            Selection sel = i.next(); // must be called before you can call i.remove()
            Dataset d = datasetService.find(sel.getDataset_id());
            if (!sel.getEnabled() || d == null || !d.getEnabled()) {
                i.remove();
            }
        }
        
        return selections;
    }

    /**
     * GET|HEAD /selection/all/
     * GET|HEAD /selection/all/?account={accountId}
     * GET|HEAD /selection/all/?dataset={datasetId}
     * 
     * All list / list for account / list for dataset / list for task.
     * @param accountId account ID.
     * @param datasetId dataset ID.
     * @return list.
     */
    @Secured({"ROLE_USER", "ROLE_CM", "ROLE_ADMIN"})
    @RequestMapping(value = "/all", method = {RequestMethod.GET, RequestMethod.HEAD})
    public @ResponseBody
    List<Selection> listAll(
            @RequestParam(value = "account", required = false) String accountId,
            @RequestParam(value = "dataset", required = false) String datasetId) {
        
        List<Selection> selections = null;
        if (accountId != null) {
            selections = selectionService.findByAccount(accountId);
            logger.info("Returning list of all selections for account " + accountId);
        } else if (datasetId != null) {
            selections = selectionService.findByDataset(datasetId);
            logger.info("Returning list of all selections for dataset " + datasetId);
        } else {
            selections = selectionService.list();
            logger.info("Returning list of all selections");
        }
        
        if (selections == null) {
            logger.info("Returning empty list of selections");
            throw new CustomNotFoundException("No selections found or you don't "
                    + "have permissions to access");
        }
        
        return selections;
    }

    /**
     * GET|HEAD /selection/{id}
     * 
     * Returns an enabled selection.
     * 
     * @param id the selection ID.
     * @param onlyEnabled when true only enabled selections are returned
     * @param ifModifiedSince last mod tag.
     * @return the selection.
     */
    @Secured({"ROLE_USER", "ROLE_CM", "ROLE_ADMIN"})
    @RequestMapping(value = "{id}", method = {RequestMethod.GET, RequestMethod.HEAD})
    public @ResponseBody
    HttpEntity<Selection> get(@PathVariable String id,
            @RequestParam(value = "onlyEnabled", required = false, defaultValue = "false") boolean onlyEnabled,
            @RequestHeader(value="If-Modified-Since", defaultValue="") String ifModifiedSince) {
        
        Selection selection = selectionService.find(id);

        if(selection == null) {
            String message = String.format("Failed to return selection %s. Permission denied or missing selection.", id);
            logger.info(message);
            throw new CustomNotFoundException(message);
        }

        Dataset dataset = datasetService.find(selection.getDataset_id());

        if ((onlyEnabled && !selection.getEnabled()) || dataset == null || !dataset.getEnabled()) {
            logger.info("Failed to return selection " + id + ". Permission denied or missing");
            throw new CustomNotFoundException("A selection with ID " + id 
                    + " doesn't exist, is disabled, or you dont have permissions to access it");
        }
        
        // Check if already newest.
        DateTime reqTime = DateOperations.parseHTTPDate(ifModifiedSince);
        if (reqTime != null && !checkIfModified(selection.getLast_modified(), reqTime)) {
            logger.info("Not returning enabled selection " + id + " since not modified");
            throw new CustomNotModifiedException("This enabled selection has not been modified");
        }
        
        HttpEntity<Selection> entity = new HttpEntity<Selection>(selection, 
                getHTTPHeaderWithCache(selection.getLast_modified()));
        logger.info("Returning enabled selection " + id);
        return entity;
    }

    /**
     * GET|HEAD /selection/lastmodified/{id}
     *
     * Finds a selection's last modified timestamp.
     * @param id the selection ID.
     * @return the timestamp.
     */
    @Secured({"ROLE_USER", "ROLE_CM", "ROLE_ADMIN"})
    @RequestMapping(value = "/lastmodified/{id}", method = {RequestMethod.GET, RequestMethod.HEAD})
    public @ResponseBody
    LastModifiedDate getLastModified(@PathVariable String id) {
        
        Selection selection = selectionService.find(id);
        if (selection == null) {
            logger.info("Failed to return last modified time of selection " + id);
            throw new CustomNotFoundException("A selection with ID " + id 
                    + " doesn't exist or you don't have permissions to access");
        }
        
        logger.info("Returning last modified time of selection " + id);
        return new LastModifiedDate(selection.getLast_modified());
    }

    /**
     * POST /selection/
     * 
     * Adds a selection
     * @param selection the selection.
     * @param result binding.
     * @return the selection with ID assigned.
     */
    @Secured({"ROLE_USER", "ROLE_CM", "ROLE_ADMIN"})
    @RequestMapping(method = RequestMethod.POST)
    public @ResponseBody
    Selection add(@RequestBody @Valid Selection selection, BindingResult result) {
        
        // Selection validation
        if (result.hasErrors()) {
            logger.info("Failed to add selection. Missing fields?");
            throw new CustomBadRequestException(
                    "Selection is invalid. Missing required fields?");
        }
        
        if (selection.getId() != null) {
            logger.info("Failed to add selection. ID set by user.");
            throw new CustomBadRequestException(
                    "The selection you want to add must not have an ID. "
                            + "The ID will be autogenerated");
        }
        
        if (selectionService.findByName(selection.getName()) != null) {
            logger.info("Failed to add selection. Duplicate name.");
            throw new CustomBadRequestException(
                    "An selection with this name already exists. Selection names are unique");
        }
        
        logger.info("Successfully added selection " + selection.getId());
        return selectionService.add(selection);
    }

    /**
     * PUT /selection/{id}
     * 
     * Updates a selection.
     * @param id the selection ID.
     * @param selection the selection.
     * @param result binding.
     */
    @Secured({"ROLE_USER", "ROLE_CM", "ROLE_ADMIN"})
    @RequestMapping(value = "{id}", method = RequestMethod.PUT)
    public @ResponseBody
    void update(@PathVariable String id, @RequestBody @Valid Selection selection,
            BindingResult result) {
        
        // Selection validation
        if (result.hasErrors()) {
            logger.info("Failed to update selection " + id + " . Missing fields?");
            throw new CustomBadRequestException(
                    "Selection is invalid. Missing required fields?");
        }
        
        if (!id.equals(selection.getId())) {
            logger.info("Failed to update selection " + id + ". ID mismatch.");
            throw new CustomBadRequestException(
                    "Selection ID in request URL does not match ID in content body");
        } else if (selectionService.find(id) == null) {
            logger.info("Failed to update selection " + id + ". Duplicate username");
            throw new CustomBadRequestException(
                    "A selection with this ID doesn't exist or you don't have permissions to access it");
        }
        
        logger.info("Successfully updated selection " + id);
        selectionService.update(selection);
    }

    /**
     * DELETE /selection/{id}
     * 
     * Deletes a selection.
     * @param id the selection ID.
     */
    @Secured({"ROLE_USER", "ROLE_CM", "ROLE_ADMIN"})
    @RequestMapping(value = "{id}", method = RequestMethod.DELETE)
    public @ResponseBody
    void delete(@PathVariable String id) {
        selectionService.delete(id);
        logger.info("Successfully deleted selection " + id);
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
        logger.error("Unknown error in selection controller: " + ex.getMessage());
        return new CustomInternalServerErrorResponse(ex.getMessage());
    }
}
