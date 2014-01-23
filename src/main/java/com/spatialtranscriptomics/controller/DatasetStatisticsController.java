/*
 * Copyright (C) 2012 Spatial Transcriptomics AB
 * Read LICENSE for more information about licensing terms
 * Contact: Jose Fernandez Navarro <jose.fernandez.navarro@scilifelab.se>
 */

package com.spatialtranscriptomics.controller;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.spatialtranscriptomics.exceptions.CustomNotFoundException;
import com.spatialtranscriptomics.exceptions.NotFoundResponse;
import com.spatialtranscriptomics.model.Feature;
import com.spatialtranscriptomics.model.DatasetStatistics;
import com.spatialtranscriptomics.serviceImpl.FeatureServiceImpl;

/**
 * This class is Spring MVC controller class for the API endpoint "rest/datasetstatistics".
 * It implements the methods available at this endpoint.
 */

@Repository
@Controller
@RequestMapping("/rest/datasetstatistics")
public class DatasetStatisticsController {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger
			.getLogger(DatasetStatisticsController.class);

	@Autowired
	FeatureServiceImpl featureService;

	// get
	@Secured({"ROLE_CM","ROLE_USER","ROLE_ADMIN"})
	@RequestMapping(method = RequestMethod.GET)
	public @ResponseBody
	DatasetStatistics get(
			@RequestParam(value = "dataset", required = true) String datasetId) {

		List<Feature> features = featureService.findByDatasetId(datasetId);

		if (features.isEmpty()) {
			throw new CustomNotFoundException(
					"Hits do not exist for this dataset or you dont have permissions to access it.");
		}

		return new DatasetStatistics(datasetId, features);

	}

	// // get sum/max/min hits, filtered by datasetId (required)
	// @RequestMapping(method = RequestMethod.GET)
	// public @ResponseBody int getHits(
	// @RequestParam(value="dataset", required=true) String datasetId,
	// @RequestParam(value="number", required=true) String count) {
	//
	// if(count.equals("min")){
	// logger.debug("find hit min: "+datasetId);
	// int result = hitService.getMin(datasetId);
	// if (result >= 0) return result;
	// throw new CustomNotFoundException("No hits information found");
	//
	// }
	// else if(count.equals("max")){
	// logger.debug("find hit max: "+datasetId);
	// int result = hitService.getMax(datasetId);
	// if (result >= 0) return result;
	// throw new CustomNotFoundException("No hits information found");
	// }
	// else if(count.equals("sum")){
	// logger.debug("find hit sum: "+datasetId);
	// int result = hitService.getSum(datasetId);
	// if (result >= 0) return result;
	// throw new CustomNotFoundException("No hits information found");
	// }
	// else{
	// throw new
	// CustomBadRequestException("Value for parameter 'number' must be 'min', 'max' or 'sum'");
	// }
	//
	//
	// }

	@ExceptionHandler(CustomNotFoundException.class)
	@ResponseStatus(value = HttpStatus.NOT_FOUND)
	public @ResponseBody
	NotFoundResponse handleNotFoundException(CustomNotFoundException ex) {
		return new NotFoundResponse(ex.getMessage());
	}

}
