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
 * This class maps the Experiment data model object into a MongoDB Document
 * We use the @Document annotation of Spring Data for the mapping.
 * We also do data validation using Hibernate validator constraints.
 * 
 * */

@Document
public class Experiment implements IExperiment {

	@Id
	String id;
	
	@NotBlank(message = "Name must not be blank.")
	String name;
	
	String emr_jobflow_id;
	Date created;

	// id is set automatically by MongoDB
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getEmr_Jobflow_id() {
		return this.emr_jobflow_id;
	}

	public void setEmr_Jobflow_id(String emrJobflowId) {
		this.emr_jobflow_id = emrJobflowId;
		
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
		
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
		
	}

	
}
