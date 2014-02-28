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

import com.spatialtranscriptomics.model.PipelineExperiment;
import com.spatialtranscriptomics.service.PipelineExperimentService;

/**
 * This class implements the store/retrieve logic to MongoDB for the data model class "PipelineExperiment".
 * The DB connection is handled in a MongoOperations object, which is configured in mvc-dispatcher-servlet.xml
 */

@Service
public class PipelineExperimentServiceImpl implements PipelineExperimentService {

	private static final Logger logger = Logger
			.getLogger(PipelineExperimentServiceImpl.class);

	@Autowired
	MongoUserDetailsServiceImpl customUserDetailsService;

	@Autowired
	MongoOperations mongoTemplateExperimentDB;

	public PipelineExperiment find(String id) {
		return mongoTemplateExperimentDB.findOne(new Query(Criteria.where("id")
				.is(id)), PipelineExperiment.class);
	}

	public PipelineExperiment findByName(String name) {
		return mongoTemplateExperimentDB.findOne(new Query(Criteria.where("name")
				.is(name)), PipelineExperiment.class);
	}

	public List<PipelineExperiment> list() {
		return mongoTemplateExperimentDB.findAll(PipelineExperiment.class);
	}

	public PipelineExperiment add(PipelineExperiment experiment) {
		logger.debug("Adding PipelineExperiment");
		mongoTemplateExperimentDB.insert(experiment);
		return experiment;
	}

	public void update(PipelineExperiment experiment) {
		logger.debug("Updating PipelineExperiment " + experiment.getId());
		mongoTemplateExperimentDB.save(experiment);
	}

	public void delete(String id) {
		logger.debug("Deleting PipelineExperiment " + id);
		mongoTemplateExperimentDB.remove(find(id));
	}

	public List<PipelineExperiment> findByAccount(String accountId) {
		return mongoTemplateExperimentDB.find(new Query(Criteria.where("account_id")
				.is(accountId)), PipelineExperiment.class);
	}

}
