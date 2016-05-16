package com.st.serviceImpl;

import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
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
        Selection selection = mongoTemplateExperimentDB.findOne(
                new Query(Criteria.where("id").is(id)), Selection.class);
        return checkCredentials(selection);
    }

    // ROLE_ADMIN: all.
    // ROLE_CM:    own account.
    // ROLE_USER:  own account.
    @Override
    public Selection findByName(String name) {
        Selection sel = mongoTemplateExperimentDB.findOne(
                new Query(Criteria.where("name").is(name)), Selection.class);
        return checkCredentials(sel);
    }

    // ROLE_ADMIN: all.
    // ROLE_CM:    own account.
    // ROLE_USER:  own account.
    @Override
    public List<Selection> list() {
        // get all the selections
        List<Selection> selections = mongoTemplateExperimentDB.findAll(Selection.class);
        // Filter based on user.
        MongoUserDetails currentUser = customUserDetailsService.loadCurrentUser();
        if (!currentUser.isAdmin()) {
            // CM or USER role can only see the selections they made
            ArrayList<Selection> filtered = new ArrayList<>(selections.size());
            for (Selection sel : selections) {
                if (sel.getAccount_id().equals(currentUser.getId())) {
                    filtered.add(sel);
                }
            }
            return filtered;
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
            return mongoTemplateExperimentDB.find(
                    new Query(Criteria.where("account_id").is(accountId)), Selection.class);
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
            return mongoTemplateExperimentDB.find(
                    new Query(Criteria.where("dataset_id").is(datasetId)), Selection.class);
        }
        // CM or USER role can only see the selections they made
        return mongoTemplateExperimentDB.find(
                new Query(Criteria.where("account_id").is(currentUser.getId())), Selection.class);
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
            // CM or USER role can only see the selections they made
            if (currentUser.getId().equals(sel.getAccount_id())) {
                return sel;
            }
        }
        return null;
    }
}
