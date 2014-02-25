/*
 * Copyright (C) 2012 Spatial Transcriptomics AB
 * Read LICENSE for more information about licensing terms
 * Contact: Jose Fernandez Navarro <jose.fernandez.navarro@scilifelab.se>
 */

package com.spatialtranscriptomics.serviceImpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.stereotype.Service;

import com.spatialtranscriptomics.model.Gene;
import com.spatialtranscriptomics.service.GeneService;

/**
 * This class implements the store/retrieve logic to MongoDB for the data model class "Gene".
 * The DB connection is handled in a MongoOperations object, which is configured in mvc-dispather-servlet.xml
 */


@Service
public class GeneServiceImpl implements GeneService {

	@Autowired
	MongoOperations mongoTemplateFeatureDB;

	public List<Gene> findByDatasetId(String datasetId) {
		return mongoTemplateFeatureDB.findAll(Gene.class, datasetId);
	}

}
