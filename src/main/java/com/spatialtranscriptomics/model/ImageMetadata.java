/*
 * Copyright (C) 2012 Spatial Transcriptomics AB
 * Read LICENSE for more information about licensing terms
 * Contact: Jose Fernandez Navarro <jose.fernandez.navarro@scilifelab.se>
 */
package com.spatialtranscriptomics.model;

import org.joda.time.DateTime;

/**
 * This class implements the ImageMetadata object. It derives its properties
 * from Amazon S3.
 *
 * Image metadata holds characterstics of a tissue microscopy image obtained
 * in an exeperiment. Images are stored on Amazon S3.
 */
public class ImageMetadata implements IImageMetadata {

    String imageType;
    String filename;
    DateTime lastModified;
    DateTime created;
    long size;       // No of bytes of file.

    /**
     * Default constructor is needed by Jackson, in
     * case other constructors are added.
     */
    public ImageMetadata() {}
    
    @Override
    public String getImageType() {
        return this.imageType;
    }

    @Override
    public void setImageType(String imageType) {
        this.imageType = imageType;
    }
    
    @Override
    public String getFilename() {
        return this.filename;
    }

    @Override
    public void setFilename(String filename) {
        this.filename = filename;
    }

    @Override
    public DateTime getLastModified() {
        return this.lastModified;
    }

    @Override
    public void setLastModified(DateTime lastModified) {
        this.lastModified = lastModified;
    }

    @Override
    public DateTime getCreated() {
        return this.created;
    }

    @Override
    public void setCreated(DateTime d) {
        this.created = d;
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
