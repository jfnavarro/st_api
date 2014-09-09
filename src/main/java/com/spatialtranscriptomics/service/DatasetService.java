/*
 * Copyright (C) 2012 Spatial Transcriptomics AB
 * Read LICENSE for more information about licensing terms
 * Contact: Jose Fernandez Navarro <jose.fernandez.navarro@scilifelab.se>
 */

package com.spatialtranscriptomics.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.spatialtranscriptomics.model.Dataset;

/**
 * Interface for the dataset service.
 */
@Service
public interface DatasetService {

	public Dataset find(String id);

	public Dataset findByName(String name);
	
	public List<Dataset> findByAccount(String accountId);

	public Dataset add(Dataset ds);

	public List<Dataset> list();

	public void update(Dataset ds);

	public void delete(String id);
        
        public void clearAccountCreator(String accountId);

        public void setUnabledForImageAlignment(String imalId);
}
