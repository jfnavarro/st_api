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

import com.spatialtranscriptomics.model.MongoUserDetails;
import com.spatialtranscriptomics.model.Task;
import com.spatialtranscriptomics.service.TaskService;

/**
 * This class implements the store/retrieve logic to MongoDB for the data model
 * class "Task". The DB connection is handled in a MongoOperations object, which
 * is configured in mvc-dispather-servlet.xml
 */
@Service
public class TaskServiceImpl implements TaskService {

    private static final Logger logger = Logger.getLogger(TaskServiceImpl.class);

    @Autowired
    MongoUserDetailsServiceImpl customUserDetailsService;

    @Autowired
    MongoOperations mongoTemplateExperimentDB;

    @Autowired
    AccountServiceImpl accountService;

    @Autowired
    DatasetServiceImpl datasetService;

    private Task checkCredentials(Task t) {
        if (t == null) {
            return null;
        }
        MongoUserDetails currentUser = customUserDetailsService.loadCurrentUser();
        if (currentUser.isAdmin() || currentUser.getId().equals(t.getAccount_id())) {
            return t;
        }
        return null;
    }

    // ROLE_ADMIN: all.
    // ROLE_CM:    own account.
    // ROLE_USER:  none.
    @Override
    public Task find(String id) {
        Task t = mongoTemplateExperimentDB.findOne(new Query(Criteria.where("id").is(id)), Task.class);
        return checkCredentials(t);
    }

    // ROLE_ADMIN: all.
    // ROLE_CM:    own account.
    // ROLE_USER:  none.
    @Override
    public Task findByName(String name) {
        Task t = mongoTemplateExperimentDB.findOne(new Query(Criteria.where("name").is(name)), Task.class);
        return checkCredentials(t);
    }

    // ROLE_ADMIN: all.
    // ROLE_CM:    own account.
    // ROLE_USER:  none.
    @Override
    public List<Task> list() {
        MongoUserDetails currentUser = customUserDetailsService.loadCurrentUser();
        if (currentUser.isAdmin()) {
            return mongoTemplateExperimentDB.findAll(Task.class);
        }
        return mongoTemplateExperimentDB.find(new Query(Criteria.where("account_id").is(currentUser.getId())), Task.class);
    }

    // ROLE_ADMIN: ok.
    // ROLE_CM:    own account.
    // ROLE_USER:  nope.
    @Override
    public Task add(Task task) {
        logger.info("Adding Task");
        MongoUserDetails currentUser = customUserDetailsService.loadCurrentUser();
        if (currentUser.isAdmin() || currentUser.getId().equals(task.getAccount_id())) {
            mongoTemplateExperimentDB.insert(task);
            return task;
        } else {
            logger.info("Failed to add Task -- user lacking access.");
            return null;
        }
    }

    // ROLE_ADMIN: ok.
    // ROLE_CM:    own account.
    // ROLE_USER:  nope.
    @Override
    public void update(Task task) {
        logger.info("Updating Task");
        MongoUserDetails currentUser = customUserDetailsService.loadCurrentUser();
        if (currentUser.isAdmin() || currentUser.getId().equals(task.getAccount_id())) {
            mongoTemplateExperimentDB.save(task);
        } else {
            logger.info("Failed to update Task -- user lacking access.");
        }
    }

    // ROLE_ADMIN: ok.
    // ROLE_CM:    own account.
    // ROLE_USER:  nope.
    @Override
    public void delete(String id) {
        logger.info("Deleting Task " + id);
        MongoUserDetails currentUser = customUserDetailsService.loadCurrentUser();
        Task t = find(id);
        if (currentUser.isAdmin() || currentUser.getId().equals(t.getAccount_id())) {
            mongoTemplateExperimentDB.remove(t);
        } else {
            logger.info("Failed to delete Task -- user lacking access.");
        }
    }

    // ROLE_ADMIN: all.
    // ROLE_CM:    own account.
    // ROLE_USER:  none.
    @Override
    public List<Task> findByAccount(String accountId) {
        MongoUserDetails currentUser = customUserDetailsService.loadCurrentUser();
        if (currentUser.isAdmin() || currentUser.getId().equals(accountId)) {
            return mongoTemplateExperimentDB.find(new Query(Criteria.where("account_id").is(accountId)), Task.class);
        }
        return null;
    }

    @Override
    public void deleteForAccount(String accountId) {
        List<Task> ts = findByAccount(accountId);
        if (ts == null) {
            return;
        }
        for (Task t : ts) {
            delete(t.getId());
        }
    }

}
