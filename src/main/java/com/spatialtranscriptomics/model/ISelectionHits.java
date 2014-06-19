package com.spatialtranscriptomics.model;

public interface ISelectionHits {
	
	public String getGene();

	public void setGene(String gene);

	public int getHit_count();

	public void setHit_count(int hit_count);

	public double getNormalized_hit_count();

	public void setNormalized_hit_count(double normalized_hit_count);

	public double getNormalized_pixel_intensity();

	public void setNormalized_pixel_intensity(double normalized_pixel_intensity);
}
