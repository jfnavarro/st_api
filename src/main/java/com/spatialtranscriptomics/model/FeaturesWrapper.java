/*
 * Copyright (C) 2012 Spatial Transcriptomics AB
 * Read LICENSE for more information about licensing terms
 * Contact: Jose Fernandez Navarro <jose.fernandez.navarro@scilifelab.se>
 */

package com.spatialtranscriptomics.model;

/**
 * This class wraps a gzipped Features JSON file byte array into a JSON class for simpler
 * sending over the service.
 */
public class FeaturesWrapper implements IFeaturesWrapper {
    
    String datasetId;
    
    byte[] file;
    
    long size;
    
    // Default constructor must for JSON wrapping.
    public FeaturesWrapper() {}
    
    public FeaturesWrapper(String datasetId, byte[] file) {
        this.datasetId = datasetId;
        this.file = file;
    }
    
    @Override
    public String getDatasetId() {
        return datasetId;
    }
    
    @Override
    public void setDatasetId(String id) {
        this.datasetId = id;
    }
    
    @Override
    public byte[] getFile() {
        return this.file;
    }
    
    @Override
    public void setFile(byte[] file) {
        this.file = file;
    }
    
    @Override
    public long getSize() {
        return this.size;
    }
    
    @Override
    public void setSize(long size) {
        this.size = size;
    }
}
