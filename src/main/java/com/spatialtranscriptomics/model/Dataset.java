/*
 * Copyright (C) 2012 Spatial Transcriptomics AB
 * Read LICENSE for more information about licensing terms
 * Contact: Jose Fernandez Navarro <jose.fernandez.navarro@scilifelab.se>
 */

package com.spatialtranscriptomics.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.validator.constraints.NotBlank;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.spatialtranscriptomics.controller.AccountController;
import com.spatialtranscriptomics.controller.DatasetInfoController;
import com.spatialtranscriptomics.serviceImpl.AccountServiceImpl;
import com.spatialtranscriptomics.serviceImpl.DatasetInfoServiceImpl;


/**
 * This class maps the Dataset data model object into a MongoDB Document
 * We use the @Document annotation of Spring Data for the mapping.
 * We also do data validation using Hibernate validator constraints.
 * 
 */
@Document(collection="dataset")
public class Dataset implements IDataset{

	@Id
	String id;
	
	@Indexed(unique = true)
	@NotBlank(message = "Name must not be blank.")
	String name;
	
	@NotBlank(message = "Tissue must not be blank.")
	String tissue;
	
	@NotBlank(message = "Species must not be blank.")
	String species;
	
	String image_alignment_id;
	
	
	int overall_gene_count;
	int unique_gene_count;
	int overall_barcode_count;
	int unique_barcode_count;
	int overall_hit_count;
	double[] overall_hit_quartiles;
	double[] gene_pooled_hit_quartiles;
	String[] obo_foundry_terms;
	String comment;
	
	@Transient
	public List<String> granted_accounts;
	
	public String getId() {
		return id;
	}
	
	// id is set automatically by MongoDB
	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getImage_alignment_id() {
		return this.image_alignment_id;
	}

	public void setImage_alignment_id(String imal) {
		this.image_alignment_id = imal;
	}

	public String getTissue() {
		return this.tissue;
	}

	public void setTissue(String tissue) {
		this.tissue = tissue;
	}

	public String getSpecies() {
		return this.species;
	}

	public void setSpecies(String species) {
		this.species = species;
	}

	public int getOverall_gene_count() {
		return overall_gene_count;
	}

	public void setOverall_gene_count(int count) {
		this.overall_gene_count = count;
	}

	public int getUnique_gene_count() {
		return this.unique_gene_count;
	}

	public void setUnique_gene_count(int count) {
		this.unique_gene_count = count;
	}

	public int getOverall_barcode_count() {
		return overall_barcode_count;
	}

	public void setOverall_barcode_count(int count) {
		this.overall_barcode_count = count;
	}

	public int getUnique_barcode_count() {
		return this.unique_barcode_count;
	}

	public void setUnique_barcode_count(int count) {
		this.unique_barcode_count = count;
	}

	public int getOverall_hit_count() {
		return this.overall_hit_count;
	}

	public void setOverall_hit_count(int count) {
		this.overall_hit_count = count;
	}

	public double[] getOverall_hit_quartiles() {
		return this.overall_hit_quartiles;
	}

	public void setOverall_hit_quartiles(double[] quartiles) {
		this.overall_hit_quartiles = quartiles;
	}

	public double[] getGene_pooled_hit_quartiles() {
		return this.gene_pooled_hit_quartiles;
	}

	public void setGene_pooled_hit_quartiles(double[] quartiles) {
		this.gene_pooled_hit_quartiles = quartiles;
	}

		
	public String[] getObo_foundry_terms() {
		return obo_foundry_terms;
	}

	public void setObo_foundry_terms(String[] obo_foundry_terms) {
		this.obo_foundry_terms = obo_foundry_terms;
	}

	public String getComment() {
		return this.comment;
	}

	public void setComment(String comm) {
		this.comment = comm;
	}
	
	
	@Transient
	public List<String> getGranted_accounts() {
		if (this.id == null) { return null; }
		AccountServiceImpl service = AccountController.getStaticAccountService();
		List<Account> alreadyInThere = service.findByDataset(this.id);
		if (alreadyInThere == null) { return null; }
		granted_accounts = new ArrayList<String>(alreadyInThere.size());
		for (Account d : alreadyInThere) {
			granted_accounts.add(d.getId());
		}
		return granted_accounts;
	}

	@Transient
	public void setGranted_accounts(List<String> grantedAccounts) {
		this.granted_accounts = grantedAccounts;
		DatasetInfoServiceImpl datasetinfoService = DatasetInfoController.getStaticDatasetInfoService();
		// Remove existing accounts.
		List<DatasetInfo> dsis = datasetinfoService.findByDataset(this.id);
		for (DatasetInfo dsi : dsis) {
			datasetinfoService.delete(dsi.getId());
		}
		if (grantedAccounts == null) { return; }
		for (String did : grantedAccounts) {
			try {
				Date d = new Date();
				datasetinfoService.add(new DatasetInfo(did, this.id, "Created " + d.toString()));
			} catch (Exception e) {
			}
		}
	}
}
