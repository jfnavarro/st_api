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

import com.spatialtranscriptomics.model.Account;
import com.spatialtranscriptomics.service.AccountService;

/**
 * This class implements the store/retrieve logic to MongoDB for the data model class "Account".
 * The DB connection is handled in a MongoOperations object, which is configured in mvc-dispather-servlet.xml
 */

@Service
public class AccountServiceImpl implements AccountService {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger
			.getLogger(AccountServiceImpl.class);

	@Autowired
	MongoOperations mongoTemplateUser;


	public Account find(String id) {
		return mongoTemplateUser.findOne(
				new Query(Criteria.where("id").is(id)), Account.class);
	}

	public Account findByName(String username) {
		return mongoTemplateUser.findOne(new Query(Criteria.where("username")
				.is(username)), Account.class);
	}

	public List<Account> list() {
		return mongoTemplateUser.findAll(Account.class);
	}

	public Account add(Account account) {
		mongoTemplateUser.insert(account);
		return account;
	}

	public void update(Account account) {
		mongoTemplateUser.save(account);

	}

	public void delete(String id) {
		mongoTemplateUser.remove(find(id));

	}

}
