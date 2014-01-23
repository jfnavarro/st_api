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

import com.spatialtranscriptomics.model.Chip;
import com.spatialtranscriptomics.service.ChipService;

/**
 * This class implements the store/retrieve logic to MongoDB for the data model class "Chip".
 * The DB connection is handled in a MongoOperations object, which is configured in mvc-dispather-servlet.xml
 */

@Service
public class ChipServiceImpl implements ChipService {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger
			.getLogger(ChipServiceImpl.class);

	@Autowired
	MongoUserDetailsServiceImpl customUserDetailsService;

	@Autowired
	MongoOperations mongoTemplateAnalysis;

	public Chip find(String id) {
		return mongoTemplateAnalysis.findOne(
				new Query(Criteria.where("id").is(id)), Chip.class);
	}

	public Chip findByName(String name) {
		return mongoTemplateAnalysis.findOne(new Query(Criteria.where("name")
				.is(name)), Chip.class);
	}

	public List<Chip> list() {

		return mongoTemplateAnalysis.findAll(Chip.class);
	}

	public Chip add(Chip chip) {

		mongoTemplateAnalysis.insert(chip);
		return chip;
	}

	public void update(Chip chip) {

		mongoTemplateAnalysis.save(chip);
	}

	public void delete(String id) {

		mongoTemplateAnalysis.remove(find(id));
	}

}
