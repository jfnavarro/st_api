/*
 * Copyright (C) 2012 Spatial Transcriptomics AB
 * Read LICENSE for more information about licensing terms
 * Contact: Jose Fernandez Navarro <jose.fernandez.navarro@scilifelab.se>
 */

package com.spatialtranscriptomics.serviceImpl;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.spatialtranscriptomics.model.DatasetInfo;
import com.spatialtranscriptomics.model.Feature;
import com.spatialtranscriptomics.model.MongoUserDetails;
import com.spatialtranscriptomics.model.Selection;
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

	@Autowired
	MongoOperations mongoTemplateExperimentDB;
	
	@Autowired
	MongoOperations mongoTemplateUserDB;
	
	public boolean datasetIsGranted(String datasetId, MongoUserDetails user) {
		List<DatasetInfo> dsis = mongoTemplateUserDB.find(new Query(Criteria.where("dataset_id").is(datasetId)), DatasetInfo.class);
		for (DatasetInfo dsi : dsis) {
			if (dsi.getAccount_id().equals(user.getId())) {
				return true;
			}
		}
		return false;
	}
	
	
	public List<Feature> findByDatasetId(String datasetId) {
		MongoUserDetails currentUser = customUserDetailsService
				.loadCurrentUser();
		if (currentUser.isContentManager() || currentUser.isAdmin()
				|| datasetIsGranted(datasetId, currentUser)) {
			return mongoTemplateFeatureDB.findAll(Feature.class, datasetId);
		} else {
			return null; // user has no permissions on dataset
		}

	}

	public List<Feature> findByGene(String datasetId, String gene) {
		MongoUserDetails currentUser = customUserDetailsService
				.loadCurrentUser();
		if (currentUser.isContentManager() || currentUser.isAdmin()
				|| datasetIsGranted(datasetId, currentUser)) {
			return mongoTemplateFeatureDB.find(new Query(Criteria.where("gene").is(gene)), Feature.class, datasetId);
		} else {
			return null; // user has no permissions on dataset
		}

	}

	public List<Feature> findBySelectionId(String selectionId) {
		Selection sel = mongoTemplateExperimentDB.findOne(new Query(Criteria.where("id").is(selectionId)), Selection.class);
		if (sel == null || sel.getDataset_id() == null || sel.getFeature_ids() == null) {
			System.out.println("Null!!!");
			return null;
		}
		MongoUserDetails currentUser = customUserDetailsService.loadCurrentUser();
		String dsid = sel.getDataset_id();
		System.out.println("Dataset ID: " + dsid);
		if (currentUser.isContentManager() || currentUser.isAdmin() || datasetIsGranted(dsid, currentUser)) {
			List<String> strs = new ArrayList<String>(sel.getFeature_ids().length);
			for (String fid : sel.getFeature_ids()) {
				strs.add(fid);
			}
			System.out.println("Found " + strs.size() + " items");
			return mongoTemplateFeatureDB.find(new Query(Criteria.where("id").in(strs)), Feature.class, dsid);
		} else {
			System.out.println("Null again");
			return null; // user has no permissions on dataset.
		}

	}
	
	// TODO: Implement if needed.
//	public List<Feature> findBy2DCoords(String datasetId, int x1, int y1, int x2, int y2) {
//		MongoUserDetails currentUser = customUserDetailsService.loadCurrentUser();
//		if (currentUser.isContentManager() || currentUser.isAdmin()
//				|| datasetIsGranted(datasetId, currentUser)) {
//			return null;// TODO implement query
//		} else {
//			return null; // user has no permissions on dataset
//		}
//	}

	public List<Feature> addAll(List<Feature> features, String datasetId) {
		logger.info("Adding features for dataset " + datasetId);
		mongoTemplateFeatureDB.insert(features, datasetId);
		return features;
	}

	
	public void deleteAll(String datasetId) {
		logger.info("Deleting features for dataset " + datasetId);
		mongoTemplateFeatureDB.dropCollection(datasetId);
	}

	
	public List<Feature> findByAnnotation(String datasetId, String annotation) {
		MongoUserDetails currentUser = customUserDetailsService
				.loadCurrentUser();
		if (currentUser.isContentManager() || currentUser.isAdmin()
				|| datasetIsGranted(datasetId, currentUser)) {
			return mongoTemplateFeatureDB.find(new Query(Criteria.where("annotation").is(annotation)), Feature.class, datasetId);
		} else {
			return null; // user has no permissions on dataset
		}
	}

}
