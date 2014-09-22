///*
// * Copyright (C) 2012 Spatial Transcriptomics AB
// * Read LICENSE for more information about licensing terms
// * Contact: Jose Fernandez Navarro <jose.fernandez.navarro@scilifelab.se>
// */
//
//package com.spatialtranscriptomics.model;
//
//import org.springframework.data.annotation.Id;
//import org.springframework.data.mongodb.core.mapping.Document;
//
//
///**
// * This class maps the Hit data model object into a MongoDB Document.
// * An instance of this class holds the number of hits of a specific
// * biological entity (e.g. a gene) in a particular array location
// * ("feature" or "well", etc.).
// * <p/>
// * We use the @Document annotation of Spring Data for the mapping.
// * We also do data validation using Hibernate validator constraints.
// */
//
//@Document
//public class Hit implements IHit {
//
//	@Id
//	String id;
//	
//	int hits;
//	
//	public String getId() {
//		return id;
//	}
//
//	/**
//	 * id is set automatically by MongoDB
//	 */
//	public void setId(String id) {
//		this.id = id;
//	}
//	
//	public int getHits() {
//		return hits;
//	}
//
//	public void setHits(int hits) {
//		this.hits = hits;
//	}
//}

