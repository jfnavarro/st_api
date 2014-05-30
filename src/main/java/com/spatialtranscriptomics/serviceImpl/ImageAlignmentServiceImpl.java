/*
 * Copyright (C) 2012 Spatial Transcriptomics AB
 * Read LICENSE for more information about licensing terms
 * Contact: Jose Fernandez Navarro <jose.fernandez.navarro@scilifelab.se>
 */

package com.spatialtranscriptomics.serviceImpl;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.spatialtranscriptomics.model.ImageAlignment;
import com.spatialtranscriptomics.service.ImageAlignmentService;

/**
 * This class implements the store/retrieve logic to MongoDB for the data model class "ImageAlignment".
 * The DB connection is handled in a MongoOperations object, which is configured in mvc-dispatcher-servlet.xml
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
	public ImageAlignment find(String id) {
		return mongoTemplateAnalysisDB.findOne(new Query(Criteria.where("id").is(id)), ImageAlignment.class);
	}
	
	// ROLE_ADMIN: ok.
	// ROLE_CM:    ok.
	// ROLE_USER:  ok.
	public ImageAlignment findByName(String name) {
		return mongoTemplateAnalysisDB.findOne(new Query(Criteria.where("name").is(name)), ImageAlignment.class);
	}

	// ROLE_ADMIN: ok.
	// ROLE_CM:    ok.
	// ROLE_USER:  ok.
	public List<ImageAlignment> findByChip(String chipId) {
		//System.out.println("Finding for chip");
		List<ImageAlignment> imals = mongoTemplateAnalysisDB.find(new Query(Criteria.where("chip_id").is(chipId)), ImageAlignment.class);
		//System.out.println("Found " + (imals == null ? 0 :  imals.size()));
		return imals;
	}

	// ROLE_ADMIN: ok.
	// ROLE_CM:    ok.
	// ROLE_USER:  nope.
	public List<ImageAlignment> list() {
		return mongoTemplateAnalysisDB.findAll(ImageAlignment.class);
	}

	// ROLE_ADMIN: ok.
	// ROLE_CM:    ok.
	// ROLE_USER:  nope.
	public ImageAlignment add(ImageAlignment imal) {
		logger.info("Adding ImageAlignment");
		mongoTemplateAnalysisDB.insert(imal);
		return imal;
	}

	// ROLE_ADMIN: ok.
	// ROLE_CM:    ok.
	// ROLE_USER:  nope.
	public void update(ImageAlignment imal) {
		logger.info("Updating imagealignment " + imal.getId());
		mongoTemplateAnalysisDB.save(imal);
	}

	// ROLE_ADMIN: ok.
	// ROLE_CM:    ok.
	// ROLE_USER:  nope.
	public void delete(String id) {
		logger.info("Deleting imagealignment " + id);
		mongoTemplateAnalysisDB.remove(find(id));
	}

}
