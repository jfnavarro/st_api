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

import com.spatialtranscriptomics.model.Hit;
import com.spatialtranscriptomics.service.HitService;

/**
 * This class implements the store/retrieve logic to MongoDB for the data model class "Hit".
 * The DB connection is handled in a MongoOperations object, which is configured in mvc-dispatcher-servlet.xml
 */

@Service
public class HitServiceImpl implements HitService {

	/** This refers to the Feature DB. */
	@Autowired
	MongoOperations mongoTemplateFeature;

	public List<Hit> findByDatasetId(String datasetId) {

		return mongoTemplateFeature.findAll(Hit.class, datasetId);
	}

}
