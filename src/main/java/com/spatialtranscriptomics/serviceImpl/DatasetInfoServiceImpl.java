package com.spatialtranscriptomics.serviceImpl;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.spatialtranscriptomics.model.DatasetInfo;
import com.spatialtranscriptomics.service.DatasetInfoService;

@Service
public class DatasetInfoServiceImpl implements DatasetInfoService {

	private static final Logger logger = Logger
			.getLogger(DatasetInfoServiceImpl.class);
	
	@Autowired
	MongoOperations mongoTemplateUserDB;
	
	public DatasetInfo find(String id) {
		return mongoTemplateUserDB.findOne(new Query(Criteria.where("id").is(id)), DatasetInfo.class);
	}
	
	public List<DatasetInfo> findByAccount(String accountId) {
		return mongoTemplateUserDB.find(new Query(Criteria.where("account_id").is(accountId)), DatasetInfo.class);
	}
	
	public List<DatasetInfo> findByDataset(String datasetId) {
		return mongoTemplateUserDB.find(new Query(Criteria.where("dataset_id").is(datasetId)), DatasetInfo.class);
	}

	public List<DatasetInfo> list() {
		return mongoTemplateUserDB.findAll(DatasetInfo.class);
	}

	public DatasetInfo add(DatasetInfo dsi) {
		logger.info("Adding datasetinfo");
		mongoTemplateUserDB.insert(dsi);
		return dsi;
	}

	public void update(DatasetInfo dsi) {
		logger.info("Updating datasetino " + dsi.getId());
		mongoTemplateUserDB.save(dsi);
	}

	public void delete(String id) {
		mongoTemplateUserDB.remove(find(id));
	}

	
	
}
