/*
 * Copyright (C) 2012 Spatial Transcriptomics AB
 * Read LICENSE for more information about licensing terms
 * Contact: Jose Fernandez Navarro <jose.fernandez.navarro@scilifelab.se>
 */

package com.spatialtranscriptomics.model;

import java.util.Date;

/**
 * This interface defines the Dataset model. Applications that use the API must implement the same model.
 */

public interface IDataset {

	public String getId();

	public void setId(String id);

	public String getChipid();

	public void setChipid(String chipid);

	public String getFigure_blue();

	public void setFigure_blue(String figure_blue);

	public String getFigure_red();

	public void setFigure_red(String figure_red);

	public int getFigure_status();

	public void setFigure_status(int figure_status);

	public double[] getAlignment_matrix();

	public void setAlignment_matrix(double[] alignment_matrix);

	public String getName();

	public void setName(String name);

	public int getStat_barcodes();

	public void setStat_barcodes(int stat_barcodes);

	public int getStat_genes();

	public void setStat_genes(int stat_genes);

	public int getStat_unique_barcodes();

	public void setStat_unique_barcodes(int stat_unique_barcodes);

	public int getStat_unique_genes();

	public void setStat_unique_genes(int stat_unique_genes);

	public String getStat_tissue();

	public void setStat_tissue(String stat_tissue);

	public String getStat_specie();

	public void setStat_specie(String stat_specie);

	public String getStat_comments();

	public void setStat_comments(String stat_comments);

	public Date getStat_created();

	public void setStat_created(Date stat_created);
}
