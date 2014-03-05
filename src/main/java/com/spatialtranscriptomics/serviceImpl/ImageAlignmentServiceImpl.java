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

	private static final Logger logger = Logger
			.getLogger(ImageAlignmentServiceImpl.class);

	@Autowired
	MongoUserDetailsServiceImpl customUserDetailsService;

	@Autowired
	MongoOperations mongoTemplateAnalysisDB;

	public ImageAlignment find(String id) {
		return mongoTemplateAnalysisDB.findOne(new Query(Criteria.where("id").is(id)), ImageAlignment.class);
	}
	
	public ImageAlignment findByName(String name) {
		return mongoTemplateAnalysisDB.findOne(new Query(Criteria.where("name").is(name)), ImageAlignment.class);
	}

	public List<ImageAlignment> findByChip(String chipId) {
		return mongoTemplateAnalysisDB.find(new Query(Criteria.where("chip_id").is(chipId)), ImageAlignment.class);
	}

	public List<ImageAlignment> list() {
		return mongoTemplateAnalysisDB.findAll(ImageAlignment.class);
	}

	public ImageAlignment add(ImageAlignment imal) {
		logger.info("Adding ImageAlignment");
		mongoTemplateAnalysisDB.insert(imal);
		return imal;
	}

	public void update(ImageAlignment imal) {
		logger.info("Updating imagealignment " + imal.getId());
		mongoTemplateAnalysisDB.save(imal);
	}

	public void delete(String id) {
		logger.info("Deleting imagealignment " + id);
		mongoTemplateAnalysisDB.remove(find(id));
	}

}
