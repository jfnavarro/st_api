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

import com.spatialtranscriptomics.model.Experiment;
import com.spatialtranscriptomics.service.ExperimentService;

/**
 * This class implements the store/retrieve logic to MongoDB for the data model class "Experiment".
 * The DB connection is handled in a MongoOperations object, which is configured in mvc-dispatcher-servlet.xml
 */

@Service
public class ExperimentServiceImpl implements ExperimentService {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger
			.getLogger(ExperimentServiceImpl.class);

	@Autowired
	MongoUserDetailsServiceImpl customUserDetailsService;

	@Autowired
	MongoOperations mongoTemplateExperiment;

	public Experiment find(String id) {

		return mongoTemplateExperiment.findOne(new Query(Criteria.where("id")
				.is(id)), Experiment.class);
	}

	public Experiment findByName(String name) {

		return mongoTemplateExperiment.findOne(new Query(Criteria.where("name")
				.is(name)), Experiment.class);
	}

	public List<Experiment> list() {

		return mongoTemplateExperiment.findAll(Experiment.class);
	}

	public Experiment add(Experiment experiment) {

		mongoTemplateExperiment.insert(experiment);
		return experiment;
	}

	public void update(Experiment experiment) {

		mongoTemplateExperiment.save(experiment);
	}

	public void delete(String id) {

		mongoTemplateExperiment.remove(find(id));
	}

}
