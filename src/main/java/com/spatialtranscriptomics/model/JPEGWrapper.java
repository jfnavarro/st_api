/*
 * Copyright (C) 2012 Spatial Transcriptomics AB
 * Read LICENSE for more information about licensing terms
 * Contact: Jose Fernandez Navarro <jose.fernandez.navarro@scilifelab.se>
 */

package com.spatialtranscriptomics.model;

/**
 * This class wraps a JPEG byte array into JSON.
 */
public class JPEGWrapper implements IJPEGWrapper {
    
    String filename;
    
    byte[] image;
    
    public String getFilename() {
        return filename;
    }
    
    public void setFilename(String filename) {
        this.filename = filename;
    }
    
    public byte[] getImage() {
        return this.image;
    }
    
    public void setImage(byte[] img) {
        this.image = img;
    }
}
