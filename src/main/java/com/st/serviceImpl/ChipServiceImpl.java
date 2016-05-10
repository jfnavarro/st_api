package com.st.serviceImpl;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.st.model.Chip;
import com.st.model.MongoUserDetails;
import com.st.service.ChipService;

/**
 * This class implements the store/retrieve logic to MongoDB for the data model
 * class "Chip". The DB connection is handled in a MongoOperations object, which
 * is configured in mvc-dispather-servlet.xml
 */
@Service
public class ChipServiceImpl implements ChipService {

    private static final Logger logger = Logger
            .getLogger(ChipServiceImpl.class);

    @Autowired
    MongoUserDetailsServiceImpl customUserDetailsService;

    @Autowired
    MongoOperations mongoTemplateAnalysisDB;

    // ROLE_ADMIN: ok.
    // ROLE_CM:    ok.
    // ROLE_USER:  ok.
    @Override
    public Chip find(String id) {
        return mongoTemplateAnalysisDB.findOne(new Query(Criteria.where("id").is(id)), Chip.class);
    }

    // ROLE_ADMIN: ok.
    // ROLE_CM:    ok.
    // ROLE_USER:  ok.
    @Override
    public Chip findByName(String name) {
        return mongoTemplateAnalysisDB.findOne(new Query(Criteria.where("name").is(name)), Chip.class);
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
        logger.info("Added chip " + chip.getId() + " to MongoDB.");
        return chip;
    }

    // ROLE_ADMIN: ok.
    // ROLE_CM:    ok.
    // ROLE_USER:  nope.
    @Override
    public void update(Chip chip) {
        mongoTemplateAnalysisDB.save(chip);
        logger.info("Updated chip " + chip.getId() + " to MongoDB.");
    }

    // See deleteIsOkForCurrUser(). Internal use may be different
    @Override
    public void delete(String id) {
        mongoTemplateAnalysisDB.remove(find(id));
        logger.info("Deleted chip " + id + " from MongoDB.");
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
