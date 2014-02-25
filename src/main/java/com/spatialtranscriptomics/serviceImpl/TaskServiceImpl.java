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

	@SuppressWarnings("unused")
	private static final Logger logger = Logger
			.getLogger(TaskServiceImpl.class);

	@Autowired
	MongoUserDetailsServiceImpl customUserDetailsService;

	@Autowired
	MongoOperations mongoTemplateExperiment;

	public Task find(String id) {
		return mongoTemplateExperiment.findOne(new Query(Criteria.where("id").is(id)), Task.class);
	}

	public Task findByName(String name) {
		return mongoTemplateExperiment.findOne(new Query(Criteria.where("name").is(name)), Task.class);
	}

	public List<Task> list() {
		return mongoTemplateExperiment.findAll(Task.class);
	}

	public Task add(Task task) {
		mongoTemplateExperiment.insert(task);
		return task;
	}

	public void update(Task task) {
		mongoTemplateExperiment.save(task);
	}

	public void delete(String id) {
		mongoTemplateExperiment.remove(find(id));
	}

	public List<Task> findByAccount(String accountId) {
		return mongoTemplateExperiment.find(new Query(Criteria.where("account_id").is(accountId)), Task.class);
	}

}
