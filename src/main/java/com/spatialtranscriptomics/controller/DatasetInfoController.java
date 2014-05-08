/*
 * Copyright (C) 2012 Spatial Transcriptomics AB
 * Read LICENSE for more information about licensing terms
 * Contact: Jose Fernandez Navarro <jose.fernandez.navarro@scilifelab.se>
 */

package com.spatialtranscriptomics.controller;

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

import com.spatialtranscriptomics.component.StaticContextAccessor;
import com.spatialtranscriptomics.exceptions.BadRequestResponse;
import com.spatialtranscriptomics.exceptions.CustomBadRequestException;
import com.spatialtranscriptomics.exceptions.CustomNotFoundException;
import com.spatialtranscriptomics.exceptions.NotFoundResponse;
import com.spatialtranscriptomics.model.DatasetInfo;
import com.spatialtranscriptomics.serviceImpl.DatasetInfoServiceImpl;

/**
 * This class is Spring MVC controller class for the API endpoint "rest/datasetinfo". It implements the methods available at this endpoint.
 */

@Repository
@Controller
@RequestMapping("/rest/datasetinfo")
public class DatasetInfoController {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(DatasetInfoController.class);

	@Autowired
	DatasetInfoServiceImpl datasetinfoService;

	// list / list by account / list by dataset
	@Secured({"ROLE_CM","ROLE_ADMIN"})
	@RequestMapping(method = RequestMethod.GET)
	public @ResponseBody
	List<DatasetInfo> list(
			@RequestParam(value = "account", required = false) String accountId,
			@RequestParam(value = "dataset", required = false) String datasetId
			) {
		List<DatasetInfo> datasetinfos;
		if (accountId != null) {
			datasetinfos = datasetinfoService.findByAccount(accountId);
		} else if (datasetId != null) {
			datasetinfos = datasetinfoService.findByDataset(datasetId);
		} else {
			datasetinfos = datasetinfoService.list();
		}
		if (datasetinfos == null) {
			throw new CustomNotFoundException("No DatasetInfos found or you dont have permissions to access them.");
		}
		return datasetinfos;
	}
	

	// get
	@Secured({"ROLE_CM", "ROLE_ADMIN"})
	@RequestMapping(value = "{id}", method = RequestMethod.GET)
	public @ResponseBody
	DatasetInfo get(@PathVariable String id) {
		DatasetInfo datasetinfo = datasetinfoService.find(id);
		if (datasetinfo == null) {
			throw new CustomNotFoundException(
					"A DatasetInfo with this ID does not exist or you dont have permissions to access it.");
		}
		return datasetinfo;
	}

	
	// add
	@Secured({"ROLE_CM","ROLE_ADMIN"})
	@RequestMapping(method = RequestMethod.POST)
	public @ResponseBody
	DatasetInfo add(@RequestBody @Valid DatasetInfo datasetinfo, BindingResult result) {
		// Data model validation
		if (result.hasErrors()) {
			throw new CustomBadRequestException(
					"DatasetInfo is invalid. Missing required fields?");
		}
		if (datasetinfo.getId() != null) {
			throw new CustomBadRequestException(
					"The DatasetInfo you want to add must not have an ID. The ID will be autogenerated.");
		}
		return datasetinfoService.add(datasetinfo);
	}

	
	// update
	@Secured({"ROLE_CM","ROLE_ADMIN"})
	@RequestMapping(value = "{id}", method = RequestMethod.PUT)
	public @ResponseBody
	void update(@PathVariable String id, @RequestBody @Valid DatasetInfo datasetinfo,
			BindingResult result) {
		// Data model validation
		if (result.hasErrors()) {
			// TODO send error messages here
			throw new CustomBadRequestException(
					"DatasetInfo is invalid. Missing required fields?");
		}
		if (!id.equals(datasetinfo.getId())) {
			throw new CustomBadRequestException(
					"DatasetInfo ID in request URL does not match ID in content body.");
		} else if (datasetinfoService.find(id) == null) {
			throw new CustomBadRequestException(
					"A DatasetInfo with this ID does not exist or you don't have permissions to access it.");
		} else {
			datasetinfoService.update(datasetinfo);
		}
	}

	// delete
	@Secured({"ROLE_CM","ROLE_ADMIN"})
	@RequestMapping(value = "{id}", method = RequestMethod.DELETE)
	public @ResponseBody
	void delete(@PathVariable String id) {
		datasetinfoService.delete(id);
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

	public static DatasetInfoServiceImpl getStaticDatasetInfoService() {
		return StaticContextAccessor.getBean(DatasetInfoController.class).getDatasetInfoService();
	}
	
	public DatasetInfoServiceImpl getDatasetInfoService() {
		return this.datasetinfoService;
	}
	
}
