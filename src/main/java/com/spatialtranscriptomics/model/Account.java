/*
 * Copyright (C) 2012 Spatial Transcriptomics AB
 * Read LICENSE for more information about licensing terms
 * Contact: Jose Fernandez Navarro <jose.fernandez.navarro@scilifelab.se>
 */

package com.spatialtranscriptomics.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.spatialtranscriptomics.controller.DatasetController;
import com.spatialtranscriptomics.controller.DatasetInfoController;
import com.spatialtranscriptomics.serviceImpl.DatasetInfoServiceImpl;
import com.spatialtranscriptomics.serviceImpl.DatasetServiceImpl;


/**
 * This class maps the Account data model object into a MongoDB Document
 * We use the @Document annotation of Spring Data for the mapping.
 * We also do data validation using Hibernate validator constraints.
 * 
 * */

@Document(collection="account")
public class Account implements IAccount {

	@Id
	public String id;
	
	@Indexed(unique = true)
	@NotBlank
	@Email(message = "Username must be a valid email address.")
	public String username;
	
	@Length(min = 4, message = "Password must have at least 4 characters.")
	public String password;
	
	@NotBlank
	public String role;

	//@NotBlank // not possible...
	public boolean enabled;
	
	public String institution;
	
	public String first_name;
	
	public String last_name;
	
	public String street_address;
	
	public String city;
	
	public String postcode;
	
	public String country;
	
	@Transient
	public List<String> granted_datasets;
	
	// id is set automatically by MongoDB
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public boolean isEnabled() {
		return this.enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
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
	
	public String getUsername() {
		return this.username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
	
	public String getInstitution() {
		return institution;
	}

	public void setInstitution(String institution) {
		this.institution = institution;
	}

	public String getFirst_name() {
		return first_name;
	}

	public void setFirst_name(String first_name) {
		this.first_name = first_name;
	}

	public String getLast_name() {
		return last_name;
	}

	public void setLast_name(String last_name) {
		this.last_name = last_name;
	}

	public String getStreet_address() {
		return street_address;
	}

	public void setStreet_address(String street_address) {
		this.street_address = street_address;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getPostcode() {
		return postcode;
	}

	public void setPostcode(String postcode) {
		this.postcode = postcode;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}
	
	@Transient
	public List<String> getGranted_datasets() {
		if (this.id == null) { return null; }
		DatasetServiceImpl service = DatasetController.getStaticDatasetService();
		List<Dataset> alreadyInThere = service.findByAccount(this.id);
		if (alreadyInThere == null) { return null; }
		granted_datasets = new ArrayList<String>(alreadyInThere.size());
		for (Dataset d : alreadyInThere) {
			granted_datasets.add(d.getId());
		}
		return granted_datasets;
	}

	@Transient
	public void setGranted_datasets(List<String> grantedDatasets) {
		this.granted_datasets = grantedDatasets;
		DatasetInfoServiceImpl datasetinfoService = DatasetInfoController.getStaticDatasetInfoService();
		// Remove existing datasets.
		List<DatasetInfo> dsis = datasetinfoService.findByAccount(this.id);
		for (DatasetInfo dsi : dsis) {
			datasetinfoService.delete(dsi.getId());
		}
		if (grantedDatasets == null) { return; }
		for (String did : grantedDatasets) {
			try {
				Date d = new Date();
				datasetinfoService.add(new DatasetInfo(this.id, did, "Created " + d.toString()));
			} catch (Exception e) {
			}
		}
	}

}
