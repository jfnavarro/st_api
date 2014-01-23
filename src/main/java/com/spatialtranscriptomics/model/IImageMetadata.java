/*
 * Copyright (C) 2012 Spatial Transcriptomics AB
 * Read LICENSE for more information about licensing terms
 * Contact: Jose Fernandez Navarro <jose.fernandez.navarro@scilifelab.se>
 */

package com.spatialtranscriptomics.model;

import java.util.Date;

/**
 * This interface defines the ImageMetadata model. Applications that use the API must implement the same model.
 */

public interface IImageMetadata {

	public String getFilename();

	public Date getLastModified();

}
