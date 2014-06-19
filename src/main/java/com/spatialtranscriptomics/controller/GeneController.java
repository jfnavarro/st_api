/*
 * Copyright (C) 2012 Spatial Transcriptomics AB
 * Read LICENSE for more information about licensing terms
 * Contact: Jose Fernandez Navarro <jose.fernandez.navarro@scilifelab.se>
 */

package com.spatialtranscriptomics.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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
import com.spatialtranscriptomics.model.Gene;
import com.spatialtranscriptomics.serviceImpl.GeneServiceImpl;


/**
 * This class is Spring MVC controller class for the API endpoint "rest/gene". It implements the methods available at this endpoint.
 */

@Repository
@Controller
@RequestMapping("/rest/gene")
public class GeneController {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(GeneController.class);

	@Autowired
	GeneServiceImpl geneService;

	// list, filtered by datasetId (required)
	@Secured({"ROLE_CM","ROLE_USER","ROLE_ADMIN"})
	@RequestMapping(method = RequestMethod.GET)
	public @ResponseBody
	List<Gene> list(
			@RequestParam(value = "dataset", required = true) String datasetId) {

		// this logic should be replaced by a "distinct" mongo query
		List<Gene> genes = geneService.findByDatasetId(datasetId);

		if (genes.isEmpty()) {
			throw new CustomNotFoundException(
					"Genes do not exist for this dataset or you dont have permissions to access it.");
		}

		List<Gene> uniqueGenes = new ArrayList<Gene>();
		HashMap<String, Gene> hm = new HashMap<String, Gene>();

		for (Gene g : genes) {
			hm.put(g.getGene(), g);
		}

		Iterator<Entry<String, Gene>> it = hm.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, Gene> pairs = it.next();
			uniqueGenes.add(pairs.getValue());
		}

		return uniqueGenes;

	}

	@ExceptionHandler(CustomNotFoundException.class)
	@ResponseStatus(value = HttpStatus.NOT_FOUND)
	public @ResponseBody
	NotFoundResponse handleNotFoundException(CustomNotFoundException ex) {
		return new NotFoundResponse(ex.getMessage());
	}

}
