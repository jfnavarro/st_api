/*
 * Copyright (C) 2012 Spatial Transcriptomics AB
 * Read LICENSE for more information about licensing terms
 * Contact: Jose Fernandez Navarro <jose.fernandez.navarro@scilifelab.se>
 */

package com.spatialtranscriptomics.serviceImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.spatialtranscriptomics.model.Account;
import com.spatialtranscriptomics.model.Dataset;
import com.spatialtranscriptomics.model.MongoUserDetails;
import com.spatialtranscriptomics.model.Selection;
import com.spatialtranscriptomics.model.Task;
import com.spatialtranscriptomics.service.SelectionService;

/**
 * This class implements the store/retrieve logic to MongoDB for the data model class "Selection".
 * The DB connection is handled in a MongoOperations object, which is configured in mvc-dispather-servlet.xml
 */

@Service
public class SelectionServiceImpl implements SelectionService {

	private static final Logger logger = Logger
			.getLogger(SelectionServiceImpl.class);

	@Autowired
	MongoUserDetailsServiceImpl customUserDetailsService;

	@Autowired
	MongoOperations mongoTemplateExperimentDB;
	
	@Autowired
	AccountServiceImpl accountService;
	
	@Autowired
	DatasetServiceImpl datasetService;

	// ROLE_ADMIN: all.
	// ROLE_CM:    own account / dataset.
	// ROLE_USER:  own account / dataset.
	public Selection find(String id) {
		Selection selection = mongoTemplateExperimentDB.findOne(new Query(Criteria.where("id").is(id)), Selection.class);
		return checkCredentials(selection);
	}

	// ROLE_ADMIN: all.
	// ROLE_CM:    own account / dataset.
	// ROLE_USER:  own account / dataset.
	public Selection findByName(String name) {
		Selection sel = mongoTemplateExperimentDB.findOne(new Query(Criteria.where("name").is(name)), Selection.class);
		return checkCredentials(sel);
	}

	// ROLE_ADMIN: all.
	// ROLE_CM:    own account / dataset.
	// ROLE_USER:  own account / dataset.
	public List<Selection> list() {
		List<Selection> selections = mongoTemplateExperimentDB.findAll(Selection.class);		
		// Filter based on user.
		MongoUserDetails currentUser = customUserDetailsService.loadCurrentUser();
		if (currentUser.isAdmin()) {
		} else {
			List<Dataset> datasets = datasetService.findByAccount(currentUser.getId());
			HashMap<String, Dataset> hash = new HashMap<String, Dataset>(datasets.size());
			for (Dataset d : datasets) {
				hash.put(d.getId(), d);
			}
			ArrayList<Selection> filtered = new ArrayList<Selection>(selections.size());
			for (Selection sel : selections) {
				if (sel.getAccount_id().equals(currentUser.getId()) || hash.containsKey(sel.getDataset_id())) {
					filtered.add(sel);
				}
			}
			selections = filtered;
		}
		return selections;
	}

	// ROLE_ADMIN: all.
	// ROLE_CM:    own account / dataset.
	// ROLE_USER:  own account / dataset.
	public Selection add(Selection selection) {
		selection = checkCredentials(selection);
		if (selection != null) {
			logger.info("Adding Selection");
			mongoTemplateExperimentDB.insert(selection);
		}
		return selection;
	}

	// ROLE_ADMIN: all.
	// ROLE_CM:    own account / dataset.
	// ROLE_USER:  own account / dataset.
	public void update(Selection selection) {
		selection = checkCredentials(selection);
		if (selection != null) {
			logger.info("Updating Selection " + selection.getId());
			mongoTemplateExperimentDB.save(selection);
		}
	}

	// ROLE_ADMIN: all.
	// ROLE_CM:    own account / dataset.
	// ROLE_USER:  own account / dataset.
	public void delete(String id) {
		Selection selection = checkCredentials(find(id));
		if (selection != null) {
			logger.info("Deleing Selection " + id);
			mongoTemplateExperimentDB.remove(selection);
		}
	}

	// ROLE_ADMIN: all.
	// ROLE_CM:    own account / dataset.
	// ROLE_USER:  own account / dataset.
	public List<Selection> findByAccount(String accountId) {
		MongoUserDetails currentUser = customUserDetailsService.loadCurrentUser();
		if (currentUser.isAdmin() || currentUser.getId().equals(accountId)) {
			return mongoTemplateExperimentDB.find(new Query(Criteria.where("account_id").is(accountId)), Selection.class);
		}
		return null;
	}

	// ROLE_ADMIN: all.
	// ROLE_CM:    own account / dataset.
	// ROLE_USER:  own account / dataset.
	public List<Selection> findByDataset(String datasetId) {
		MongoUserDetails currentUser = customUserDetailsService.loadCurrentUser();
		if (currentUser.isAdmin()) {
			return mongoTemplateExperimentDB.find(new Query(Criteria.where("dataset_id").is(datasetId)), Selection.class);
		}
		List<Dataset> datasets = datasetService.findByAccount(currentUser.getId());
		for (Dataset d : datasets) {
			if (d.getId().equals(datasetId)) {
				return mongoTemplateExperimentDB.find(new Query(Criteria.where("dataset_id").is(datasetId)), Selection.class);
			}
		}
		return null;
	}
	
	// ROLE_ADMIN: all.
	// ROLE_CM:    own account / dataset.
	// ROLE_USER:  own account / dataset.
	public List<Selection> findByTask(String taskId) {
		Task t = mongoTemplateExperimentDB.findOne(new Query(Criteria.where("id").is(taskId)), Task.class);
		if (t == null) { return null; }
		String[] selids = t.getSelection_ids();
		if (selids == null) { return null; }
		ArrayList<String> ls = new ArrayList<String>(selids.length);
		for (String id : selids) {
			ls.add(id);
		}
		return mongoTemplateExperimentDB.find(new Query(Criteria.where("id").in(ls)), Selection.class);
	}
	
	private Selection checkCredentials(Selection sel) {
		MongoUserDetails currentUser = customUserDetailsService.loadCurrentUser();
		if (currentUser.isAdmin() || sel == null) {
			return sel;
		} else {
			if (currentUser.getId().equals(sel.getAccount_id())) {
				return sel;
			}
			List<Account> accounts = accountService.findByDataset(sel.getDataset_id());
			for (Account acc : accounts) {
				if (currentUser.getId().equals(acc.getId())) {
					return sel;
				}
			}
		}
		return null;
	}

}
