/*
 * Copyright (C) 2012 Spatial Transcriptomics AB
 * Read LICENSE for more information about licensing terms
 * Contact: Jose Fernandez Navarro <jose.fernandez.navarro@scilifelab.se>
 */
package com.spatialtranscriptomics.model;

import com.spatialtranscriptomics.util.SpatialStringUtils;
import java.util.Date;
import org.joda.time.DateTime;
import org.springframework.data.annotation.Transient;

/**
 * This class implements the ImageMetadata object.
 *
 */
public class ImageMetadata implements IImageMetadata {

    String filename;
    DateTime lastModified;
    DateTime created;
    long size;

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
