/*
 * Copyright (C) 2012 Spatial Transcriptomics AB
 * Read LICENSE for more information about licensing terms
 * Contact: Jose Fernandez Navarro <jose.fernandez.navarro@scilifelab.se>
 */

package com.spatialtranscriptomics.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.spatialtranscriptomics.model.ImageAlignment;

@Service
public interface ImageAlignmentService {

	public ImageAlignment find(String id);
	
	public ImageAlignment findByName(String name);
	
	public List<ImageAlignment> findByChip(String chipId);

	public List<ImageAlignment> list();

	public ImageAlignment add(ImageAlignment imal);

	public void update(ImageAlignment imal);

	public void delete(String id);

}
