/*
 * Copyright (C) 2012 Spatial Transcriptomics AB
 * Read LICENSE for more information about licensing terms
 * Contact: Jose Fernandez Navarro <jose.fernandez.navarro@scilifelab.se>
 */

package com.spatialtranscriptomics.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.spatialtranscriptomics.model.Experiment;

@Service
public interface ExperimentService {

	public Experiment find(String id);
	
	public Experiment findByName(String name);

	public List<Experiment> list();

	public Experiment add(Experiment experiment);

	public void update(Experiment experiment);

	public void delete(String id);

}
