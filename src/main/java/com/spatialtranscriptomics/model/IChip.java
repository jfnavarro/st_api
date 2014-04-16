/*
 * Copyright (C) 2012 Spatial Transcriptomics AB
 * Read LICENSE for more information about licensing terms
 * Contact: Jose Fernandez Navarro <jose.fernandez.navarro@scilifelab.se>
 */

package com.spatialtranscriptomics.model;

/**
 * This interface defines the Chip model. Applications that use the API must implement the same model.
 */
public interface IChip {

	public String getId();

	public void setId(String id);

	public int getBarcodes();

	public void setBarcodes(int barcodes);

	public String getName();

	public void setName(String name);

	public int getX1();

	public void setX1(int x1);

	public int getX1_border();

	public void setX1_border(int x1_border);

	public int getX1_total();

	public void setX1_total(int x1_total);

	public int getX2();

	public void setX2(int x2);

	public int getX2_border();

	public void setX2_border(int x2_border);

	public int getX2_total();

	public void setX2_total(int x2_total);

	public int getY1();

	public void setY1(int y1);

	public int getY1_border();

	public void setY1_border(int y1_border);

	public int getY1_total();

	public void setY1_total(int y1_total);

	public int getY2();

	public void setY2(int y2);

	public int getY2_border();

	public void setY2_border(int y2_border);

	public int getY2_total();

	public void setY2_total(int y2_total);
	
}
