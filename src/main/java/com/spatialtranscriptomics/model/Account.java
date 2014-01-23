/*
 * Copyright (C) 2012 Spatial Transcriptomics AB
 * Read LICENSE for more information about licensing terms
 * Contact: Jose Fernandez Navarro <jose.fernandez.navarro@scilifelab.se>
 */

package com.spatialtranscriptomics.model;

import java.util.List;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


/**
 * This class maps the Account data model object into a MongoDB Document
 * We use the @Document annotation of Spring Data for the mapping.
 * We also do data validation using Hibernate validator constraints.
 * 
 * */

@Document
public class Account implements IAccount {

	@Id
	public String id;

	@NotBlank
	public String username;

	@NotBlank
	public String role;

	@Length(min = 4, message = "Password must have at least 4 characters.")
	public String password;

	public List<String> grantedDatasets;
	public boolean enabled;

	// id is set automatically by MongoDB
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUsername() {
		return this.username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getRole() {
		return this.role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public List<String> getGrantedDatasets() {
		return this.grantedDatasets;
	}

	public void setGrantedDatasets(List<String> grantedDatasets) {
		this.grantedDatasets = grantedDatasets;
	}

	public boolean isEnabled() {
		return this.enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

}
