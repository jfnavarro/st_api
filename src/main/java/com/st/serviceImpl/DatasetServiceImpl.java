package com.st.serviceImpl;

import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import com.st.model.Dataset;
import com.st.model.DatasetInfo;
import com.st.model.MongoUserDetails;
import com.st.service.DatasetService;

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

    @Autowired
    FileServiceImpl filesService;
    
    //add the file services
    
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
            return mongoTemplateAnalysisDB.findOne(
                    new Query(Criteria.where("id").is(id)), Dataset.class);
        }
        return null;
    }

    // Helper method to check if a dataset is granted to an user
    @Override
    public boolean datasetIsGranted(String datasetId, MongoUserDetails user) {
        List<DatasetInfo> dsis = mongoTemplateUserDB.find(
                new Query(Criteria.where("dataset_id").is(datasetId).and("account_id").is(user.getId())), 
                DatasetInfo.class);
        return (dsis != null && dsis.size() > 0);
    }

    // Helper method to check for duplicated names
    @Override
    public boolean datasetNameExist(String name) {
        Dataset dataset = mongoTemplateAnalysisDB.findOne(new 
        Query(Criteria.where("name").is(name)), Dataset.class);
        return (dataset != null);
    }

    // Helper method to check for duplicated Ids and Names
    @Override
    public boolean datasetNameIdExist(String name, String id) {
        Dataset dataset = mongoTemplateAnalysisDB.findOne(new 
        Query(Criteria.where("name").is(name)), Dataset.class);
        return (dataset != null && !dataset.getId().equals(id));
    }
    
    // ROLE_ADMIN: all datasets.
    // ROLE_CM:    granted datasets.
    // ROLE_USER:  granted datasets.
    @Override
    public Dataset findByName(String name) {
        Dataset ds = mongoTemplateAnalysisDB.findOne(new 
        Query(Criteria.where("name").is(name)), Dataset.class);
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
        return findByAccount(currentUser.getId());
    }

    // ROLE_ADMIN: all datasets.
    // ROLE_CM:    granted datasets.
    // ROLE_USER:  granted datasets.
    @Override
    public boolean update(Dataset ds) {
        MongoUserDetails currentUser = customUserDetailsService.loadCurrentUser();
        if (currentUser.isAdmin() || datasetIsGranted(ds.getId(), currentUser)) {
            logger.info("Updating dataset " + ds.getId());
            mongoTemplateAnalysisDB.save(ds);
            return true;
        }
        return false;
    }

    
    // ROLE_ADMIN: all datasets.
    // ROLE_CM:    granted datasets.
    // ROLE_USER:  granted datasets.
    @Override
    public boolean delete(String id) {
        MongoUserDetails currentUser = customUserDetailsService.loadCurrentUser();
        Dataset dataset = find(id);
        if (dataset != null && (currentUser.isAdmin() 
                || (datasetIsGranted(id, currentUser)))) {
            logger.info("Deleting dataset " + id);
            mongoTemplateAnalysisDB.remove(dataset);
            boolean files_deleted = true;
            for (String filename : dataset.getFiles()) {
                files_deleted &= filesService.delete(filename, id);
            }
            files_deleted &= filesService.delete(dataset.getDataFile(), id);
            if (!files_deleted) {
                logger.info("There were error deleting the files for " + id);
                return false;
            }
            return true;
        }
        return false;
    }

    // ROLE_ADMIN: all datasets.
    // ROLE_CM:    all datasets.
    // ROLE_USER:  granted datasets.
    @Override
    public List<Dataset> findByAccount(String accountId) {
        // In case of pre-login calls.
        if (!customUserDetailsService.isProperlyLoaded()) {
            return null;
        } 
        // In case of pre-login calls.
        if (customUserDetailsService == null) {
            return null;
        }   
        // In case of pre-login calls.
        MongoUserDetails currentUser = customUserDetailsService.loadCurrentUser();
        if (currentUser == null) {
            return null;
        }                

        if (!currentUser.isAdmin() && !currentUser.getId().equals(accountId)) {
            return null;
        }
        
        try {
            // retrieve dataset_info objects by account
            List<DatasetInfo> dat_infos = mongoTemplateUserDB.find(
                    new Query(Criteria.where("account_id").is(accountId)), DatasetInfo.class);  
            if (dat_infos == null) {
                return null;
            }
                
            List<String> strs = new ArrayList<>(dat_infos.size());
            for (DatasetInfo dsi : dat_infos) {              
                strs.add(dsi.getDataset_id());
            }
                
            return mongoTemplateAnalysisDB.find(
                    new Query(Criteria.where("id").in(strs)), Dataset.class);
            
        } catch (Exception e) {
            logger.info("There was an error retrieving datasets by account", e);
            return null;
        }
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
