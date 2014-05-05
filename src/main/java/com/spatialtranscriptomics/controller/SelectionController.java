/*
 * Copyright (C) 2012 Spatial Transcriptomics AB
 * Read LICENSE for more information about licensing terms
 * Contact: Jose Fernandez Navarro <jose.fernandez.navarro@scilifelab.se>
 */

package com.spatialtranscriptomics.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.validation.Valid;

import org.apache.log4j.Logger;
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

import com.spatialtranscriptomics.exceptions.BadRequestResponse;
import com.spatialtranscriptomics.exceptions.CustomBadRequestException;
import com.spatialtranscriptomics.exceptions.CustomNotFoundException;
import com.spatialtranscriptomics.exceptions.NotFoundResponse;
import com.spatialtranscriptomics.model.Account;
import com.spatialtranscriptomics.model.Dataset;
import com.spatialtranscriptomics.model.MongoUserDetails;
import com.spatialtranscriptomics.model.Selection;
import com.spatialtranscriptomics.serviceImpl.AccountServiceImpl;
import com.spatialtranscriptomics.serviceImpl.DatasetServiceImpl;
import com.spatialtranscriptomics.serviceImpl.MongoUserDetailsServiceImpl;
import com.spatialtranscriptomics.serviceImpl.SelectionServiceImpl;

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
	MongoUserDetailsServiceImpl customUserDetailsService;
	
	@Autowired
	SelectionServiceImpl selectionService;
	
	@Autowired
	AccountServiceImpl accountService;
	
	@Autowired
	DatasetServiceImpl datasetService;

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
			selections = selectionService.list();
		}
		
		// Filter based on user.
		MongoUserDetails currentUser = customUserDetailsService.loadCurrentUser();
		if (currentUser.isContentManager() || currentUser.isAdmin()) {
		} else {
			List<Dataset> datasets = datasetService.findByAccount(currentUser.getId());
			HashMap<String, Dataset> hash = new HashMap<String, Dataset>(datasets.size());
			for (Dataset d : datasets) {
				hash.put(d.getId(), d);
			}
			ArrayList<Selection> filtered = new ArrayList<Selection>(selections.size());
			for (Selection sel : selections) {
				if (sel.getAccount_id().equals(currentUser.getId()) || hash.containsKey(sel.getDataset_id())) {
					filtered.add(sel);
				}
			}
			selections = filtered;
		}
		if (selections == null) {
			throw new CustomNotFoundException("No selections found or you dont have permissions to access them.");
		}
		return selections;
	}

	// get
	@Secured({"ROLE_USER","ROLE_CM","ROLE_ADMIN"})
	@RequestMapping(value = "{id}", method = RequestMethod.GET)
	public @ResponseBody
	Selection get(@PathVariable String id) {
		Selection selection = selectionService.find(id);
		selection = checkCredentials(selection);
		if (selection == null) {
			throw new CustomNotFoundException(
					"A selection with this ID does not exist or you dont have permissions to access it.");
		}
		return selection;
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
		selection = checkCredentials(selection);
		if (selection != null) {
			return selectionService.add(selection);
		} else {
			throw new CustomBadRequestException(
					"User lacks authorization to add the selection.");
		}
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
		selection = checkCredentials(selection);
		if (selection != null) {
			selectionService.update(selection);
		} else {
			throw new CustomBadRequestException(
					"User lacks authorization to update the selection.");
		}
	}

	// delete
	@Secured({"ROLE_USER","ROLE_CM","ROLE_ADMIN"})
	@RequestMapping(value = "{id}", method = RequestMethod.DELETE)
	public @ResponseBody
	void delete(@PathVariable String id) {
		Selection selection = get(id);
		if (selection != null) {
			selectionService.delete(id);
		} else {
			throw new CustomBadRequestException("User lacks authorization to delete the selection.");
		}
	}
	
	private Selection checkCredentials(Selection sel) {
		MongoUserDetails currentUser = customUserDetailsService.loadCurrentUser();
		if (currentUser.isContentManager() || currentUser.isAdmin()) {
			return sel;
		} else {
			if (currentUser.getId().equals(sel.getAccount_id())) {
				return sel;
			}
			List<Account> accounts = accountService.findByDataset(sel.getDataset_id());
			for (Account acc : accounts) {
				if (currentUser.getId().equals(acc.getId())) {
					return sel;
				}
			}
		}
		return null;
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
