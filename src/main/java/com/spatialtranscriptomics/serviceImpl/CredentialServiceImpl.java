/*
 * Copyright (C) 2012 Spatial Transcriptomics AB
 * Read LICENSE for more information about licensing terms
 * Contact: Jose Fernandez Navarro <jose.fernandez.navarro@scilifelab.se>
 */

//package com.spatialtranscriptomics.serviceImpl;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.mongodb.core.MongoOperations;
//import org.springframework.data.mongodb.core.query.Criteria;
//import org.springframework.data.mongodb.core.query.Query;
//import org.springframework.stereotype.Service;
//
//import com.spatialtranscriptomics.model.Credential;
//import com.spatialtranscriptomics.service.CredentialService;
//
//@Service
//public class CredentialServiceImpl implements CredentialService {
//
//	@Autowired
//	MongoOperations mongoTemplateUser;
//
//	private final String DB_COLLECTION_NAME = "account";
//
//	public Credential findByUsername(String username) {
//		return mongoTemplateUser.findOne(new Query(Criteria.where("username")
//				.is(username)), Credential.class, DB_COLLECTION_NAME);
//	}
//
//	public void update(Credential credential) {
//		mongoTemplateUser.save(credential, DB_COLLECTION_NAME);
//	}
//
//}
