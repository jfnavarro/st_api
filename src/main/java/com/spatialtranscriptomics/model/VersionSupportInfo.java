/*
 * Copyright (C) 2012 Spatial Transcriptomics AB
 * Read LICENSE for more information about licensing terms
 * Contact: Jose Fernandez Navarro <jose.fernandez.navarro@scilifelab.se>
 */

package com.spatialtranscriptomics.model;



/**
 * This class implements the VersionSupportInfo object.
 * VersionSupportInfo is not mapped to a MongoDB document. Instead, it is being instantiated in VersionSupportInfoController.class
 * 
 * */
public class VersionSupportInfo implements IVersionSupportInfo {

	String minSupportedClientVersion;

	public String getMinSupportedClientVersion() {
		return this.minSupportedClientVersion;
	}

	public void setMinSupportedClientVersion(String version) {
		this.minSupportedClientVersion = version;

	}

}
