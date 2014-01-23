/*
 * Copyright (C) 2012 Spatial Transcriptomics AB
 * Read LICENSE for more information about licensing terms
 * Contact: Jose Fernandez Navarro <jose.fernandez.navarro@scilifelab.se>
 */

package com.spatialtranscriptomics.model;

/**
 * This interface defines the Hit model. Applications that use the API must implement the same model.
 */
public interface IHit {

	public String getId();

	public void setId(String id);

	public int getHits();

	public void setHits(int hits);

}
