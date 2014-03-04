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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.spatialtranscriptomics.exceptions.BadRequestResponse;
import com.spatialtranscriptomics.exceptions.CustomBadRequestException;
import com.spatialtranscriptomics.exceptions.CustomNotFoundException;
import com.spatialtranscriptomics.model.Feature;
import com.spatialtranscriptomics.serviceImpl.FeatureServiceImpl;


/**
 * This class is Spring MVC controller class for the API endpoint "rest/feature". It implements the methods available at this endpoint.
 */

@Repository
@Controller
@RequestMapping("/rest/feature")
public class FeatureController {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger
			.getLogger(FeatureController.class);

	@Autowired
	FeatureServiceImpl featureService;

	// list, filtered by name (required), gene(optional)
	@Secured({"ROLE_CM","ROLE_USER","ROLE_ADMIN"})
	@RequestMapping(method = RequestMethod.GET)
	public @ResponseBody
	List<Feature> list(
			@RequestParam(value = "dataset", required = true) String datasetId,
			@RequestParam(value = "gene", required = false) String gene,
			@RequestParam(value = "annotation", required = false) String annotation)
//			@RequestParam(value = "x1", required = false) Integer x1,
//			@RequestParam(value = "y1", required = false) Integer y1,
//			@RequestParam(value = "x2", required = false) Integer x2,
//			@RequestParam(value = "y2", required = false) Integer y2)
	{

		if (annotation != null) {
			return featureService.findByAnnotation(datasetId, annotation);
		} else if (gene != null) {
			return featureService.findByGene(datasetId, gene);
		}
//		else if ((x1 != null) || (x2 != null) || (y1 != null) || (y2 != null)) {
//			List<Integer> coords = Arrays.asList(x1, y1, x2, y2);
//			if (coords.contains(null)) {
//				throw new CustomBadRequestException("One or more coordinates missing.");
//			}
//			return featureService.findBy2DCoords(datasetId, x1, y1, x2, y2);
//		}
		else {
			return featureService.findByDatasetId(datasetId);
		}
	}
	
//	// list
//	@Secured({"ROLE_CM","ROLE_USER","ROLE_ADMIN"})
//	@RequestMapping(method = RequestMethod.GET)
//	public @ResponseBody
//	List<Feature> list(
//			@RequestParam(value = "dataset", required = true) String datasetId) {
//		return featureService.findByDatasetId(datasetId);
//	}
//	
//	
//	// list by gene
//	@Secured({"ROLE_CM","ROLE_USER","ROLE_ADMIN"})
//	@RequestMapping(method = RequestMethod.GET)
//	public @ResponseBody
//	List<Feature> listByGene(
//			@RequestParam(value = "dataset", required = true) String datasetId,
//			@RequestParam(value = "gene", required = true) String gene) {
//		return featureService.findByGene(datasetId, gene);
//	}
//	
//	// list by annotation
//	@Secured({"ROLE_CM","ROLE_USER","ROLE_ADMIN"})
//	@RequestMapping(method = RequestMethod.GET)
//	public @ResponseBody
//	List<Feature> listByAnnotation(
//			@RequestParam(value = "dataset", required = true) String datasetId,
//			@RequestParam(value = "annotation", required = true) String annotation) {
//		return featureService.findByAnnotation(datasetId, annotation);
//	}
//
//	// list by coordinates
//	@Secured({"ROLE_CM","ROLE_USER","ROLE_ADMIN"})
//	@RequestMapping(method = RequestMethod.GET)
//	public @ResponseBody
//	List<Feature> listByCoords(
//			@RequestParam(value = "dataset", required = true) String datasetId,
//			@RequestParam(value = "x1", required = true) Integer x1,
//			@RequestParam(value = "y1", required = true) Integer y1,
//			@RequestParam(value = "x2", required = true) Integer x2,
//			@RequestParam(value = "y2", required = true) Integer y2) {
//		List<Integer> coords = Arrays.asList(x1, y1, x2, y2);
//		if (coords.contains(null)) {
//			throw new CustomBadRequestException("One or more coordinates missing.");
//		}
//		return featureService.findBy2DCoords(datasetId, x1, y1, x2, y2);
//	}

	// add all
	@Secured({"ROLE_CM","ROLE_ADMIN"})
	@RequestMapping(method = RequestMethod.POST)
	public @ResponseBody
	List<Feature> addAll(@RequestBody List<Feature> features,
			@RequestParam(value = "dataset", required = true) String datasetId) {
		for (Feature f : features) {
			if (f.getId() != null) {
				throw new CustomNotFoundException(
						"The features you want to add must not have IDs. The IDs will be autogenerated.");
			}
		}
		return featureService.addAll(features, datasetId);
	}

	// delete all
	@Secured({"ROLE_CM","ROLE_ADMIN"})
	@RequestMapping(method = RequestMethod.DELETE)
	public @ResponseBody
	void deleteAll(
			@RequestParam(value = "dataset", required = true) String datasetId) {
		featureService.deleteAll(datasetId);
	}

	@ExceptionHandler(CustomBadRequestException.class)
	@ResponseStatus(value = HttpStatus.BAD_REQUEST)
	public @ResponseBody
	BadRequestResponse handleNotFoundException(CustomBadRequestException ex) {
		return new BadRequestResponse(ex.getMessage());
	}

}
