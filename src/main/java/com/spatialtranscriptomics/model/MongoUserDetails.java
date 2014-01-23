/*
 * Copyright (C) 2012 Spatial Transcriptomics AB
 * Read LICENSE for more information about licensing terms
 * Contact: Jose Fernandez Navarro <jose.fernandez.navarro@scilifelab.se>
 */

package com.spatialtranscriptomics.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * This class maps the Spring Security interface UserDetails.class to a MongoDB document
 * It contains information required for authentication and authorization by Spring Security
 * We use the @Document annotation of Spring Data for the mapping.
 * There are some fields required by Spring Security interface UserDetails.class that we don't want to store in our MongoDB. These fields are annotated as @Transient. 
 * See also class MongoUserDetailsServiceImpl.
 * 
 */

@Document
public class MongoUserDetails implements UserDetails {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6756025876358143006L;

	@Id
	String id;

	String username;
	String password;
	String role;
	List<String> grantedDatasets;
	boolean enabled;

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
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getRole() {
		return this.role;
	}

	public void setRole(String role) { // role must be "ROLE_USER", "ROLE_CM" or
										// "ROLE_ADMIN"
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

	public void isEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	/*
	 * Methods for the Interface
	 * org.springframework.security.core.userdetails.UserDetails. These are not
	 * persisted to the DB
	 */

	@Transient
	public Collection<? extends GrantedAuthority> getAuthorities() {

		Collection<GrantedAuthority> grantedAuthorities = new ArrayList<GrantedAuthority>();
		grantedAuthorities.add(new SimpleGrantedAuthority(getRole()));
		return grantedAuthorities;
	}

	@Transient
	public boolean isAccountNonExpired() {
		return true;
	}

	@Transient
	public boolean isAccountNonLocked() {
		return true;
	}

	@Transient
	public boolean isCredentialsNonExpired() {
		return true;
	}

	/*
	 * Convenience methods for use in ServiceImpl classes. Not persisted to DB
	 */
	
	@Transient
	public boolean isContentManager() {
		return role.equals("ROLE_CM");
	}
	
	@Transient
	public boolean isAdmin() {
		return role.equals("ROLE_ADMIN");
	}

}
