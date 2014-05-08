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

import com.spatialtranscriptomics.model.Dataset;
import com.spatialtranscriptomics.model.DatasetInfo;
import com.spatialtranscriptomics.model.MongoUserDetails;
import com.spatialtranscriptomics.service.DatasetService;


/**
 * This class implements the store/retrieve logic to MongoDB for the data model class "Dataset".
 * The DB connection is handled in a MongoOperations object, which is configured in mvc-dispather-servlet.xml
 */

@Service
public class DatasetServiceImpl implements DatasetService {

	private static final Logger logger = Logger
			.getLogger(DatasetServiceImpl.class);

	@Autowired
	MongoUserDetailsServiceImpl customUserDetailsService;

	@Autowired
	MongoOperations mongoTemplateAnalysisDB;

	@Autowired
	MongoOperations mongoTemplateUserDB;
	
	public Dataset add(Dataset ds) {
		logger.info("Adding dataset");
		mongoTemplateAnalysisDB.insert(ds);
		return ds;
	}

	
	public Dataset find(String id) {
		MongoUserDetails currentUser = customUserDetailsService.loadCurrentUser();
		if (currentUser.isContentManager() || currentUser.isAdmin() || datasetIsGranted(id, currentUser)) {
			return mongoTemplateAnalysisDB.findOne(new Query(Criteria.where("id").is(id)), Dataset.class);
		}
		return null;
	}

	
	public boolean datasetIsGranted(String id, MongoUserDetails user) {
		List<DatasetInfo> dsis = mongoTemplateUserDB.find(new Query(Criteria.where("dataset_id").is(id)), DatasetInfo.class);
		for (DatasetInfo dsi : dsis) {
			if (dsi.getAccount_id().equals(user.getId())) {
				return true;
			}
		}
		return false;
	}
	
	// required for check to ensure unique dataset names
	public Dataset findByName(String name) {
		return mongoTemplateAnalysisDB.findOne(new Query(Criteria.where("name").is(name)), Dataset.class);
	}

	
	public List<Dataset> list() {
		MongoUserDetails currentUser = customUserDetailsService.loadCurrentUser();
		if (currentUser.isContentManager() || currentUser.isAdmin()) {
			return mongoTemplateAnalysisDB.findAll(Dataset.class);
		}
		return this.findByAccount(currentUser.getId());
	}

	
	public void update(Dataset ds) {
		logger.info("Updating dataset " + ds.getId());
		mongoTemplateAnalysisDB.save(ds);
	}

	
	public void delete(String id) {
		logger.info("Deleting dataset " + id);
		mongoTemplateAnalysisDB.remove(find(id));
	}


	public List<Dataset> findByAccount(String accountId) {
		if (!customUserDetailsService.isProperlyLoaded()) {
			return null;
		}
		
		if (customUserDetailsService == null) { return null; }   // In case of pre-login calls.
		MongoUserDetails currentUser = customUserDetailsService.loadCurrentUser();
		if (currentUser == null) { return null; }                // In case of pre-login calls.
		if (currentUser.isContentManager() || currentUser.isAdmin()) {
			try {
			List<DatasetInfo> dsis = mongoTemplateUserDB.find(new Query(Criteria.where("account_id").is(accountId)), DatasetInfo.class);
			if (dsis == null) { return null; }
			List<String> strs = new ArrayList<String>(dsis.size());
			for (DatasetInfo dsi : dsis) {
				strs.add(dsi.getDataset_id());
			}
			return mongoTemplateAnalysisDB.find(new Query(Criteria.where("id").in(strs)), Dataset.class);
			} catch (Exception e) {
				return null;
			}
		}
		return null;
	}

}
