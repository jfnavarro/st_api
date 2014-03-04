/*
 * Copyright (C) 2012 Spatial Transcriptomics AB
 * Read LICENSE for more information about licensing terms
 * Contact: Jose Fernandez Navarro <jose.fernandez.navarro@scilifelab.se>
 */

package com.spatialtranscriptomics.serviceImpl;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.spatialtranscriptomics.model.Account;
import com.spatialtranscriptomics.model.DatasetInfo;
import com.spatialtranscriptomics.service.AccountService;

/**
 * This class implements the store/retrieve logic to MongoDB for the data model class "Account".
 * The DB connection is handled in a MongoOperations object, which is configured in mvc-dispather-servlet.xml
 */

@Service
public class AccountServiceImpl implements AccountService {

	private static final Logger logger = Logger
			.getLogger(AccountServiceImpl.class);

	@Autowired
	MongoOperations mongoTemplateUserDB;

	public Account find(String id) {
		return mongoTemplateUserDB.findOne(
				new Query(Criteria.where("id").is(id)), Account.class);
	}

	public Account findByUsername(String username) {
		return mongoTemplateUserDB.findOne(new Query(Criteria.where("username").is(username)), Account.class);
	}

	public List<Account> list() {
		return mongoTemplateUserDB.findAll(Account.class);
	}

	public Account add(Account account) {
		logger.debug("Adding account");
		mongoTemplateUserDB.insert(account);
		return account;
	}

	public void update(Account account) {
		logger.debug("Updating account " + account.getId());
		mongoTemplateUserDB.save(account);

	}

	public void delete(String id) {
		logger.debug("Removing account " + id);
		mongoTemplateUserDB.remove(find(id));
	}

	public List<Account> findByDataset(String datasetId) {
		List<DatasetInfo> dsis = mongoTemplateUserDB.find(new Query(Criteria.where("dataset_id").is(datasetId)), DatasetInfo.class);
		if (dsis == null) { return null; }
		List<String> strs = new ArrayList<String>(dsis.size());
		for (DatasetInfo dsi : dsis) {
			strs.add(dsi.getAccount_id());
		}
		return mongoTemplateUserDB.find(new Query(Criteria.where("id").in(strs)), Account.class);
	}

}
