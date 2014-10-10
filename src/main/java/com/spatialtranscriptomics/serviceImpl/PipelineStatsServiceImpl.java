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
import com.spatialtranscriptomics.model.PipelineExperiment;
import com.spatialtranscriptomics.model.PipelineStats;
import com.spatialtranscriptomics.service.PipelineStatsService;

/**
 * This class implements the store/retrieve logic to MongoDB for the data model
 * class "PipelineStats". The DB connection is handled in a MongoOperations
 * object, which is configured in mvc-dispatcher-servlet.xml
 */
@Service
public class PipelineStatsServiceImpl implements PipelineStatsService {

    private static final Logger logger = Logger
            .getLogger(PipelineStatsServiceImpl.class);

    @Autowired
    MongoUserDetailsServiceImpl customUserDetailsService;

    @Autowired
    MongoOperations mongoTemplateExperimentDB;

    // ROLE_ADMIN: all.
    // ROLE_CM:    own account.
    // ROLE_USER:  none.
    @Override
    public PipelineStats find(String id) {
        MongoUserDetails currentUser = customUserDetailsService.loadCurrentUser();
        PipelineStats stats = mongoTemplateExperimentDB.findOne(new Query(Criteria.where("id").is(id)), PipelineStats.class);
        if (stats == null || currentUser.isAdmin()) {
            return stats;
        }
        PipelineExperiment exp = mongoTemplateExperimentDB.findOne(new Query(Criteria.where("id").is(stats.getExperiment_id())), PipelineExperiment.class);
        if (exp.getAccount_id().equals(currentUser.getId())) {
            return stats;
        }
        return null;
    }

    // ROLE_ADMIN: all.
    // ROLE_CM:    own account.
    // ROLE_USER:  none.
    @Override
    public PipelineStats findByExperiment(String experimentId) {
        MongoUserDetails currentUser = customUserDetailsService.loadCurrentUser();
        PipelineStats stats = mongoTemplateExperimentDB.findOne(new Query(Criteria.where("experiment_id").is(experimentId)), PipelineStats.class);
        if (stats == null || currentUser.isAdmin()) {
            return stats;
        }
        PipelineExperiment exp = mongoTemplateExperimentDB.findOne(new Query(Criteria.where("id").is(experimentId)), PipelineExperiment.class);
        if (exp.getAccount_id().equals(currentUser.getId())) {
            return stats;
        }
        return null;
    }

    // ROLE_ADMIN: all.
    // ROLE_CM:    own account.
    // ROLE_USER:  none.
    @Override
    public List<PipelineStats> list() {
        MongoUserDetails currentUser = customUserDetailsService.loadCurrentUser();
        List<PipelineStats> list = mongoTemplateExperimentDB.findAll(PipelineStats.class);
        if (list == null || currentUser.isAdmin()) {
            return list;
        }
        for (int i = list.size() - 1; i >= 0; i--) {
            // This will be a bit slow, but works for now...
            PipelineExperiment exp = mongoTemplateExperimentDB.findOne(new Query(Criteria.where("id").is(list.get(i).getExperiment_id())), PipelineExperiment.class);
            if (!exp.getAccount_id().equals(currentUser.getId())) {
                list.remove(i);
            }
        }
        return list;
    }

    // ROLE_ADMIN: all.
    // ROLE_CM:    own account.
    // ROLE_USER:  none.
    @Override
    public PipelineStats add(PipelineStats stats) {
        MongoUserDetails currentUser = customUserDetailsService.loadCurrentUser();
        if (stats == null) {
            return null;
        }
        PipelineExperiment exp = mongoTemplateExperimentDB.findOne(new Query(Criteria.where("id").is(stats.getExperiment_id())), PipelineExperiment.class);
        if (currentUser.isAdmin() || exp.getAccount_id().equals(currentUser.getId())) {
            mongoTemplateExperimentDB.insert(stats);
            logger.info("Added pipeline stats " + stats.getId() + " to MongoDB.");
            return stats;
        }
        return null;
    }

    // ROLE_ADMIN: all.
    // ROLE_CM:    own account.
    // ROLE_USER:  none.
    @Override
    public void update(PipelineStats stats) {
        MongoUserDetails currentUser = customUserDetailsService.loadCurrentUser();
        if (stats == null) {
            return;
        }
        PipelineExperiment exp = mongoTemplateExperimentDB.findOne(new Query(Criteria.where("id").is(stats.getExperiment_id())), PipelineExperiment.class);
        if (currentUser.isAdmin() || exp.getAccount_id().equals(currentUser.getId())) {
            mongoTemplateExperimentDB.save(stats);
            logger.info("Updated pipeline stats " + stats.getId() + " to MongoDB.");
        }
    }

    // ROLE_ADMIN: all.
    // ROLE_CM:    own account.
    // ROLE_USER:  none.
    @Override
    public void delete(String id) {
        MongoUserDetails currentUser = customUserDetailsService.loadCurrentUser();
        PipelineStats stats = find(id);
        if (stats == null) {
            return;
        }
        PipelineExperiment exp = mongoTemplateExperimentDB.findOne(new Query(Criteria.where("id").is(stats.getExperiment_id())), PipelineExperiment.class);
        if (currentUser.isAdmin() || exp.getAccount_id().equals(currentUser.getId())) {
            mongoTemplateExperimentDB.remove(stats);
            logger.info("Deleting pipeline stats " + id + " from MongoDB.");
        }
    }

    @Override
    public void deleteForExperiment(String experimentId) {
        PipelineStats stats = findByExperiment(experimentId);
        if (stats != null) {
            delete(stats.getId());
        }
    }
}
