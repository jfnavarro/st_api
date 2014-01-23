/*
 * Copyright (C) 2012 Spatial Transcriptomics AB
 * Read LICENSE for more information about licensing terms
 * Contact: Jose Fernandez Navarro <jose.fernandez.navarro@scilifelab.se>
 */

package com.spatialtranscriptomics.model;

import java.util.Date;

import org.hibernate.validator.constraints.NotBlank;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


/**
 * This class maps the Dataset data model object into a MongoDB Document
 * We use the @Document annotation of Spring Data for the mapping.
 * We also do data validation using Hibernate validator constraints.
 * 
 * */

@Document
public class Dataset implements IDataset{

	@Id
	String id;
	
	@NotBlank(message = "Name must not be blank.")
	String name;
	
	String chipid;
	String figure_blue;
	String figure_red;
	int figure_status;
	double[] alignment_matrix;
	int stat_barcodes;
	int stat_genes;
	int stat_unique_barcodes;
	int stat_unique_genes;
	String stat_tissue;
	String stat_specie;
	String stat_comments;
	Date stat_created;	
	
	public String getId() {
		return id;
	}
	
	// id is set automatically by MongoDB
	public void setId(String id) {
		this.id = id;
	}

	public String getChipid() {
		return chipid;
	}
	public void setChipid(String chipid) {
		this.chipid = chipid;
	}
	public String getFigure_blue() {
		return figure_blue;
	}
	public void setFigure_blue(String figure_blue) {
		this.figure_blue = figure_blue;
	}
	public String getFigure_red() {
		return figure_red;
	}
	public void setFigure_red(String figure_red) {
		this.figure_red = figure_red;
	}
	public int getFigure_status() {
		return figure_status;
	}
	public void setFigure_status(int figure_status) {
		this.figure_status = figure_status;
	}
	
	public double[] getAlignment_matrix(){
		return alignment_matrix;
	}
	public void setAlignment_matrix(double[] alignment_matrix){
		this.alignment_matrix = alignment_matrix;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public int getStat_barcodes() {
		return this.stat_barcodes;
	}

	public void setStat_barcodes(int stat_barcodes) {
		this.stat_barcodes = stat_barcodes;
	}

	public int getStat_genes() {
		return this.stat_genes;
	}

	public void setStat_genes(int stat_genes) {
		this.stat_genes = stat_genes;
	}

	public int getStat_unique_barcodes() {
		return this.stat_unique_barcodes;
	}

	public void setStat_unique_barcodes(int stat_unique_barcodes) {
		this.stat_unique_barcodes = stat_unique_barcodes;
	}

	public int getStat_unique_genes() {
		return this.stat_unique_genes;
	}

	public void setStat_unique_genes(int stat_unique_genes) {
		this.stat_unique_genes = stat_unique_genes;
	}

	public String getStat_tissue() {
		return this.stat_tissue;
	}

	public void setStat_tissue(String stat_tissue) {
		this.stat_tissue = stat_tissue;
	}

	public String getStat_specie() {
		return this.stat_specie;
	}

	public void setStat_specie(String stat_specie) {
		this.stat_specie = stat_specie;
	}

	public Date getStat_created() {
		return this.stat_created;
	}

	public void setStat_created(Date stat_created) {
		this.stat_created = stat_created;
	}

	public String getStat_comments() {
		return this.stat_comments;
	}

	public void setStat_comments(String stat_comments) {
		this.stat_comments = stat_comments;
	}

	
}
