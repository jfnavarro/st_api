package com.st.model;

/**
 * This interface defines the S3Resource model. Applications that use the API
 * must implement the same model.
 */
public interface IS3Resource {
    
    public String getContentType();
    
    public void setContentType(String type);
    
    public String getContentEncoding();
    
    public void setContentEncoding(String encoding);
    
    public String getFilename();
    
    public void setFilename(String filename);
    
    public byte[] getFile();
    
    public void setFile(byte[] file);
    
    public long getSize();
    
    public void setSize(long size);
}
