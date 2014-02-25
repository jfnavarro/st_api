/*
 * Copyright (C) 2012 Spatial Transcriptomics AB
 * Read LICENSE for more information about licensing terms
 * Contact: Jose Fernandez Navarro <jose.fernandez.navarro@scilifelab.se>
 */

package com.spatialtranscriptomics.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.spatialtranscriptomics.model.Selection;

@Service
public interface SelectionService {

	public Selection find(String id);
	
	public Selection findByName(String name);

	public List<Selection> list();

	public Selection add(Selection sel);

	public void update(Selection sel);

	public void delete(String id);

}
