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

    private static final Logger logger = Logger
            .getLogger(DatasetServiceImpl.class);

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
            logger.info("Adding dataset");
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
            return mongoTemplateAnalysisDB.findOne(new Query(Criteria.where("id").is(id)), Dataset.class);
        }
        return null;
    }

    @Override
    public boolean datasetIsGranted(String datasetId, MongoUserDetails user) {
        List<DatasetInfo> dsis = mongoTemplateUserDB.find(new Query(Criteria.where("dataset_id").is(datasetId).and("account_id").is(user.getId())), DatasetInfo.class);
        return (dsis != null && dsis.size() > 0);
    }

    // required for check to ensure unique dataset names.
    // No user check.
    @Override
    public Dataset findByNameInternal(String name) {
        return mongoTemplateAnalysisDB.findOne(new Query(Criteria.where("name").is(name)), Dataset.class);
    }

    // ROLE_ADMIN: all datasets.
    // ROLE_CM:    granted datasets.
    // ROLE_USER:  granted datasets.
    @Override
    public Dataset findByName(String name) {
        Dataset ds = mongoTemplateAnalysisDB.findOne(new Query(Criteria.where("name").is(name)), Dataset.class);
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
            logger.info("Updating dataset " + ds.getId());
            mongoTemplateAnalysisDB.save(ds);
        }
    }

    
    // See deleteIsOkForCurrUser(). Internal use may be different
    @Override
    public void delete(String id) {
        MongoUserDetails currentUser = customUserDetailsService.loadCurrentUser();
        if (currentUser.isAdmin() || (datasetIsGranted(id, currentUser))) {
            logger.info("Deleting dataset " + id);
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
        } // In case of pre-login calls.
        if (customUserDetailsService == null) {
            return null;
        }   // In case of pre-login calls.
        MongoUserDetails currentUser = customUserDetailsService.loadCurrentUser();
        if (currentUser == null) {
            return null;
        }                // In case of pre-login calls.

        if (currentUser.isAdmin() || currentUser.getId().equals(accountId)) {
            try {
                List<DatasetInfo> dsis = mongoTemplateUserDB.find(new Query(Criteria.where("account_id").is(accountId)), DatasetInfo.class);
                if (dsis == null) {
                    return null;
                }
                List<String> strs = new ArrayList<String>(dsis.size());
                for (DatasetInfo dsi : dsis) {
                    strs.add(dsi.getDataset_id());
                }
                return mongoTemplateAnalysisDB.find(new Query(Criteria.where("id").in(strs)), Dataset.class);
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }

    @Override
    public void clearAccountCreator(String accountId) {
        List<Dataset> l = list();
        for (Dataset d : l) {
            if (d.getCreated_by_account_id() != null && d.getCreated_by_account_id().equals(accountId)) {
                d.setCreated_by_account_id("");
                update(d);
            }
        }
    }

    @Override
    public void setUnabledForImageAlignment(String imalId) {
        List<Dataset> ds = list();
        if (ds == null) {
            return;
        }
        for (Dataset d : ds) {
            if (d.getImage_alignment_id() != null && d.getImage_alignment_id().equals(imalId)) {
                d.setEnabled(false);
                d.setImage_alignment_id("");
                update(d);
            }
        }
    }

}
