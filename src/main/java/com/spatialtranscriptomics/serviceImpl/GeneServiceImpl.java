///*
// * Copyright (C) 2012 Spatial Transcriptomics AB
// * Read LICENSE for more information about licensing terms
// * Contact: Jose Fernandez Navarro <jose.fernandez.navarro@scilifelab.se>
// */
//
//package com.spatialtranscriptomics.serviceImpl;
//
//import java.util.List;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.mongodb.core.MongoOperations;
//import org.springframework.data.mongodb.core.query.Criteria;
//import org.springframework.data.mongodb.core.query.Query;
//import org.springframework.stereotype.Service;
//
//import com.spatialtranscriptomics.model.DatasetInfo;
//import com.spatialtranscriptomics.model.Gene;
//import com.spatialtranscriptomics.model.MongoUserDetails;
//import com.spatialtranscriptomics.service.GeneService;
//
///**
// * This class implements the store/retrieve logic to MongoDB for the data model class "Gene".
// * The DB connection is handled in a MongoOperations object, which is configured in mvc-dispather-servlet.xml
// */
//
//
//@Service
//public class GeneServiceImpl implements GeneService {
//
//	@Autowired
//	MongoOperations mongoTemplateFeatureDB;
//
//	@Autowired
//	MongoOperations mongoTemplateUserDB;
//	
//	@Autowired
//	MongoUserDetailsServiceImpl customUserDetailsService;
//	
//	public boolean datasetIsGranted(String datasetId, MongoUserDetails user) {
//		List<DatasetInfo> dsis = mongoTemplateUserDB.find(new Query(Criteria.where("dataset_id").is(datasetId).and("account_id").is(user.getId())), DatasetInfo.class);
//		return (dsis != null && dsis.size() > 0);
//	}
//	
//	// ROLE_ADMIN: all.
//	// ROLE_CM:    all.
//	// ROLE_USER:  granted datasets.
//	public List<Gene> findByDatasetId(String datasetId) {
//		MongoUserDetails currentUser = customUserDetailsService.loadCurrentUser();
//		if (currentUser.isContentManager() || currentUser.isAdmin() || datasetIsGranted(datasetId, currentUser)) {
//			return mongoTemplateFeatureDB.findAll(Gene.class, datasetId);
//		}
//		return null;
//	}
//
//}
