package com.spatialtranscriptomics.model;

import org.codehaus.jackson.annotate.JsonProperty;

public class SelectionHits implements ISelectionHits {

	@JsonProperty(value="g")
	String gene;
	
	@JsonProperty(value="hc")
	int hit_count;
	
	@JsonProperty(value="nhc")
	double normalized_hit_count;
	
	@JsonProperty(value="npi")
	double normalized_pixel_intensity;

	@JsonProperty(value="g")
	public String getGene() {
		return gene;
	}

	@JsonProperty(value="g")
	public void setGene(String gene) {
		this.gene = gene;
	}

	@JsonProperty(value="hc")
	public int getHit_count() {
		return hit_count;
	}

	@JsonProperty(value="hc")
	public void setHit_count(int hit_count) {
		this.hit_count = hit_count;
	}

	@JsonProperty(value="nhc")
	public double getNormalized_hit_count() {
		return normalized_hit_count;
	}

	@JsonProperty(value="nhc")
	public void setNormalized_hit_count(double normalized_hit_count) {
		this.normalized_hit_count = normalized_hit_count;
	}

	@JsonProperty(value="npi")
	public double getNormalized_pixel_intensity() {
		return normalized_pixel_intensity;
	}

	@JsonProperty(value="npi")
	public void setNormalized_pixel_intensity(double normalized_pixel_intensity) {
		this.normalized_pixel_intensity = normalized_pixel_intensity;
	}
	
	
}
