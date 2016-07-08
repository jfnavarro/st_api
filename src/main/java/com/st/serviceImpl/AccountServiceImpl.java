package com.st.serviceImpl;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.st.model.Account;
import com.st.model.DatasetInfo;
import com.st.model.MongoUserDetails;
import com.st.service.AccountService;

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
    // ROLE_CM:    own.
    // ROLE_USER:  own.
    @Override
    public Account find(String id) {
        MongoUserDetails currentUser = customUserDetailsService.loadCurrentUser();
        if (currentUser.isAdmin() || currentUser.getId().equals(id)) {
            return mongoTemplateUserDB.findOne(new Query(Criteria.where("id").is(id)), Account.class);
        }
        return null;
    }

    // ROLE_ADMIN: ok.
    // ROLE_CM:    own.
    // ROLE_USER:  own.
    @Override
    public Account findByUsername(String username) {
        MongoUserDetails currentUser = customUserDetailsService.loadCurrentUser();
        if (currentUser.isAdmin() || currentUser.getUsername().equals(username)) {
            return mongoTemplateUserDB.findOne(new Query(Criteria.where("username").is(username)), Account.class);
        }
        return null;
    }

    // ROLE_ADMIN: all.
    // ROLE_CM:    own.
    // ROLE_USER:  own.
    @Override
    public List<Account> list() {
        MongoUserDetails currentUser = customUserDetailsService.loadCurrentUser();
        if (currentUser.isAdmin()) {
            return mongoTemplateUserDB.findAll(Account.class);
        }
        ArrayList<Account> l = new ArrayList<>(1);
        l.add(mongoTemplateUserDB.findOne(new Query(Criteria.where("id").is(currentUser.getId())), Account.class));
        return l;
    }

    // ROLE_ADMIN: ok.
    // ROLE_CM:    none.
    // ROLE_USER:  none.
    @Override
    public Account add(Account account) {
        MongoUserDetails currentUser = customUserDetailsService.loadCurrentUser();
        if (currentUser.isAdmin()) {
            return null;
        }
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

    // ROLE_ADMIN: ok.
    // ROLE_CM:    none.
    // ROLE_USER:  none.
    @Override
    public void delete(String id) {
        if (deleteIsOkForCurrUser(id)) {
            mongoTemplateUserDB.remove(find(id));
            logger.info("Deleted account " + id + " from MongoDB.");
        }
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
        List<DatasetInfo> dsis = mongoTemplateUserDB.find(
                new Query(Criteria.where("dataset_id").is(datasetId)), DatasetInfo.class);
        if (dsis == null) {
            return null;
        }
        List<String> strs = new ArrayList<>(dsis.size());
        MongoUserDetails currentUser = customUserDetailsService.loadCurrentUser();
        final boolean isAdmin = currentUser.isAdmin();
        for (DatasetInfo dsi : dsis) {
            if (isAdmin || dsi.getAccount_id().equals(currentUser.getId())) {
                strs.add(dsi.getAccount_id());
            }
        } 
        return mongoTemplateUserDB.find(new Query(Criteria.where("id").in(strs)), Account.class);
    }

}
