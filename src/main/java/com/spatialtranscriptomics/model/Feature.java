/*
 * Copyright (C) 2012 Spatial Transcriptomics AB
 * Read LICENSE for more information about licensing terms
 * Contact: Jose Fernandez Navarro <jose.fernandez.navarro@scilifelab.se>
 */

package com.spatialtranscriptomics.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;



/**
 * This class maps the Feature data model object into a MongoDB Document.
 * IMPORTANT NOTE: An instance of this class does not merely represent
 * an individual chip grid location. Rather, it represents a unique gene
 * in an individual chip grid location.
 * <p/>
 * We use the @Document annotation of Spring Data for the mapping.
 * We also do data validation using Hibernate validator constraints.
 */

@Document
public class Feature implements IFeature {

	@Id
	String id;
	
	String barcode;

	String gene;
	
	int hits;
	
	int x;
	
	int y;
	
	String annotation;
	
	public String getId() {
		return id;
	}

	// id is set automatically by MongoDB
	public void setId(String id) {
		this.id = id;
	}

	public String getBarcode() {
		return barcode;
	}

	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}

	public int getHits() {
		return hits;
	}

	public void setHits(int hits) {
		this.hits = hits;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public String getGene() {
		return this.gene;
	}

	public void setGene(String gene) {
		this.gene = gene;
	}

	public String getAnnotation() {
		return this.annotation;
	}

	public void setAnnotation(String ann) {
		this.annotation = ann;
	}

}
