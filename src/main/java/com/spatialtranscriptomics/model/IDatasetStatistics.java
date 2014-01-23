/*
 * Copyright (C) 2012 Spatial Transcriptomics AB
 * Read LICENSE for more information about licensing terms
 * Contact: Jose Fernandez Navarro <jose.fernandez.navarro@scilifelab.se>
 */

package com.spatialtranscriptomics.model;

/**
 * This interface defines the DatasetStatistics model. Applications that use the API must implement the same model.
 */

public interface IDatasetStatistics {

	/** Bean getter. */
	public String getDatasetId();
	
	/** Bean getter. */
	public int[] getHits();
	
	/** Bean getter. */
	public int[] getPooledHits();
	
	/** Bean getter. */
	public double[] getHitsQuartiles();
	
	/** Bean getter. */
	public double[] getPooledHitsQuartiles();
	
	/** Bean getter. */
	public int getHitsSum();
	
	
	/** Bean setter. */
	public void setDatasetId(String id);
	
	/** Bean setter. */
	public void setHits(int[] ia);
	
	/** Bean setter. */
	public void setPooledHits(int[] ia);
	
	/** Bean setter. */
	public void setHitsQuartiles(double[] da);
	
	/** Bean setter. */
	public void setPooledHitsQuartiles(double[] da);
	
	/** Bean setter. */
	public void setHitsSum(int i);
	
}
