/*
 * Copyright (C) 2012 Spatial Transcriptomics AB
 * Read LICENSE for more information about licensing terms
 * Contact: Jose Fernandez Navarro <jose.fernandez.navarro@scilifelab.se>
 */
package com.spatialtranscriptomics.serviceImpl;

import com.spatialtranscriptomics.model.ImageAlignment;
import com.spatialtranscriptomics.model.MongoUserDetails;
import com.spatialtranscriptomics.service.ImageAlignmentService;
import java.util.List;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

/**
 * This class implements the store/retrieve logic to MongoDB for the data model
 * class "ImageAlignment". The DB connection is handled in a MongoOperations
 * object, which is configured in mvc-dispatcher-servlet.xml
 */
@Service
public class ImageAlignmentServiceImpl implements ImageAlignmentService {

    private static final Logger logger = Logger.getLogger(ImageAlignmentServiceImpl.class);

    @Autowired
    MongoUserDetailsServiceImpl customUserDetailsService;

    @Autowired
    MongoOperations mongoTemplateAnalysisDB;

    // ROLE_ADMIN: ok.
    // ROLE_CM:    ok.
    // ROLE_USER:  ok.
    @Override
    public ImageAlignment find(String id) {
        return mongoTemplateAnalysisDB.findOne(new Query(Criteria.where("id").is(id)), ImageAlignment.class);
    }

    // ROLE_ADMIN: ok.
    // ROLE_CM:    ok.
    // ROLE_USER:  ok.
    @Override
    public ImageAlignment findByName(String name) {
        return mongoTemplateAnalysisDB.findOne(new Query(Criteria.where("name").is(name)), ImageAlignment.class);
    }

    // ROLE_ADMIN: ok.
    // ROLE_CM:    ok.
    // ROLE_USER:  ok.
    @Override
    public List<ImageAlignment> findByChip(String chipId) {
        //System.out.println("Finding for chip");
        List<ImageAlignment> imals = mongoTemplateAnalysisDB.find(new Query(Criteria.where("chip_id").is(chipId)), ImageAlignment.class);
        //System.out.println("Found " + (imals == null ? 0 :  imals.size()));
        return imals;
    }

    // ROLE_ADMIN: ok.
    // ROLE_CM:    ok.
    // ROLE_USER:  nope.
    @Override
    public List<ImageAlignment> list() {
        return mongoTemplateAnalysisDB.findAll(ImageAlignment.class);
    }

    // ROLE_ADMIN: ok.
    // ROLE_CM:    ok.
    // ROLE_USER:  nope.
    @Override
    public ImageAlignment add(ImageAlignment imal) {
        logger.info("Adding ImageAlignment");
        mongoTemplateAnalysisDB.insert(imal);
        return imal;
    }

    // ROLE_ADMIN: ok.
    // ROLE_CM:    ok.
    // ROLE_USER:  nope.
    @Override
    public void update(ImageAlignment imal) {
        logger.info("Updating imagealignment " + imal.getId());
        mongoTemplateAnalysisDB.save(imal);
    }

    // ROLE_ADMIN: ok.
    // ROLE_CM:    ok.
    // ROLE_USER:  nope.
    @Override
    public void delete(String id) {
        logger.info("Deleting imagealignment " + id);
        mongoTemplateAnalysisDB.remove(find(id));
    }

    @Override
    public boolean deleteIsOK(String id) {
        MongoUserDetails currentUser = customUserDetailsService.loadCurrentUser();
        return (currentUser.isAdmin() || currentUser.isContentManager()) && find(id) != null;
    }

    @Override
    public List<ImageAlignment> deleteForChip(String chipId) {
        //System.out.println("about to delete chip");
        List<ImageAlignment> imals = findByChip(chipId);
        //System.out.println("imal size" + (imals == null ? 0 : imals.size()));
        if (imals == null) {
            return null;
        }
        for (ImageAlignment imal : imals) {
            delete(imal.getId());
        }
        return imals;
    }

}
