package com.spatialtranscriptomics.model;

import java.util.Date;

/**
 * This interface defines the ImageAlignment model. Applications that use the API must implement the same model.
 */
public interface IImageAlignment {

	public String getId();

	public void setId(String id);
	
	public String getName();
	
	public void setName(String name);
	
	public String getChip_id();
	
	public void setChip_id(String id);
	
	public String getFigure_red();
	
	public void setFigure_red(String fig);
	
	public String getFigure_blue();
	
	public void setFigure_blue(String fig);
	
	public double[] getAlignment_matrix();
	
	public void setAlignment_matrix(double[] arr);
	
	public Date getLast_modified();
	
	public void setLast_modified(Date d);
}

