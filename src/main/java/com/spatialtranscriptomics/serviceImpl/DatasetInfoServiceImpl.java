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

import com.spatialtranscriptomics.model.DatasetInfo;
import com.spatialtranscriptomics.model.MongoUserDetails;
import com.spatialtranscriptomics.service.DatasetInfoService;

/**
 * This class implements the store/retrieve logic to MongoDB for the data model class "DatasetInfo".
 * The DB connection is handled in a MongoOperations object, which is configured in mvc-dispather-servlet.xml
 */
@Service
public class DatasetInfoServiceImpl implements DatasetInfoService {

	private static final Logger logger = Logger.getLogger(DatasetInfoServiceImpl.class);
	
	@Autowired
	MongoUserDetailsServiceImpl customUserDetailsService;
	
	@Autowired
	MongoOperations mongoTemplateUserDB;
	
	// ROLE_ADMIN: all.
	// ROLE_CM:    all.
	// ROLE_USER:  if for own account.
	public DatasetInfo find(String id) {
		DatasetInfo dsi = mongoTemplateUserDB.findOne(new Query(Criteria.where("id").is(id)), DatasetInfo.class);
		MongoUserDetails currentUser = customUserDetailsService.loadCurrentUser();
		if (currentUser.isAdmin() || currentUser.isContentManager() || dsi.getAccount_id().equals(currentUser.getId())) {
			return dsi;
		}
		return null;
	}
	
	// ROLE_ADMIN: all.
	// ROLE_CM:    all.
	// ROLE_USER:  for own account.
	public List<DatasetInfo> findByAccount(String accountId) {
		List<DatasetInfo> dsis = mongoTemplateUserDB.find(new Query(Criteria.where("account_id").is(accountId)), DatasetInfo.class);
		if (dsis == null) { return null; }
		MongoUserDetails currentUser = customUserDetailsService.loadCurrentUser();
		if (currentUser.isAdmin() || currentUser.isContentManager() || accountId.equals(currentUser.getId())) {
			return dsis;
		}
		return null;
	}
	
	// ROLE_ADMIN: all.
	// ROLE_CM:    all.
	// ROLE_USER:  granted datasets.
	public List<DatasetInfo> findByDataset(String datasetId) {
		List<DatasetInfo> dsis = mongoTemplateUserDB.find(new Query(Criteria.where("dataset_id").is(datasetId)), DatasetInfo.class);
		if (dsis == null) { return null; }
		MongoUserDetails currentUser = customUserDetailsService.loadCurrentUser();
		if (currentUser.isAdmin() || currentUser.isContentManager()) {
			return dsis;
		}
		for (int i = dsis.size() - 1; i >= 0; i--) {
			if (!dsis.get(i).getAccount_id().equals(currentUser.getId())) {
				dsis.remove(i);
			}
		}
		return dsis;
	}

	// ROLE_ADMIN: all.
	// ROLE_CM:    all.
	// ROLE_USER:  granted datasets.
	public List<DatasetInfo> list() {
		List<DatasetInfo> dsis = mongoTemplateUserDB.findAll(DatasetInfo.class);
		if (dsis == null) { return null; }
		MongoUserDetails currentUser = customUserDetailsService.loadCurrentUser();
		if (currentUser.isAdmin() || currentUser.isContentManager()) {
			return dsis;
		}
		for (int i = dsis.size() - 1; i >= 0; i--) {
			if (!dsis.get(i).getAccount_id().equals(currentUser.getId())) {
				dsis.remove(i);
			}
		}
		return dsis;
	}

	// ROLE_ADMIN: ok.
	// ROLE_CM:    ok.
	// ROLE_USER:  nope.
	public DatasetInfo add(DatasetInfo dsi) {
		logger.info("Adding datasetinfo");
		mongoTemplateUserDB.insert(dsi);
		return dsi;
	}

	// ROLE_ADMIN: ok.
	// ROLE_CM:    ok.
	// ROLE_USER:  nope.
	public void update(DatasetInfo dsi) {
		logger.info("Updating datasetino " + dsi.getId());
		mongoTemplateUserDB.save(dsi);
	}

	// ROLE_ADMIN: ok.
	// ROLE_CM:    ok.
	// ROLE_USER:  nope.
	public void delete(String id) {
		mongoTemplateUserDB.remove(find(id));
	}

	
	
}
