/*
 * Copyright (C) 2012 Spatial Transcriptomics AB
 * Read LICENSE for more information about licensing terms
 * Contact: Jose Fernandez Navarro <jose.fernandez.navarro@scilifelab.se>
 */
package com.spatialtranscriptomics.model;

import com.spatialtranscriptomics.util.StringOperations;
import java.util.Date;
import org.joda.time.DateTime;
import org.springframework.data.annotation.Transient;

/**
 * This class implements the FeaturesMetadata object.
 * It derives its contents from Amazon S3.
 * 
 * Features metadata holds properties of a features file stored on Amazon S3.
 */
public class FeaturesMetadata implements IFeaturesMetadata {

    String datasetId;
    String filename;
    DateTime lastModified;
    DateTime created;
    long size;       // No. of bytes in file
    
    /**
     * Default constructor is needed by Jackson, in
     * case other constructors are added.
     */
    public FeaturesMetadata() {}

    @Override
    public String getDatasetId() {
        return this.datasetId;
    }
    
    @Override
    public void setDatasetId(String id) {
        this.datasetId = id;
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
