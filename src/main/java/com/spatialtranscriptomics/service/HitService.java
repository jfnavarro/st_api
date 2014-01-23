/*
 * Copyright (C) 2012 Spatial Transcriptomics AB
 * Read LICENSE for more information about licensing terms
 * Contact: Jose Fernandez Navarro <jose.fernandez.navarro@scilifelab.se>
 */

package com.spatialtranscriptomics.service;

import java.util.List;
import org.springframework.stereotype.Service;

import com.spatialtranscriptomics.model.Hit;

@Service
public interface HitService {

	public List<Hit> findByDatasetId(String datasetId);

}
