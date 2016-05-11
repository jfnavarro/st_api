package com.st.serviceImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.st.model.Dataset;
import com.st.model.MongoUserDetails;
import com.st.model.Selection;
import com.st.service.SelectionService;

/**
 * This class implements the store/retrieve logic to MongoDB for the data model
 * class "Selection". The DB connection is handled in a MongoOperations object,
 * which is configured in mvc-dispather-servlet.xml
 */
@Service
public class SelectionServiceImpl implements SelectionService {

    private static final Logger logger = Logger
            .getLogger(SelectionServiceImpl.class);

    @Autowired
    MongoUserDetailsServiceImpl customUserDetailsService;

    @Autowired
    MongoOperations mongoTemplateExperimentDB;

    @Autowired
    AccountServiceImpl accountService;

    @Autowired
    DatasetServiceImpl datasetService;

    // ROLE_ADMIN: all.
    // ROLE_CM:    own account.
    // ROLE_USER:  own account.
    @Override
    public Selection find(String id) {
        Selection selection = mongoTemplateExperimentDB.findOne(new Query(Criteria.where("id").is(id)), Selection.class);
        return checkCredentials(selection);
    }

    // ROLE_ADMIN: all.
    // ROLE_CM:    own account.
    // ROLE_USER:  own account.
    @Override
    public Selection findByName(String name) {
        Selection sel = mongoTemplateExperimentDB.findOne(new Query(Criteria.where("name").is(name)), Selection.class);
        return checkCredentials(sel);
    }

    // ROLE_ADMIN: all.
    // ROLE_CM:    own account.
    // ROLE_USER:  own account.
    @Override
    public List<Selection> list() {
        List<Selection> selections = mongoTemplateExperimentDB.findAll(Selection.class);
        // Filter based on user.
        MongoUserDetails currentUser = customUserDetailsService.loadCurrentUser();
        if (currentUser.isAdmin()) {
        } else {
            List<Dataset> datasets = datasetService.findByAccount(currentUser.getId());
            HashMap<String, Dataset> hash = new HashMap<>(datasets.size());
            for (Dataset d : datasets) {
                hash.put(d.getId(), d);
            }
            // Replace to below to enable non-owner users w access to the dataset to access the selection.
            ArrayList<Selection> filtered = new ArrayList<>(selections.size());
            for (Selection sel : selections) {
                if (sel.getAccount_id().equals(currentUser.getId()) /*|| hash.containsKey(sel.getDataset_id())*/) {
                    filtered.add(sel);
                }
            }
            selections = filtered;
        }
        return selections;
    }

    // ROLE_ADMIN: all.
    // ROLE_CM:    own account.
    // ROLE_USER:  own account.
    @Override
    public Selection add(Selection selection) {
        selection = checkCredentials(selection);
        if (selection != null) {
            mongoTemplateExperimentDB.insert(selection);
            logger.info("Added selection " + selection.getId() + " to MongoDB.");
        }
        return selection;
    }

    // ROLE_ADMIN: all.
    // ROLE_CM:    own account.
    // ROLE_USER:  own account.
    @Override
    public void update(Selection selection) {
        selection = checkCredentials(selection);
        if (selection != null) {
            mongoTemplateExperimentDB.save(selection);
            logger.info("Updated selection " + selection.getId() + " to MongoDB.");
        }
    }

    // ROLE_ADMIN: all.
    // ROLE_CM:    own account.
    // ROLE_USER:  own account.
    @Override
    public void delete(String id) {
        Selection selection = checkCredentials(find(id));
        if (selection != null) {
            mongoTemplateExperimentDB.remove(selection);
            logger.info("Deleted account " + id + " from MongoDB.");
        }
    }

    // ROLE_ADMIN: all.
    // ROLE_CM:    own account.
    // ROLE_USER:  own account.
    @Override
    public List<Selection> findByAccount(String accountId) {
        MongoUserDetails currentUser = customUserDetailsService.loadCurrentUser();
        if (currentUser.isAdmin() || currentUser.getId().equals(accountId)) {
            return mongoTemplateExperimentDB.find(new Query(Criteria.where("account_id").is(accountId)), Selection.class);
        }
        return null;
    }

    // ROLE_ADMIN: all.
    // ROLE_CM:    own account.
    // ROLE_USER:  own account.
    @Override
    public List<Selection> findByDataset(String datasetId) {
        MongoUserDetails currentUser = customUserDetailsService.loadCurrentUser();
        if (currentUser.isAdmin()) {
            return mongoTemplateExperimentDB.find(new Query(Criteria.where("dataset_id").is(datasetId)), Selection.class);
        }
        // Replace to below to enable non-owner users w access to the dataset to access the selection.
        return mongoTemplateExperimentDB.find(new Query(Criteria.where("account_id").is(currentUser.getId())), Selection.class);
        //List<Dataset> datasets = datasetService.findByAccount(currentUser.getId());
        //for (Dataset d : datasets) {
        //	if (d.getId().equals(datasetId)) {
        //		return mongoTemplateExperimentDB.find(new Query(Criteria.where("dataset_id").is(datasetId)), Selection.class);
        //	}
        //}
        //return null;
    }


    @Override
    public void deleteForDataset(String datasetId) {
        List<Selection> sels = findByDataset(datasetId);
        if (sels == null) {
            return;
        }
        for (Selection sel : sels) {
            delete(sel.getId());
        }
    }

    @Override
    public void deleteForAccount(String accountId) {
        List<Selection> sels = findByAccount(accountId);
        if (sels == null) {
            return;
        }
        for (Selection sel : sels) {
            delete(sel.getId());
        }
    }

    // Helper.
    private Selection checkCredentials(Selection sel) {
        MongoUserDetails currentUser = customUserDetailsService.loadCurrentUser();
        if (currentUser.isAdmin() || sel == null) {
            return sel;
        } else {
            if (currentUser.getId().equals(sel.getAccount_id())) {
                return sel;
            }
            // Add this to enable non-owner users w access to the dataset to access the selection.
            //List<Account> accounts = accountService.findByDataset(sel.getDataset_id());
            //for (Account acc : accounts) {
            //	if (currentUser.getId().equals(acc.getId())) {
            //		return sel;
            //	}
            //}
        }
        return null;
    }
}
