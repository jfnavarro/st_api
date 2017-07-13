package com.st.service;

import com.st.model.FileMetadata;
import java.io.InputStream;
import java.util.List;

/**
 * Interface for the features service.
 */
public interface FileService {
    
    /**
     * Returns all files' metadata.
     * @return metadata list.
     */
    public List<FileMetadata> listMetadata();
    
    /**
     * Returns a file metadata.
     * @param id the file ID.
     * @return the metadata.
     */
    public FileMetadata getMetadata(String id);
    
    /**
     * Adds or updates a file.
     * @param id the file ID.
     * @param gzipfile the file, gzipped in BASE64-encoding.
     * @return true if file was updated; false if added.
     */
    public boolean addUpdate(String id, byte[] gzipfile);
    
    /**
     * Finds a file.
     * @param id the file ID.
     * @return the file gzipped, as an input stream or null.
     */
    public InputStream find(String id);
    
    /**
     * Deletes a file.
     * @param id the file ID.
     * @return 
     */
    public boolean delete(String id);
}
