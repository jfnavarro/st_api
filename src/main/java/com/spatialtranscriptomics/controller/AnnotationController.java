///*
// * Copyright (C) 2012 Spatial Transcriptomics AB
// * Read LICENSE for more information about licensing terms
// * Contact: Jose Fernandez Navarro <jose.fernandez.navarro@scilifelab.se>
// */
//
//package com.spatialtranscriptomics.controller;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.Iterator;
//import java.util.List;
//import java.util.Map;
//import java.util.Map.Entry;
//
//import org.apache.log4j.Logger;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.security.access.annotation.Secured;
//import org.springframework.stereotype.Controller;
//import org.springframework.stereotype.Repository;
//import org.springframework.web.bind.annotation.ExceptionHandler;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestMethod;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.ResponseBody;
//import org.springframework.web.bind.annotation.ResponseStatus;
//
//import com.spatialtranscriptomics.exceptions.CustomNotFoundException;
//import com.spatialtranscriptomics.exceptions.NotFoundResponse;
//import com.spatialtranscriptomics.model.Annotation;
//import com.spatialtranscriptomics.serviceImpl.AnnotationServiceImpl;
//
//
///**
// * This class is Spring MVC controller class for the API endpoint "rest/annotation". It implements the methods available at this endpoint.
// */
//
//@Repository
//@Controller
//@RequestMapping("/rest/annotation")
//public class AnnotationController {
//
//	 @SuppressWarnings("unused")
//	private static final Logger logger =
//	 Logger.getLogger(AnnotationController.class);
//
//	@Autowired
//	AnnotationServiceImpl annotationService;
//	
//	// list, filtered by datasetId (required)
//	@Secured({"ROLE_CM","ROLE_USER","ROLE_ADMIN"})
//	@RequestMapping(method = RequestMethod.GET)
//	public @ResponseBody
//	List<Annotation> list(
//			@RequestParam(value = "dataset", required = true) String datasetId) {
//
//		// this logic should be replaced by a "distinct" mongo query
//		List<Annotation> annotations = annotationService.findByDatasetId(datasetId);
//
//		if (annotations.isEmpty()) {
//			throw new CustomNotFoundException(
//					"Annotations do not exist for this dataset or you dont have permissions to access it.");
//		}
//
//		List<Annotation> uniqueAnnotations = new ArrayList<Annotation>();
//		HashMap<String, Annotation> hm = new HashMap<String, Annotation>();
//
//		for (Annotation g : annotations) {
//			hm.put(g.getAnnotation(), g);
//		}
//
//		Iterator<Entry<String, Annotation>> it = hm.entrySet().iterator();
//		while (it.hasNext()) {
//			Map.Entry<String, Annotation> pairs = it.next();
//			uniqueAnnotations.add(pairs.getValue());
//		}
//
//		return uniqueAnnotations;
//
//	}
//
//	@ExceptionHandler(CustomNotFoundException.class)
//	@ResponseStatus(value = HttpStatus.NOT_FOUND)
//	public @ResponseBody
//	NotFoundResponse handleNotFoundException(CustomNotFoundException ex) {
//		return new NotFoundResponse(ex.getMessage());
//	}
//
//}
