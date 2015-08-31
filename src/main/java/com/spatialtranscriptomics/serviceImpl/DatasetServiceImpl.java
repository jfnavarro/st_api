/*
 * Copyright (C) 2012 Spatial Transcriptomics AB
 * Read LICENSE for more information about licensing terms
 * Contact: Jose Fernandez Navarro <jose.fernandez.navarro@scilifelab.se>
 */

package com.spatialtranscriptomics.serviceImpl;

import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.spatialtranscriptomics.model.Dataset;
import com.spatialtranscriptomics.model.DatasetInfo;
import com.spatialtranscriptomics.model.MongoUserDetails;
import com.spatialtranscriptomics.service.DatasetService;

/**
 * This class implements the store/retrieve logic to MongoDB for the data model
 * class "Dataset". The DB connection is handled in a MongoOperations object,
 * which is configured in mvc-dispather-servlet.xml
 */

@Service
public class DatasetServiceImpl implements DatasetService {

    private static final Logger logger = Logger.getLogger(DatasetServiceImpl.class);

    @Autowired
    MongoUserDetailsServiceImpl customUserDetailsService;

    @Autowired
    MongoOperations mongoTemplateAnalysisDB;

    @Autowired
    MongoOperations mongoTemplateUserDB;

    // ROLE_ADMIN: ok.
    // ROLE_CM:    ok.
    // ROLE_USER:  none.
    @Override
    public Dataset add(Dataset ds) {
        MongoUserDetails currentUser = customUserDetailsService.loadCurrentUser();
        if (currentUser.isAdmin() || currentUser.isContentManager()) {
            logger.info("Adding dataset to DB.");
            mongoTemplateAnalysisDB.insert(ds);
            return ds;
        }
        
        return null;
    }

    // ROLE_ADMIN: all datasets.
    // ROLE_CM:    granted datasets.
    // ROLE_USER:  granted datasets.
    @Override
    public Dataset find(String id) {
        MongoUserDetails currentUser = customUserDetailsService.loadCurrentUser();
        if (currentUser.isAdmin() || datasetIsGranted(id, currentUser)) {
            return mongoTemplateAnalysisDB.findOne(
                    new Query(Criteria.where("id").is(id)), Dataset.class);
        }
        
        return null;
    }

    // ROLE_ADMIN: all datasets.
    // ROLE_CM:    own datasets.
    // ROLE_USER:  own datasets.
    @Override
    public boolean datasetIsGranted(String datasetId, MongoUserDetails user) {
        List<DatasetInfo> dsis = mongoTemplateUserDB.find(
                new Query(Criteria.where("dataset_id").is(datasetId).and("account_id").is(user.getId())), 
                DatasetInfo.class);
        return (dsis != null && dsis.size() > 0);
    }

    // required for check to ensure unique dataset names.
    // No user check.
    @Override
    public Dataset findByNameInternal(String name) {
        return mongoTemplateAnalysisDB.findOne(
                new Query(Criteria.where("name").is(name)), Dataset.class);
    }

    // ROLE_ADMIN: all datasets.
    // ROLE_CM:    granted datasets.
    // ROLE_USER:  granted datasets.
    @Override
    public Dataset findByName(String name) {
        Dataset ds = mongoTemplateAnalysisDB.findOne(
                new Query(Criteria.where("name").is(name)), Dataset.class);
        if (ds == null) {
            return null;
        }
        
        MongoUserDetails currentUser = customUserDetailsService.loadCurrentUser();
        if (currentUser.isAdmin() || datasetIsGranted(ds.getId(), currentUser)) {
            return ds;
        }
        
        return null;
    }

    // ROLE_ADMIN: all datasets.
    // ROLE_CM:    granted datasets.
    // ROLE_USER:  granted datasets.
    @Override
    public List<Dataset> list() {
        MongoUserDetails currentUser = customUserDetailsService.loadCurrentUser();
        if (currentUser.isAdmin()) {
            return mongoTemplateAnalysisDB.findAll(Dataset.class);
        }
        
        return this.findByAccount(currentUser.getId());
    }

    // ROLE_ADMIN: all datasets.
    // ROLE_CM:    granted datasets.
    // ROLE_USER:  granted datasets.
    @Override
    public void update(Dataset ds) {
        MongoUserDetails currentUser = customUserDetailsService.loadCurrentUser();
        if (currentUser.isAdmin() || datasetIsGranted(ds.getId(), currentUser)) {
            logger.info("Updating dataset " + ds.getId() + " in DB.");
            mongoTemplateAnalysisDB.save(ds);
        }
    }
    
    // See deleteIsOkForCurrUser(). Internal use may be different
    @Override
    public void delete(String id) {
        MongoUserDetails currentUser = customUserDetailsService.loadCurrentUser();
        if (currentUser.isAdmin() || (datasetIsGranted(id, currentUser))) {
            logger.info("Deleting dataset " + id + " from DB.");
            mongoTemplateAnalysisDB.remove(find(id));
        }
    }
    
    // ROLE_ADMIN: all datasets.
    // ROLE_CM:    granted datasets.
    // ROLE_USER:  granted datasets.
    @Override
    public boolean deleteIsOkForCurrUser(String id) {
        MongoUserDetails currentUser = customUserDetailsService.loadCurrentUser();
        return (currentUser.isAdmin() || (datasetIsGranted(id, currentUser))) && find(id) != null;
    }

    // ROLE_ADMIN: all datasets.
    // ROLE_CM:    granted datasets.
    // ROLE_USER:  granted datasets.
    @Override
    public List<Dataset> findByAccount(String accountId) {
        if (!customUserDetailsService.isProperlyLoaded()) {
            return null;
        }// In case of pre-login calls.
        
        if (customUserDetailsService == null) {
            return null;
        }// In case of pre-login calls.
        
        MongoUserDetails currentUser = customUserDetailsService.loadCurrentUser();
        if (currentUser == null) {
            return null;
        }// In case of pre-login calls.

        try {
            
            List<DatasetInfo> dat_infos = mongoTemplateUserDB.find(
                    new Query(Criteria.where("account_id").is(accountId)), DatasetInfo.class);
                
            if (dat_infos == null) {
                return null;
            }
                
            //filter out dataset infos and get the ids of the datasets
            List<String> strs = new ArrayList<String>(dat_infos.size());
            for (DatasetInfo dsi : dat_infos) {
                if (currentUser.isAdmin() || currentUser.getId().equals(dsi.getAccount_id())) {
                    strs.add(dsi.getDataset_id());
                }
            }
                
            return mongoTemplateAnalysisDB.find(
                    new Query(Criteria.where("id").in(strs)), Dataset.class);
            
        } catch (Exception e) {
            logger.info("There was an error retrieving datasets by account", e);
            return null;
        }
    }
    
    // ROLE_ADMIN: all datasets.
    // ROLE_CM:    all datasets.
    // ROLE_USER:  all datasets.
    @Override
    public List<Dataset> findByImageAlignment(String imageAlignmentId) {
        return mongoTemplateAnalysisDB.find(
                new Query(Criteria.where("image_alignment_id").is(imageAlignmentId)), Dataset.class);
    }

    //helper function to set the account_id field to empty for datasets created
    //by the given accountId param
    @Override
    public void clearAccountCreator(String accountId) {
        List<Dataset> datasets = list();
        
        for (Dataset dataset : datasets) {
            if (dataset.getCreated_by_account_id() != null 
                    && dataset.getCreated_by_account_id().equals(accountId)) {
                dataset.setCreated_by_account_id("");
                update(dataset);
            }
        }
    }

}
