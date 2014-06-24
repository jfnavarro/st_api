/*
 * Copyright (C) 2012 Spatial Transcriptomics AB
 * Read LICENSE for more information about licensing terms
 * Contact: Jose Fernandez Navarro <jose.fernandez.navarro@scilifelab.se>
 */

package com.spatialtranscriptomics.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.spatialtranscriptomics.model.PipelineExperiment;

/**
 * Interface for the pipelineexperiment service.
 */
@Service
public interface PipelineExperimentService {

	public PipelineExperiment find(String id);
	
	public PipelineExperiment findByName(String name);

	public List<PipelineExperiment> findByAccount(String accountId);
	
	public List<PipelineExperiment> list();

	public PipelineExperiment add(PipelineExperiment experiment);

	public void update(PipelineExperiment experiment);

	public void delete(String id);

}
