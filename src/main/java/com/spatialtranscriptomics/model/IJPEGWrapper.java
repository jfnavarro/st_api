/*
 * Copyright (C) 2012 Spatial Transcriptomics AB
 * Read LICENSE for more information about licensing terms
 * Contact: Jose Fernandez Navarro <jose.fernandez.navarro@scilifelab.se>
 */
package com.spatialtranscriptomics.model;

/**
 * This interface defines the IJPEGWrapper model. Applications that use the API
 * must implement the same model.
 */
interface IJPEGWrapper {

    public String getFilename();

    public void setFilename(String filename);

    public byte[] getImage();

    public void setImage(byte[] img);
}
