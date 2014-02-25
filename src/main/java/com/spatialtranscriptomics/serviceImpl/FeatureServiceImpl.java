/*
 * Copyright (C) 2012 Spatial Transcriptomics AB
 * Read LICENSE for more information about licensing terms
 * Contact: Jose Fernandez Navarro <jose.fernandez.navarro@scilifelab.se>
 */

package com.spatialtranscriptomics.serviceImpl;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.spatialtranscriptomics.model.Feature;
import com.spatialtranscriptomics.model.MongoUserDetails;
import com.spatialtranscriptomics.service.FeatureService;

/**
 * This class implements the store/retrieve logic to MongoDB for the data model class "Feature".
 * The DB connection is handled in a MongoOperations object, which is configured in mvc-dispatcher-servlet.xml
 */

@Service
public class FeatureServiceImpl implements FeatureService {

	private static final Logger logger = Logger
			.getLogger(FeatureServiceImpl.class);

	@Autowired
	MongoUserDetailsServiceImpl customUserDetailsService;

	@Autowired
	MongoOperations mongoTemplateFeatureDB;

	public List<Feature> findByDatasetId(String datasetId) {
		MongoUserDetails currentUser = customUserDetailsService
				.loadCurrentUser();
		if (currentUser.isContentManager() || currentUser.isAdmin()
				|| currentUser.getGrantedDatasets().contains(datasetId)) {
			return mongoTemplateFeatureDB.findAll(Feature.class, datasetId);
		} else {
			return null; // user has no permissions on dataset
		}

	}

	public List<Feature> findByGene(String datasetId, String gene) {
		MongoUserDetails currentUser = customUserDetailsService
				.loadCurrentUser();
		if (currentUser.isContentManager() || currentUser.isAdmin()
				|| currentUser.getGrantedDatasets().contains(datasetId)) {
			return mongoTemplateFeatureDB.find(new Query(Criteria.where("gene").is(gene)), Feature.class, datasetId);
		} else {
			return null; // user has no permissions on dataset
		}

	}

	public List<Feature> findBy2DCoords(String datasetId, int x1, int y1,
			int x2, int y2) {

		MongoUserDetails currentUser = customUserDetailsService
				.loadCurrentUser();
		if (currentUser.isContentManager() || currentUser.isAdmin()
				|| currentUser.getGrantedDatasets().contains(datasetId)) {
			return null;// TODO implement query
		} else {
			return null; // user has no permissions on dataset
		}
	}

	public List<Feature> addAll(List<Feature> features, String datasetId) {
		logger.debug("Adding features for dataset " + datasetId);
		mongoTemplateFeatureDB.insert(features, datasetId);
		return features;
	}

	
	public void deleteAll(String datasetId) {
		logger.debug("Deleting features for dataset " + datasetId);
		mongoTemplateFeatureDB.dropCollection(datasetId);
	}

	
	public List<Feature> findByAnnotation(String datasetId, String annotation) {
		MongoUserDetails currentUser = customUserDetailsService
				.loadCurrentUser();
		if (currentUser.isContentManager() || currentUser.isAdmin()
				|| currentUser.getGrantedDatasets().contains(datasetId)) {
			return mongoTemplateFeatureDB.find(new Query(Criteria.where("annotation").is(annotation)), Feature.class, datasetId);
		} else {
			return null; // user has no permissions on dataset
		}
	}

}
