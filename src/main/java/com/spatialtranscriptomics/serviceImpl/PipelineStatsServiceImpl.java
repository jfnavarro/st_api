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

import com.spatialtranscriptomics.model.PipelineStats;
import com.spatialtranscriptomics.service.PipelineStatsService;

/**
 * This class implements the store/retrieve logic to MongoDB for the data model class "PipelineStats".
 * The DB connection is handled in a MongoOperations object, which is configured in mvc-dispatcher-servlet.xml
 */

@Service
public class PipelineStatsServiceImpl implements PipelineStatsService {

	private static final Logger logger = Logger
			.getLogger(PipelineStatsServiceImpl.class);

	@Autowired
	MongoUserDetailsServiceImpl customUserDetailsService;

	@Autowired
	MongoOperations mongoTemplateExperimentDB;

	public PipelineStats find(String id) {
		return mongoTemplateExperimentDB.findOne(new Query(Criteria.where("id").is(id)), PipelineStats.class);
	}

	public PipelineStats findByExperiment(String experimentId) {
		return mongoTemplateExperimentDB.findOne(new Query(Criteria.where("experiment_id").is(experimentId)), PipelineStats.class);
	}

	public List<PipelineStats> list() {
		return mongoTemplateExperimentDB.findAll(PipelineStats.class);
	}

	public PipelineStats add(PipelineStats experiment) {
		logger.debug("Adding PipelineStats");
		mongoTemplateExperimentDB.insert(experiment);
		return experiment;
	}

	public void update(PipelineStats experiment) {
		logger.debug("Updating PipelineStats " + experiment.getId());
		mongoTemplateExperimentDB.save(experiment);
	}

	public void delete(String id) {
		logger.debug("Deleting PipelineStats " + id);
		mongoTemplateExperimentDB.remove(find(id));
	}

}
