package com.st.model;

import org.joda.time.DateTime;

/**
 * This class implements the FileMetadata object.
 * It derives its contents from Amazon S3.
 * 
 * Features metadata holds properties of a file stored on Amazon S3.
 */
public class FileMetadata implements IFileMetadata {

    String datasetId;
    String filename;
    DateTime lastModified;
    DateTime created;
    long size;       // No. of bytes in file
    
    /**
     * Default constructor is needed by Jackson, in
     * case other constructors are added.
     */
    public FileMetadata() {}

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
