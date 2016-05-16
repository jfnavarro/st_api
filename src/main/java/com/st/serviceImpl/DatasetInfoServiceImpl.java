package com.st.serviceImpl;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.st.model.DatasetInfo;
import com.st.model.MongoUserDetails;
import com.st.service.DatasetInfoService;

/**
 * This class implements the store/retrieve logic to MongoDB for the data model
 * class "DatasetInfo". The DB connection is handled in a MongoOperations
 * object, which is configured in mvc-dispather-servlet.xml
 */
@Service
public class DatasetInfoServiceImpl implements DatasetInfoService {

    private static final Logger logger = Logger.getLogger(DatasetInfoServiceImpl.class);

    @Autowired
    MongoUserDetailsServiceImpl customUserDetailsService;

    @Autowired
    MongoOperations mongoTemplateUserDB;

    // ROLE_ADMIN: all.
    // ROLE_CM:    own.
    // ROLE_USER:  own.
    @Override
    public DatasetInfo find(String id) {
        DatasetInfo dsi = mongoTemplateUserDB.findOne(
                new Query(Criteria.where("id").is(id)), DatasetInfo.class);
        MongoUserDetails currentUser = customUserDetailsService.loadCurrentUser();
        if (currentUser.isAdmin() || dsi.getAccount_id().equals(currentUser.getId())) {
            return dsi;
        }
        return null;
    }

    // ROLE_ADMIN: all.
    // ROLE_CM:    own.
    // ROLE_USER:  own.
    @Override
    public List<DatasetInfo> findByAccount(String accountId) {
        List<DatasetInfo> dsis = mongoTemplateUserDB.find(
                new Query(Criteria.where("account_id").is(accountId)), DatasetInfo.class);
        if (dsis == null) {
            return null;
        }
        MongoUserDetails currentUser = customUserDetailsService.loadCurrentUser();
        if (currentUser.isAdmin() || accountId.equals(currentUser.getId())) {
            return dsis;
        }
        return null;
    }

    // ROLE_ADMIN: all.
    // ROLE_CM:    granted datasets.
    // ROLE_USER:  granted datasets.
    @Override
    public List<DatasetInfo> findByDataset(String datasetId) {
        List<DatasetInfo> dsis = mongoTemplateUserDB.find(
                new Query(Criteria.where("dataset_id").is(datasetId)), DatasetInfo.class);
        if (dsis == null) {
            return null;
        }
        MongoUserDetails currentUser = customUserDetailsService.loadCurrentUser();
        if (currentUser.isAdmin()) {
            return dsis;
        }
        for (int i = dsis.size() - 1; i >= 0; i--) {
            if (!dsis.get(i).getAccount_id().equals(currentUser.getId())) {
                dsis.remove(i);
            }
        }
        return dsis;
    }

    // ROLE_ADMIN: all.
    // ROLE_CM:    granted datasets.
    // ROLE_USER:  granted datasets.
    @Override
    public List<DatasetInfo> list() {
        List<DatasetInfo> dsis = mongoTemplateUserDB.findAll(DatasetInfo.class);
        if (dsis == null) {
            return null;
        }
        MongoUserDetails currentUser = customUserDetailsService.loadCurrentUser();
        if (currentUser.isAdmin()) {
            return dsis;
        }
        for (int i = dsis.size() - 1; i >= 0; i--) {
            if (!dsis.get(i).getAccount_id().equals(currentUser.getId())) {
                dsis.remove(i);
            }
        }
        return dsis;
    }

    // ROLE_ADMIN: ok.
    // ROLE_CM:    ok.
    // ROLE_USER:  nope.
    @Override
    public DatasetInfo add(DatasetInfo dsi) {
        MongoUserDetails currentUser = customUserDetailsService.loadCurrentUser();
        if (currentUser.isAdmin() || currentUser.isContentManager()) {
            mongoTemplateUserDB.insert(dsi);
            logger.info("Added dataset info " + dsi.getId() + " to MongoDB.");    
            return dsi;
        } 
        return null;
    }

    // ROLE_ADMIN: ok.
    // ROLE_CM:    ok.
    // ROLE_USER:  ok.
    @Override
    public void update(DatasetInfo dsi) {
        mongoTemplateUserDB.save(dsi);
        logger.info("Updated dataset info " + dsi.getId() + " to MongoDB.");
    }

    // ROLE_ADMIN: ok.
    // ROLE_CM:    ok.
    // ROLE_USER:  ok.
    @Override
    public void delete(String id) {
        mongoTemplateUserDB.remove(find(id));
        logger.info("Removed dataset info " + id + " from MongoDB.");
    }

    @Override
    public void deleteForDataset(String datasetId) {
        List<DatasetInfo> dsis = findByDataset(datasetId);
        if (dsis == null) {
            return;
        }
        for (DatasetInfo dsi : dsis) {
            delete(dsi.getId());
        }
    }

    @Override
    public void deleteForAccount(String accountId) {
        List<DatasetInfo> dsis = findByAccount(accountId);
        if (dsis == null) {
            return;
        }
        for (DatasetInfo dsi : dsis) {
            delete(dsi.getId());
        }
    }

}
