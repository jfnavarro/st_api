/*
 * Copyright (C) 2012 Spatial Transcriptomics AB
 * Read LICENSE for more information about licensing terms
 * Contact: Jose Fernandez Navarro <jose.fernandez.navarro@scilifelab.se>
 */

package com.spatialtranscriptomics.model;

/**
 * This interface defines the VersionSupportInfo model. Applications that use the API must implement the same model.
 */

public interface IVersionSupportInfo {

	public String getMinSupportedClientVersion();

	public void setMinSupportedClientVersion(String version);

}
