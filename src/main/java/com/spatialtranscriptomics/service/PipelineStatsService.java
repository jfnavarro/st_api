/*
 * Copyright (C) 2012 Spatial Transcriptomics AB
 * Read LICENSE for more information about licensing terms
 * Contact: Jose Fernandez Navarro <jose.fernandez.navarro@scilifelab.se>
 */

package com.spatialtranscriptomics.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.spatialtranscriptomics.model.PipelineStats;

@Service
public interface PipelineStatsService {

	public PipelineStats find(String id);
	
	public PipelineStats findByExperiment(String experimentId);

	public List<PipelineStats> list();

	public PipelineStats add(PipelineStats stats);

	public void update(PipelineStats stats);

	public void delete(String id);

}
