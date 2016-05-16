package com.st.serviceImpl;

import com.st.model.ImageAlignment;
import com.st.model.MongoUserDetails;
import com.st.service.ImageAlignmentService;
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
        return mongoTemplateAnalysisDB.findOne(new Query(Criteria.where("id").is(id)), 
                ImageAlignment.class);
    }

    // ROLE_ADMIN: ok.
    // ROLE_CM:    ok.
    // ROLE_USER:  ok.
    @Override
    public ImageAlignment findByName(String name) {
        return mongoTemplateAnalysisDB.findOne(new Query(Criteria.where("name").is(name)), 
                ImageAlignment.class);
    }

    // ROLE_ADMIN: ok.
    // ROLE_CM:    ok.
    // ROLE_USER:  ok.
    @Override
    public List<ImageAlignment> findByChip(String chipId) {
        List<ImageAlignment> imals = mongoTemplateAnalysisDB.find(
                new Query(Criteria.where("chip_id").is(chipId)), ImageAlignment.class);
        return imals;
    }

    // ROLE_ADMIN: ok.
    // ROLE_CM:    ok.
    // ROLE_USER:  nope.
    @Override
    public List<ImageAlignment> list() {
        List<ImageAlignment> alignments = null;
        MongoUserDetails currentUser = customUserDetailsService.loadCurrentUser();
        if (currentUser.isAdmin() || currentUser.isContentManager()) {
            alignments =  mongoTemplateAnalysisDB.findAll(ImageAlignment.class);
        }
        return alignments;
    }

    // ROLE_ADMIN: ok.
    // ROLE_CM:    ok.
    // ROLE_USER:  nope.
    @Override
    public ImageAlignment add(ImageAlignment imal) {
        MongoUserDetails currentUser = customUserDetailsService.loadCurrentUser();
        if (currentUser.isAdmin() || currentUser.isContentManager()) {
            mongoTemplateAnalysisDB.insert(imal);
            logger.info("Added image alignment " + imal.getId() + " to MongoDB.");
            return imal;
        } 
        return null;
    }

    // ROLE_ADMIN: ok.
    // ROLE_CM:    ok.
    // ROLE_USER:  nope.
    @Override
    public void update(ImageAlignment imal) {
        MongoUserDetails currentUser = customUserDetailsService.loadCurrentUser();
        if (currentUser.isAdmin() || currentUser.isContentManager()) {
            mongoTemplateAnalysisDB.save(imal);
            logger.info("Updated image alignment " + imal.getId() + " to MongoDB.");
        }
    }

    
    // See deleteIsOkForCurrUser(). Internal use may be different
    @Override
    public void delete(String id) {
        if (deleteIsOkForCurrUser(id)) {
            mongoTemplateAnalysisDB.remove(find(id));
            logger.info("Deleted image alignment " + id + " from MongoDB.");
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

    @Override
    public List<ImageAlignment> deleteForChip(String chipId) {
        List<ImageAlignment> imals = findByChip(chipId);
        if (imals == null) {
            return null;
        }
        for (ImageAlignment imal : imals) {
            delete(imal.getId());
        }
        return imals;
    }

}
