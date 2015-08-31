/*
 * Copyright (C) 2012 Spatial Transcriptomics AB
 * Read LICENSE for more information about licensing terms
 * Contact: Jose Fernandez Navarro <jose.fernandez.navarro@scilifelab.se>
 */
package com.spatialtranscriptomics.serviceImpl;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.spatialtranscriptomics.model.MongoUserDetails;

/**
 * This class implements the store/retrieve logic to MongoDB for the data model
 * class "MongoUserDetails". The DB connection is handled in a MongoOperations
 * object, which is configured in mvc-dispather-servlet.xml See also class
 * MongoUserDetails.
 */
@Service
public class MongoUserDetailsServiceImpl implements UserDetailsService {

    private static final Logger logger = Logger
            .getLogger(MongoUserDetailsServiceImpl.class);

    @Autowired
    MongoOperations mongoTemplateUserDB;

    private final String DB_COLLECTION_NAME = "account";

    private boolean isProperlyLoaded = false;

    @Override
    public MongoUserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {

        isProperlyLoaded = false;
        MongoUserDetails result =
                mongoTemplateUserDB.findOne(new Query(Criteria.where("username").is(username)), 
                        MongoUserDetails.class, DB_COLLECTION_NAME);
        
        if (result == null) {
            logger.info("Failed loading user " + username);
            throw new UsernameNotFoundException(username);
        } else {
            isProperlyLoaded = true;
        }
        
        return result;
    }

    public MongoUserDetails loadCurrentUser() {
        Authentication a = SecurityContextHolder.getContext().getAuthentication();
        return loadUserByUsername(a.getName());
    }

    public boolean isProperlyLoaded() {
        return isProperlyLoaded;
    }

}
