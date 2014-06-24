/*
 * Copyright (C) 2012 Spatial Transcriptomics AB
 * Read LICENSE for more information about licensing terms
 * Contact: Jose Fernandez Navarro <jose.fernandez.navarro@scilifelab.se>
 */

package com.spatialtranscriptomics.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.spatialtranscriptomics.model.Feature;

/**
 * Interface for the feature service.
 */
@Service
public interface FeatureService {

	public List<Feature> addAll(List<Feature> features, String datasetId);

	public void deleteAll(String datasetId);

	public List<Feature> findByDatasetId(String datasetId);

	public List<Feature> findByGene(String datasetId, String gene);

//	public List<Feature> findBy2DCoords(String datasetId, int x1, int y1,
//			int x2, int y2);

}
