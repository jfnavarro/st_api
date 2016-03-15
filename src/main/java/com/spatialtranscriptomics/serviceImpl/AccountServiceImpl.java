package com.spatialtranscriptomics.serviceImpl;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.spatialtranscriptomics.model.Account;
import com.spatialtranscriptomics.model.DatasetInfo;
import com.spatialtranscriptomics.model.MongoUserDetails;
import com.spatialtranscriptomics.service.AccountService;

/**
 * This class implements the store/retrieve logic to MongoDB for the data model
 * class "Account". The DB connection is handled in a MongoOperations object,
 * which is configured in mvc-dispather-servlet.xml
 */
@Service
public class AccountServiceImpl implements AccountService {

    private static final Logger logger = Logger
            .getLogger(AccountServiceImpl.class);

    @Autowired
    MongoOperations mongoTemplateUserDB;

    @Autowired
    MongoUserDetailsServiceImpl customUserDetailsService;

    // ROLE_ADMIN: ok.
    // ROLE_CM:    ok.
    // ROLE_USER:  own.
    @Override
    public Account find(String id) {
        MongoUserDetails currentUser = customUserDetailsService.loadCurrentUser();
        if (currentUser.isAdmin() || currentUser.isContentManager() || currentUser.getId().equals(id)) {
            return mongoTemplateUserDB.findOne(new Query(Criteria.where("id").is(id)), Account.class);
        }
        return null;
    }

    // ROLE_ADMIN: ok.
    // ROLE_CM:    ok.
    // ROLE_USER:  own.
    @Override
    public Account findByUsername(String username) {
        MongoUserDetails currentUser = customUserDetailsService.loadCurrentUser();
        if (currentUser.isAdmin() || currentUser.isContentManager() || currentUser.getUsername().equals(username)) {
            return mongoTemplateUserDB.findOne(new Query(Criteria.where("username").is(username)), Account.class);
        }
        return null;
    }

    // ROLE_ADMIN: all.
    // ROLE_CM:    all.
    // ROLE_USER:  own.
    @Override
    public List<Account> list() {
        MongoUserDetails currentUser = customUserDetailsService.loadCurrentUser();
        if (currentUser.isAdmin() || currentUser.isContentManager()) {
            return mongoTemplateUserDB.findAll(Account.class);
        }
        ArrayList<Account> l = new ArrayList<Account>(1);
        l.add(mongoTemplateUserDB.findOne(new Query(Criteria.where("id").is(currentUser.getId())), Account.class));
        return l;
    }

    // ROLE_ADMIN: ok.
    // ROLE_CM:    none.
    // ROLE_USER:  none.
    @Override
    public Account add(Account account) {
        mongoTemplateUserDB.insert(account);
        logger.info("Added account " + account.getId() + " to MongoDB.");
        return account;
    }

    // ROLE_ADMIN: ok.
    // ROLE_CM:    own (e.g., password change).
    // ROLE_USER:  own (e.g., password change).
    @Override
    public void update(Account account) {
        MongoUserDetails currentUser = customUserDetailsService.loadCurrentUser();
        if (currentUser.isAdmin() || currentUser.getId().equals(account.getId())) {
            mongoTemplateUserDB.save(account);
            logger.info("Updated account " + account.getId() + " to MongoDB.");
        }
    }

    // See deleteIsOkForCurrUser(). Internal use may be different
    @Override
    public void delete(String id) {
        mongoTemplateUserDB.remove(find(id));
        logger.info("Deleted account " + id + " from MongoDB.");
    }

    // ROLE_ADMIN: ok.
    // ROLE_CM:    none.
    // ROLE_USER:  none.
    @Override
    public boolean deleteIsOkForCurrUser(String id) {
        MongoUserDetails currentUser = customUserDetailsService.loadCurrentUser();
        return (currentUser.isAdmin() && find(id) != null);
    }

    // ROLE_ADMIN: all.
    // ROLE_CM:    own.
    // ROLE_USER:  own.
    @Override
    public List<Account> findByDataset(String datasetId) {
        List<DatasetInfo> dsis = mongoTemplateUserDB.find(new Query(Criteria.where("dataset_id").is(datasetId)), DatasetInfo.class);
        if (dsis == null) {
            return null;
        }
        List<String> strs = new ArrayList<String>(dsis.size());
        MongoUserDetails currentUser = customUserDetailsService.loadCurrentUser();
        if (currentUser.isAdmin()) {
            for (DatasetInfo dsi : dsis) {
                strs.add(dsi.getAccount_id());
            }
        } else {
            for (DatasetInfo dsi : dsis) {
                if (dsi.getAccount_id().equals(currentUser.getId())) {
                    strs.add(dsi.getAccount_id());
                    break;
                }
            }
        }
        return mongoTemplateUserDB.find(new Query(Criteria.where("id").in(strs)), Account.class);
    }

}
