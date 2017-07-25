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
import com.st.model.AccountId;
import com.st.model.DatasetInfo;
import com.st.model.MongoUserDetails;
import com.st.service.AccountService;
import java.util.HashSet;
import java.util.Set;

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
            return mongoTemplateUserDB.findOne(
                    new Query(Criteria.where("id").is(id)), Account.class);
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
            return mongoTemplateUserDB.findOne(
                    new Query(Criteria.where("username").is(username)), Account.class);
        }
        return null;
    }

    // Helper method to check for duplicated names
    @Override
    public boolean accountNameExist(String username) {
        Account account = mongoTemplateUserDB.findOne(
                new Query(Criteria.where("username").is(username)), Account.class);
        return (account != null);
    }

    // Helper method to check for duplicated Ids and Names
    @Override
    public boolean accountNameIdExist(String username, String id) {
        Account account = mongoTemplateUserDB.findOne(
                new Query(Criteria.where("username").is(username)), Account.class);
        return (account != null && !account.getId().equals(id));
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
        ArrayList<Account> accounts = new ArrayList<>(1);
        accounts.add(mongoTemplateUserDB.findOne(new 
        Query(Criteria.where("id").is(currentUser.getId())), Account.class));
        return accounts;
    }
    
    // ROLE_ADMIN: all.
    // ROLE_CM:    all.
    // ROLE_USER:  own.
    @Override
    public List<AccountId> listIds() {
        MongoUserDetails currentUser = customUserDetailsService.loadCurrentUser();
        List<Account> accounts = new ArrayList<>();
        if (currentUser.isAdmin() || currentUser.isContentManager()) {
            accounts = mongoTemplateUserDB.findAll(Account.class);
        } else {
            accounts.add(mongoTemplateUserDB.findOne(
                    new Query(Criteria.where("id").is(currentUser.getId())), Account.class));
        }
        // Obtain a list of AccountId objects from the Account objects
        List<AccountId> account_ids = new ArrayList<>();
        for (Account account : accounts) {
            AccountId account_id = new AccountId();
            account_id.setId(account.getId());
            account_id.setUsername(account.getUsername());
            account_ids.add(account_id);
        }
        return account_ids;
    }

    // ROLE_ADMIN: ok.
    // ROLE_CM:    none.
    // ROLE_USER:  none.
    @Override
    public Account add(Account account) {
        MongoUserDetails currentUser = customUserDetailsService.loadCurrentUser();
        if (!currentUser.isAdmin()) {
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
    public boolean update(Account account) {
        MongoUserDetails currentUser = customUserDetailsService.loadCurrentUser();
        if (currentUser.isAdmin() || currentUser.getId().equals(account.getId())) {
            mongoTemplateUserDB.save(account);
            logger.info("Updated account " + account.getId() + " to MongoDB.");
            return true;
        }
        return false;
    }

    // ROLE_ADMIN: ok.
    // ROLE_CM:    none.
    // ROLE_USER:  none.
    @Override
    public boolean delete(String id) {
        MongoUserDetails currentUser = customUserDetailsService.loadCurrentUser();
        Account acc_id = find(id);
        if (currentUser.isAdmin() && acc_id != null) {
            mongoTemplateUserDB.remove(acc_id);
            logger.info("Deleted account " + id + " from MongoDB.");
            return true;
        }
        return false;
    }
    
    // ROLE_ADMIN: all.
    // ROLE_CM:    all
    // ROLE_USER:  own.
    @Override
    public List<AccountId> findIdsByDataset(String datasetId) {
        List<DatasetInfo> dsis = mongoTemplateUserDB.find(
                new Query(Criteria.where("dataset_id").is(datasetId)), DatasetInfo.class);
        if (dsis == null) {
            return null;
        }
        // Get the DatasetInfo objects for the given dataset and store the account ids
        Set<String> strs = new HashSet<>();
        MongoUserDetails currentUser = customUserDetailsService.loadCurrentUser();
        final boolean isAdmin = currentUser.isAdmin();
        final boolean isCM = currentUser.isContentManager();
        for (DatasetInfo dsi : dsis) {
            if (isAdmin || isCM || dsi.getAccount_id().equals(currentUser.getId())) {
                strs.add(dsi.getAccount_id());
            }
        } 
        // Get the Account objects
        List<Account> accounts = mongoTemplateUserDB.find(
                new Query(Criteria.where("id").in(strs)), Account.class);
        if (accounts == null) {
            return null;
        }
        // Obtain a list of AccountId objects from the Account objects
        List<AccountId> account_ids = new ArrayList<>();
        for (Account account : accounts) {
            AccountId account_id = new AccountId();
            account_id.setId(account.getId());
            account_id.setUsername(account.getUsername());
            account_ids.add(account_id);
        }
        return account_ids;
    }

}
