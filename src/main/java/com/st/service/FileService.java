package com.st.service;

import com.st.model.FileMetadata;
import java.io.InputStream;

/**
 * Interface for the features service.
 */
public interface FileService {
    
    /**
     * Returns a file metadata.
     * @param filename the name of the file
     * @param id the dataset ID.
     * @return the metadata.
     */
    public FileMetadata getMetadata(String filename, String id);
    
    /**
     * Adds or updates a file.
     * @param filename the name of the file
     * @param id the dataset ID.
     * @param gzipfile the file, gzipped in BASE64-encoding.
     * @return true if file was updated or added correctly
     */
    public boolean addUpdate(String filename, String id, byte[] gzipfile);
    
    /**
     * Finds a file.
     * @param filename the name of the file
     * @param id the dataset ID.
     * @return the file gzipped, as an input stream or null.
     */
    public InputStream find(String filename, String id);
    
    /**
     * Deletes a file.
     * @param filename the name of the file
     * @param id the dataset ID.
     * @return 
     */
    public boolean delete(String filename, String id);
}
