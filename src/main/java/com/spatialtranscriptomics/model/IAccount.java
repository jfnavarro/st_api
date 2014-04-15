/*
 * Copyright (C) 2012 Spatial Transcriptomics AB
 * Read LICENSE for more information about licensing terms
 * Contact: Jose Fernandez Navarro <jose.fernandez.navarro@scilifelab.se>
 */

package com.spatialtranscriptomics.model;

import java.util.Date;

/**
 * This interface defines the Account model. Applications that use the API must implement the same model.
 */

public interface IAccount {

	public String getId();

	public void setId(String id);
	
	public String getUsername();

	public void setUsername(String username);
	
	public String getPassword();

	public void setPassword(String password);
	
	public String getRole();

	public void setRole(String role);
	
	public boolean isEnabled();

	public void setEnabled(boolean enabled);
	
	public String getInstitution();

	public void setInstitution(String institution);
	
	public String getFirst_name();

	public void setFirst_name(String firstName);
	
	public String getLast_name();

	public void setLast_name(String lastName);
	
	public String getStreet_address();

	public void setStreet_address(String streetAddress);

	public String getCity();

	public void setCity(String city);

	public String getPostcode();

	public void setPostcode(String postcode);

	public String getCountry();

	public void setCountry(String country);

	public Date getLast_modified();

	public void setLast_modified(Date lastModified);	
		
}
