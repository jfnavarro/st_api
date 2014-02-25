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
 * This class maps the PipelineExperiment data model object into a MongoDB Document
 * We use the @Document annotation of Spring Data for the mapping.
 * We also do data validation using Hibernate validator constraints.
 * 
 * */

@Document
public class PipelineExperiment implements IPipelineExperiment {

	@Id
	String id;
	
	@NotBlank(message = "Name must not be blank.")
	String name;
	
	String emr_jobflow_id;
	String account_id;
	Date last_modified;

	// id is set automatically by MongoDB
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getEmr_jobflow_id() {
		return this.emr_jobflow_id;
	}

	public void setEmr_jobflow_id(String emrJobflowId) {
		this.emr_jobflow_id = emrJobflowId;
		
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
		
	}

	public String getAccount_id() {
		return account_id;
	}

	public void setAccount_id(String id) {
		this.account_id = id;
	}

	public Date getLast_modified() {
		return this.last_modified;
	}

	public void setLast_modified(Date date) {
		this.last_modified = date;
	}

	
}
