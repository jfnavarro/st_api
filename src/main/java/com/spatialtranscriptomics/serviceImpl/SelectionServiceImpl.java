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

import com.spatialtranscriptomics.model.Selection;
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

	public Selection find(String id) {
		return mongoTemplateExperimentDB.findOne(new Query(Criteria.where("id").is(id)), Selection.class);
	}

	public Selection findByName(String name) {
		return mongoTemplateExperimentDB.findOne(new Query(Criteria.where("name").is(name)), Selection.class);
	}

	public List<Selection> list() {
		return mongoTemplateExperimentDB.findAll(Selection.class);
	}

	public Selection add(Selection selection) {
		logger.debug("Adding Selection");
		mongoTemplateExperimentDB.insert(selection);
		return selection;
	}

	public void update(Selection selection) {
		logger.debug("Updating Selection " + selection.getId());
		mongoTemplateExperimentDB.save(selection);
	}

	public void delete(String id) {
		logger.debug("Deleing Selection " + id);
		mongoTemplateExperimentDB.remove(find(id));
	}

	public List<Selection> findByAccount(String accountId) {
		return mongoTemplateExperimentDB.find(new Query(Criteria.where("account_id").is(accountId)), Selection.class);
	}

	public List<Selection> findByDataset(String datasetId) {
		return mongoTemplateExperimentDB.find(new Query(Criteria.where("dataset_id").is(datasetId)), Selection.class);
	}

}
