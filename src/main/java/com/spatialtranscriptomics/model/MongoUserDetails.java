/*
 * Copyright (C) 2012 Spatial Transcriptomics AB
 * Read LICENSE for more information about licensing terms
 * Contact: Jose Fernandez Navarro <jose.fernandez.navarro@scilifelab.se>
 */

package com.spatialtranscriptomics.model;

import java.util.ArrayList;
import java.util.Collection;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * This class maps the Spring Security interface UserDetails.class to a MongoDB document
 * It contains information required for authentication and authorization by Spring Security
 * We use the @Document annotation of Spring Data for the mapping.
 * There are some fields required by Spring Security interface UserDetails.class that we don't
 * want to store in our MongoDB. These fields are annotated as @Transient. 
 * See also class MongoUserDetailsServiceImpl.
 * 
 */

@Document
public class MongoUserDetails extends Account implements UserDetails {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6756025876358143006L;

	
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
