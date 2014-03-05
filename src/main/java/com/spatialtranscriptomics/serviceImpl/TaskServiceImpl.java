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

import com.spatialtranscriptomics.model.Task;
import com.spatialtranscriptomics.service.TaskService;

/**
 * This class implements the store/retrieve logic to MongoDB for the data model class "Task".
 * The DB connection is handled in a MongoOperations object, which is configured in mvc-dispather-servlet.xml
 */

@Service
public class TaskServiceImpl implements TaskService {

	private static final Logger logger = Logger
			.getLogger(TaskServiceImpl.class);

	@Autowired
	MongoUserDetailsServiceImpl customUserDetailsService;

	@Autowired
	MongoOperations mongoTemplateExperimentDB;

	public Task find(String id) {
		return mongoTemplateExperimentDB.findOne(new Query(Criteria.where("id").is(id)), Task.class);
	}

	public Task findByName(String name) {
		return mongoTemplateExperimentDB.findOne(new Query(Criteria.where("name").is(name)), Task.class);
	}

	public List<Task> list() {
		return mongoTemplateExperimentDB.findAll(Task.class);
	}

	public Task add(Task task) {
		logger.info("Adding Task");
		mongoTemplateExperimentDB.insert(task);
		return task;
	}

	public void update(Task task) {
		logger.info("Updating Task");
		mongoTemplateExperimentDB.save(task);
	}

	public void delete(String id) {
		logger.info("Deleting Task " + id);
		mongoTemplateExperimentDB.remove(find(id));
	}

	public List<Task> findByAccount(String accountId) {
		return mongoTemplateExperimentDB.find(new Query(Criteria.where("account_id").is(accountId)), Task.class);
	}

}
