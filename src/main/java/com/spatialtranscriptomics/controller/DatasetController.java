/*
 * Copyright (C) 2012 Spatial Transcriptomics AB
 * Read LICENSE for more information about licensing terms
 * Contact: Jose Fernandez Navarro <jose.fernandez.navarro@scilifelab.se>
 */

package com.spatialtranscriptomics.controller;

import com.spatialtranscriptomics.component.StaticContextAccessor;
import com.spatialtranscriptomics.exceptions.BadRequestResponse;
import com.spatialtranscriptomics.exceptions.CustomBadRequestException;
import com.spatialtranscriptomics.exceptions.CustomNotFoundException;
import com.spatialtranscriptomics.exceptions.NotFoundResponse;
import com.spatialtranscriptomics.model.Dataset;
import com.spatialtranscriptomics.serviceImpl.DatasetInfoServiceImpl;
import com.spatialtranscriptomics.serviceImpl.DatasetServiceImpl;
import com.spatialtranscriptomics.serviceImpl.FeatureServiceImpl;
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
 * This class is Spring MVC controller class for the API endpoint "rest/dataset". It implements the methods available at this endpoint.
 */

@Repository
@Controller
@RequestMapping("/rest/dataset")
public class DatasetController {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger
			.getLogger(DatasetController.class);

	@Autowired
	DatasetServiceImpl datasetService;
        
        @Autowired
	FeatureServiceImpl featureService;
        
        @Autowired
	SelectionServiceImpl selectionService;
        
        @Autowired
	DatasetInfoServiceImpl datasetInfoService;

	// list / list for account
	@Secured({"ROLE_CM","ROLE_USER","ROLE_ADMIN"})
	@RequestMapping(method = RequestMethod.GET)
	public @ResponseBody
	List<Dataset> list(@RequestParam(value = "account", required = false) String accountId) {
		List<Dataset> ds;
                if (accountId != null) {
			ds = datasetService.findByAccount(accountId);
		} else {
                        ds = datasetService.list();
                }
                Iterator<Dataset> i = ds.iterator();
                while (i.hasNext()) {
                   Dataset d = i.next(); // must be called before you can call i.remove()
                   if (!d.getEnabled()) {
                        i.remove();
                   }
                }
                return ds;
	}
        
        // list all / list all for account
	@Secured({"ROLE_CM","ROLE_USER","ROLE_ADMIN"})
	@RequestMapping(value = "/all", method = RequestMethod.GET)
	public @ResponseBody
	List<Dataset> listAll(@RequestParam(value = "account", required = false) String accountId) {
		if (accountId != null) {
			return datasetService.findByAccount(accountId);
		}
		return datasetService.list();
	}

	// get
	@Secured({"ROLE_CM","ROLE_USER","ROLE_ADMIN"})
	@RequestMapping(value = "{id}", method = RequestMethod.GET)
	public @ResponseBody
	Dataset get(@PathVariable String id) {
		Dataset ds = datasetService.find(id);
		if (ds == null || !ds.getEnabled()) {
			throw new CustomNotFoundException("A dataset with this ID does not exist, is disabled, or you dont have permissions to access it.");
		}
		return ds;
	}
        
        // get all
	@Secured({"ROLE_CM","ROLE_USER","ROLE_ADMIN"})
	@RequestMapping(value = "/all/{id}", method = RequestMethod.GET)
	public @ResponseBody
	Dataset getAll(@PathVariable String id) {
		Dataset ds = datasetService.find(id);
		if (ds == null) {
			throw new CustomNotFoundException("A dataset with this ID does not exist, or you dont have permissions to access it.");
		}
		return ds;
	}
        
        // get last modified
	@Secured({"ROLE_CM","ROLE_USER","ROLE_ADMIN"})
	@RequestMapping(value = "/lastmodified/{id}", method = RequestMethod.GET)
	public @ResponseBody
	DateTime getLastModified(@PathVariable String id) {
		Dataset ds = datasetService.find(id);
		if (ds == null) {
			throw new CustomNotFoundException("A dataset with this ID does not exist, or you dont have permissions to access it.");
		}
		return ds.getLast_modified();
	}

	// add
	@Secured({"ROLE_CM","ROLE_ADMIN"})
	@RequestMapping(method = RequestMethod.POST)
	public @ResponseBody
	Dataset add(@RequestBody @Valid Dataset ds, BindingResult result) {
		// Data model validation
		if (result.hasErrors()) {
			throw new CustomBadRequestException("Dataset is invalid. Missing required fields?");
		}
		if (ds.getId() != null) {
			throw new CustomBadRequestException("The dataset you want to add must not have an ID. The ID will be autogenerated.");
		}
		// else if (ds.getName() == null || ds.getName() == "") {
		// throw new CustomBadRequestException(
		// "The dataset must have a value for the property 'name'.");
		// }
		else if (datasetService.findByNameInternal(ds.getName()) != null) {
			throw new CustomBadRequestException("A dataset with this name exists already. Dataset names are unique.");
		}
		
		Dataset d = datasetService.add(ds);
		// HACK: Id needs to be autogenerated at persisting before updating DatasetInfos.
		d.updateGranted_accounts();
		return d;
	}

	// update
	@Secured({"ROLE_CM","ROLE_ADMIN", "ROLE_USER"})
	@RequestMapping(value = "{id}", method = RequestMethod.PUT)
	public @ResponseBody
	void update(@PathVariable String id, @RequestBody @Valid Dataset ds, BindingResult result) {
		// Data model validation
		if (result.hasErrors()) {
			throw new CustomBadRequestException(
					"Dataset is invalid. Missing required fields?");
		}
		if (!id.equals(ds.getId())) {
			throw new CustomBadRequestException("ID in request URL does not match ID in content body.");
		} else if (datasetService.find(id) == null) {
			throw new CustomBadRequestException("A dataset with this ID does not exist or you don't have permissions to access it.");
		} else if (datasetService.findByNameInternal(ds.getName()) != null) {
			if (!datasetService.findByNameInternal(ds.getName()).getId().equals(id)) {
				// dataset with this name and another ID exists
				throw new CustomBadRequestException("Another dataset with this name exists already. Dataset names are unique.");
			}
		}
		datasetService.update(ds);
	}

	// delete
	@Secured({"ROLE_CM","ROLE_ADMIN", "ROLE_USER"})
	@RequestMapping(value = "{id}", method = RequestMethod.DELETE)
	public @ResponseBody
	void delete(@PathVariable String id,
                @RequestParam(value="cascade", required = false, defaultValue = "true") boolean cascade) {
            if (!datasetService.deleteIsOK(id)) {
                throw new CustomBadRequestException("You are not allowed to delete this dataset.");
            }
            if (cascade) {
                featureService.deleteAll(id);
            }
            datasetService.delete(id);
            if (cascade) {
		datasetService.delete(id);
		selectionService.deleteForDataset(id);
		datasetInfoService.deleteForDataset(id);
            }
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
	
	// static access to dataset service
	public static DatasetServiceImpl getStaticDatasetService() {
		DatasetServiceImpl b = StaticContextAccessor.getBean(DatasetController.class).getDatasetService();
		return b;
	}
	
	// access to dataset service
	public DatasetServiceImpl getDatasetService() {
		return this.datasetService;
	}

}
