/*
 * Copyright (C) 2012 Spatial Transcriptomics AB
 * Read LICENSE for more information about licensing terms
 * Contact: Jose Fernandez Navarro <jose.fernandez.navarro@scilifelab.se>
 */

package com.spatialtranscriptomics.model;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;


/**
 * This class implements the DatasetStatistics object.
 * Given a Dataset, it computes various
 * statistical metrics, both with respect to the overall individual hits,
 * and for the set resulting when hits lying in the same
 * array location have been pooled together.
 * <p/>
 * DatasetStatistics doesn't map to a Mongo Document. Instead, it is being instantiated in
 * DatasetStatisticsController.class.
 */
public class DatasetStatistics implements IDatasetStatistics {

	String datasetId;

	/** Individual hits, sorted in increasing size. */
	int[] hits;

	/** Pooled hits, grouped by same array location, sorted in increasing size. */
	int[] pooledHits;
	
	/** Hit quartiles. */
	double[] hitsQuartiles;
	
	/** Pooled hit quartiles. */
	double[] pooledHitsQuartiles;
	
	/** Sum */
	int hitsSum;
	
	/**
	 * 
	 * @param datasetId
	 * @param features
	 */
	public DatasetStatistics(String datasetId, List<Feature> features) {
		this.datasetId = datasetId;
		
		this.hits = new int[features.size()];
		LinkedHashMap<String, Integer> pooled = new LinkedHashMap<String, Integer>(500 * 500);
		
		// Collect hits from features.
		int i = 0;
		hitsSum = 0;
		for (Feature f : features) {
			this.hits[i++] = f.hits;
			String xy = "(" + f.x + "," + f.y + ')';
			Integer h = pooled.get(xy);
			if (h == null) {
				pooled.put(xy, f.hits);
			} else {
				pooled.put(xy, h + f.hits);
			}
			hitsSum += f.hits;
		}
		
		// Transform pooled hits int int-array.
		this.pooledHits = new int[pooled.size()];
		Iterator<Integer> it = pooled.values().iterator();
		i = 0;
		while (it.hasNext()) {
			pooledHits[i++] = it.next();
		}
		
		// Sort hits.
		Arrays.sort(hits);
		Arrays.sort(pooledHits);
		
		// Compute quartiles.
		this.hitsQuartiles = computeQuartiles(this.hits);
		this.pooledHitsQuartiles = computeQuartiles(this.pooledHits);
	}

	/**
	 * Computes the quartiles of a sorted list.
	 * @param hits the sorted list.
	 * @return the quartile boundaries.
	 */
	private double[] computeQuartiles(int[] hits) {
		if (hits.length == 1) {
			return new double[] { hits[0], hits[0], hits[0], hits[0], hits[0] };
		}
		
		double[] q = new double[5];
		
		// Linear interpolation for intermediate values, exact at endpoints.
		q[0] = hits[0];
		q[4] = hits[hits.length - 1];
		double[] idx = { 0.25*hits.length - 0.25, 0.5*hits.length - 0.5, 0.75*hits.length - 0.75 };
		for (int i = 0; i < idx.length; ++i) {
			int floor = (int) Math.floor(idx[i]);
			int ceil = (int) Math.ceil(idx[i]);
			double delta = idx[i] - floor;
			q[i + 1] = hits[floor] * (1.0 - delta) + hits[ceil] * delta; // No prob if ceil==floor...
		}
		return q;
	}

	public String getDatasetId() {
		return datasetId;
	}
	
	public double[] getHitsQuartiles() {
		return this.hitsQuartiles;
	}
	
	public double[] getPooledHitsQuartiles() {
		return this.pooledHitsQuartiles;
	}

	public int getHitsSum() {
		return this.hitsSum;
	}

	
	public int[] getHits() {
		return this.hits;
	}

	public int[] getPooledHits() {
		return this.pooledHits;
	}

	public void setDatasetId(String id) {
		this.datasetId = id;
	}
	
	public void setHits(int[] ia) {
		this.hits = ia;
	}
		
	public void setPooledHits(int[] ia) {
		this.pooledHits = ia;
	}

	public void setHitsQuartiles(double[] da) {
		this.hitsQuartiles = da;
	}

	public void setPooledHitsQuartiles(double[] da) {
		this.pooledHitsQuartiles = da;
	}

	public void setHitsSum(int i) {
		this.hitsSum = i;
	}
	
	
}
