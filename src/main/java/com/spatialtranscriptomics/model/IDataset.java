/*
 * Copyright (C) 2012 Spatial Transcriptomics AB
 * Read LICENSE for more information about licensing terms
 * Contact: Jose Fernandez Navarro <jose.fernandez.navarro@scilifelab.se>
 */

package com.spatialtranscriptomics.model;

import java.util.List;
import org.joda.time.DateTime;

/**
 * This interface defines the Dataset model. Applications that use the API must implement the same model.
 */
public interface IDataset {

	public String getId();

	public void setId(String id);

	public String getName();

	public void setName(String name);
	
	public String getImage_alignment_id();
	
	public void setImage_alignment_id(String imal);

	public String getTissue();

	public void setTissue(String tissue);

	public String getSpecies();

	public void setSpecies(String species);
	
	public int getOverall_feature_count();

	public void setOverall_feature_count(int count);
	
	public int getOverall_hit_count();
	
	public void setOverall_hit_count(int count);
	
	public int getUnique_gene_count();

	public void setUnique_gene_count(int count);

	public int getUnique_barcode_count();

	public void setUnique_barcode_count(int count);
	
	public double[] getOverall_hit_quartiles();

	public void setOverall_hit_quartiles(double[] quartiles);

	public double[] getGene_pooled_hit_quartiles();

	public void setGene_pooled_hit_quartiles(double[] quartiles);

	public String[] getObo_foundry_terms();

	public void setObo_foundry_terms(String[] obo_foundry_terms);
	
	public String getComment();
	
	public void setComment(String comm);
	
	public boolean getEnabled();
	
	public void setEnabled(boolean b);
	
	public List<String> getGranted_accounts();
	
	public void setGranted_accounts(List<String> grantedAccounts);
	
        public DateTime getCreated_at();

	public void setCreated_at(DateTime created);
	
	public DateTime getLast_modified();
}
