/*
 * Copyright (C) 2012 Spatial Transcriptomics AB
 * Read LICENSE for more information about licensing terms
 * Contact: Jose Fernandez Navarro <jose.fernandez.navarro@scilifelab.se>
 */

package com.spatialtranscriptomics.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.spatialtranscriptomics.model.Chip;

/**
 * Interface for the chip service.
 */

@Service
public interface ChipService {

	public Chip find(String id);
	
	public Chip findByName(String name);

	public List<Chip> list();

	public Chip add(Chip chip);

	public void update(Chip chip);

	public void delete(String id);

}
