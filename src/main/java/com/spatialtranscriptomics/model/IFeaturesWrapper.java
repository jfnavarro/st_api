/*
 * Copyright (C) 2012 Spatial Transcriptomics AB
 * Read LICENSE for more information about licensing terms
 * Contact: Jose Fernandez Navarro <jose.fernandez.navarro@scilifelab.se>
 */

package com.spatialtranscriptomics.model;

/**
 * This interface defines the FeaturesWrapper model. Applications that use the API
 * must implement the same model.
 */
public interface IFeaturesWrapper {
    
    public String getDatasetId();
    
    public void setDatasetId(String id);
    
    public byte[] getFile();
    
    public void setFile(byte[] file);
    
    public long getSize();
    
    public void setSize(long size);
}
