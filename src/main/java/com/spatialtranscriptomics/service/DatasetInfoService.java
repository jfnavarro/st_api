package com.spatialtranscriptomics.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.spatialtranscriptomics.model.DatasetInfo;

@Service
public interface DatasetInfoService {

	public DatasetInfo find(String id);

	public DatasetInfo add(DatasetInfo ds);

	public List<DatasetInfo> list();

	public List<DatasetInfo> findByAccount(String accountId);

	public List<DatasetInfo> findByDataset(String datasetId);
	
	public void update(DatasetInfo dsi);

	public void delete(String id);
	
}
