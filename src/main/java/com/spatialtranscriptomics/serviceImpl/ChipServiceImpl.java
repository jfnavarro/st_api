/*
 * Copyright (C) 2012 Spatial Transcriptomics AB
 * Read LICENSE for more information about licensing terms
 * Contact: Jose Fernandez Navarro <jose.fernandez.navarro@scilifelab.se>
 */
package com.spatialtranscriptomics.serviceImpl;

import com.amazonaws.AmazonClientException;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.spatialtranscriptomics.model.Chip;
import com.spatialtranscriptomics.model.MongoUserDetails;
import com.spatialtranscriptomics.service.ChipService;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import org.springframework.beans.factory.annotation.Value;

/**
 * This class implements the store/retrieve logic to MongoDB for the data model
 * class "Chip". The DB connection is handled in a MongoOperations object, which
 * is configured in mvc-dispather-servlet.xml
 */

@Service
public class ChipServiceImpl implements ChipService {

    private static final Logger logger = Logger.getLogger(ChipServiceImpl.class);

    @Autowired
    MongoUserDetailsServiceImpl customUserDetailsService;

    @Autowired
    MongoOperations mongoTemplateAnalysisDB;

    @Autowired
    AmazonS3Client s3Client;
        
    private @Value("${s3.pipelinebucket}")
    String chipsBucket;
    
    private @Value("${s3.idspath}")
    String chipsPath;
        
    // ROLE_ADMIN: ok.
    // ROLE_CM:    ok.
    // ROLE_USER:  ok.
    @Override
    public Chip find(String id) {
        return mongoTemplateAnalysisDB.findOne(
                new Query(Criteria.where("id").is(id)), Chip.class);
    }

    // ROLE_ADMIN: ok.
    // ROLE_CM:    ok.
    // ROLE_USER:  ok.
    @Override
    public Chip findByName(String name) {
        return mongoTemplateAnalysisDB.findOne(
                new Query(Criteria.where("name").is(name)), Chip.class);
    }

    // ROLE_ADMIN: ok.
    // ROLE_CM:    ok.
    // ROLE_USER:  nope.
    @Override
    public List<Chip> list() {
        return mongoTemplateAnalysisDB.findAll(Chip.class);
    }

    // ROLE_ADMIN: ok.
    // ROLE_CM:    ok.
    // ROLE_USER:  nope.
    @Override
    public Chip add(Chip chip) {
        mongoTemplateAnalysisDB.insert(chip);
        //TODO: add logic to add the file to S3
        logger.info("Added chip " + chip.getId() + " to DB.");
        return chip;
    }

    // ROLE_ADMIN: ok.
    // ROLE_CM:    ok.
    // ROLE_USER:  nope.
    @Override
    public void addFileToS3(String id, byte[] chipFile) {
        try {
            ObjectMetadata om = new ObjectMetadata();
            om.setContentType("application/text");
            InputStream is = new ByteArrayInputStream(chipFile);
            s3Client.putObject(chipsBucket + chipsPath, id, is, om);
            logger.info("Added chip file " + id + " to Amazon S3");
        } catch(AmazonClientException e) {
            logger.info("Error adding chip file " + id + " to Amazon S3.", e);
        }
    }
    
    // ROLE_ADMIN: ok.
    // ROLE_CM:    ok.
    // ROLE_USER:  nope.
    @Override
    public void update(Chip chip) {
        mongoTemplateAnalysisDB.save(chip);
        logger.info("Updated chip " + chip.getId() + " in DB.");
    }

    // ROLE_ADMIN: ok.
    // ROLE_CM:    ok.
    // ROLE_USER:  nope.
    // See deleteIsOkForCurrUser(). Internal use may be different
    @Override
    public void delete(String id) {
        try {
            // first remove chip
            mongoTemplateAnalysisDB.remove(find(id));
            logger.info("Deleted chip " + id + " from DB.");
            //then try to remove the file from S3
            s3Client.deleteObject(chipsBucket + chipsPath, id);
            logger.info("Deleted chip file " + id + " from DB.");
        } catch(AmazonClientException e) {
            logger.info("Error deleting chip " + id + " from Amazon S3.", e);
            throw e;
        }
    }

    // ROLE_ADMIN: ok.
    // ROLE_CM:    ok.
    // ROLE_USER:  nope.
    @Override
    public boolean deleteIsOkForCurrUser(String id) {
        MongoUserDetails currentUser = customUserDetailsService.loadCurrentUser();
        return (currentUser.isAdmin() || currentUser.isContentManager()) && find(id) != null;
    }
}
