/*
 * Copyright (C) 2012 Spatial Transcriptomics AB
 * Read LICENSE for more information about licensing terms
 * Contact: Jose Fernandez Navarro <jose.fernandez.navarro@scilifelab.se>
 */

package com.spatialtranscriptomics.controller;

import com.spatialtranscriptomics.component.StartupHousekeeper;
import com.spatialtranscriptomics.exceptions.BadRequestResponse;
import com.spatialtranscriptomics.exceptions.CustomBadRequestException;
import com.spatialtranscriptomics.exceptions.CustomNotFoundException;
import com.spatialtranscriptomics.exceptions.NotFoundResponse;
import com.spatialtranscriptomics.model.Dataset;
import com.spatialtranscriptomics.model.LastModifiedDate;
import com.spatialtranscriptomics.model.MongoUserDetails;
import com.spatialtranscriptomics.model.Selection;
import com.spatialtranscriptomics.serviceImpl.DatasetServiceImpl;
import com.spatialtranscriptomics.serviceImpl.MongoUserDetailsServiceImpl;
import com.spatialtranscriptomics.serviceImpl.SelectionServiceImpl;
import java.util.Iterator;
import java.util.List;
import javax.validation.Valid;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * This class is Spring MVC controller class for the API endpoint "rest/selection". It implements the methods available at this endpoint.
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
        
	// list / list for account / list for dataset
	@Secured({"ROLE_USER","ROLE_CM","ROLE_ADMIN"})
	@RequestMapping(method = RequestMethod.GET)
	public @ResponseBody
	List<Selection> list(
			@RequestParam(value = "account", required = false) String accountId,
			@RequestParam(value = "dataset", required = false) String datasetId,
			@RequestParam(value = "task", required = false) String taskId
			) {
		List<Selection> selections = null;
		if (accountId != null) {
			selections = selectionService.findByAccount(accountId);
		} else if (datasetId != null) {
			selections = selectionService.findByDataset(datasetId);
		} else if (taskId != null) {
			selections = selectionService.findByTask(taskId);
		} else {
                    // NOTE: Only current user's selections, even for admin.
                    MongoUserDetails currentUser = customUserDetailsService.loadCurrentUser();
                    selections = selectionService.findByAccount(currentUser.getId());
		}
		if (selections == null) {
                    throw new CustomNotFoundException("No selections found or you dont have permissions to access them.");
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
        
        // list all / list all for account / list all for dataset
	@Secured({"ROLE_USER","ROLE_CM","ROLE_ADMIN"})
	@RequestMapping(value = "/all", method = RequestMethod.GET)
	public @ResponseBody
	List<Selection> listAll(
			@RequestParam(value = "account", required = false) String accountId,
			@RequestParam(value = "dataset", required = false) String datasetId,
			@RequestParam(value = "task", required = false) String taskId
			) {
            List<Selection> selections = null;
            if (accountId != null) {
                    selections = selectionService.findByAccount(accountId);
            } else if (datasetId != null) {
                    selections = selectionService.findByDataset(datasetId);
            } else if (taskId != null) {
                    selections = selectionService.findByTask(taskId);
            } else {
                    selections = selectionService.list();
            }
            if (selections == null) {
                    throw new CustomNotFoundException("No selections found or you dont have permissions to access them.");
            }
            return selections;
	}


	// get
	@Secured({"ROLE_USER","ROLE_CM","ROLE_ADMIN"})
	@RequestMapping(value = "/all/{id}", method = RequestMethod.GET)
	public @ResponseBody
	Selection get(@PathVariable String id) {
		Selection selection = selectionService.find(id);
                Dataset d = datasetService.find(selection.getDataset_id());
		if (selection == null || !selection.getEnabled() || d == null || !d.getEnabled()) {
                    throw new CustomNotFoundException("A selection with this ID does not exist, is disabled, or you dont have permissions to access it.");
		}
		return selection;
	}
        
        // get all
	@Secured({"ROLE_USER","ROLE_CM","ROLE_ADMIN"})
	@RequestMapping(value = "{id}", method = RequestMethod.GET)
	public @ResponseBody
	Selection getAll(@PathVariable String id) {
		Selection selection = selectionService.find(id);
		if (selection == null) {
			throw new CustomNotFoundException("A selection with this ID does not exist or you dont have permissions to access it.");
		}
		return selection;
	}
        
         // get last modified
	@Secured({"ROLE_USER","ROLE_CM","ROLE_ADMIN"})
	@RequestMapping(value = "/lastmodified/{id}", method = RequestMethod.GET)
	public @ResponseBody
	LastModifiedDate getLastModified(@PathVariable String id) {
		Selection selection = selectionService.find(id);
		if (selection == null) {
                    throw new CustomNotFoundException("A selection with this ID does not exist or you dont have permissions to access it.");
		}
                return new LastModifiedDate(selection.getLast_modified());
	}

	// add
	@Secured({"ROLE_USER","ROLE_CM","ROLE_ADMIN"})
	@RequestMapping(method = RequestMethod.POST)
	public @ResponseBody
	Selection add(@RequestBody @Valid Selection selection, BindingResult result) {
		// Selection validation
		if (result.hasErrors()) {
			// TODO send error messages here
			throw new CustomBadRequestException(
					"Selection is invalid. Missing required fields?");
		}
		if (selection.getId() != null) {
			throw new CustomBadRequestException(
					"The selection you want to add must not have an ID. The ID will be autogenerated.");
		}
		if (selectionService.findByName(selection.getName()) != null) {
			throw new CustomBadRequestException(
					"An selection with this name already exists. Selection names are unique.");
		}
		return selectionService.add(selection);
	}

	// update
	@Secured({"ROLE_USER","ROLE_CM","ROLE_ADMIN"})
	@RequestMapping(value = "{id}", method = RequestMethod.PUT)
	public @ResponseBody
	void update(@PathVariable String id, @RequestBody @Valid Selection selection,
			BindingResult result) {
		// Selection validation
		if (result.hasErrors()) {
			// TODO send error messages here
			throw new CustomBadRequestException(
					"Selection is invalid. Missing required fields?");
		}
		if (!id.equals(selection.getId())) {
			throw new CustomBadRequestException(
					"Selection ID in request URL does not match ID in content body.");
		} else if (selectionService.find(id) == null) {
			throw new CustomBadRequestException(
					"A selection with this ID does not exist or you don't have permissions to access it.");
		}
		selectionService.update(selection);
	}

	// delete
	@Secured({"ROLE_USER","ROLE_CM","ROLE_ADMIN"})
	@RequestMapping(value = "{id}", method = RequestMethod.DELETE)
	public @ResponseBody
	void delete(@PathVariable String id) {
            selectionService.delete(id);
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
	BadRequestResponse handleNotFoundException(CustomBadRequestException ex) {
		return new BadRequestResponse(ex.getMessage());
	}

}
