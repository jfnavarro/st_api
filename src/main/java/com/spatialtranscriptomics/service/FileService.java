package com.spatialtranscriptomics.service;

import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSFile;
import com.spatialtranscriptomics.file.File;

import java.io.InputStream;

/**
 * The FileService is used to perform CRUD operations on files. There can be different implementations
 * that use different file stores.
 */
public interface FileService {

    /**
     * Stores a file in the file store. Can be used to create or update a file.
     *
     * @param inputStream The input stream that should be read and stored in the file store.
     * @param filename    The filename that the file should be stored under.
     * @return
     */
    File storeFile(InputStream inputStream, String filename);

    /**
     * Gets a file from the file store for the given filename.
     * @param filename
     * @return
     */
    File getFile(String filename);

    /**
     * Delets a file from the file store.
     * @param filename The filename of the file that should be deleted.
     */
    void deleteFile(String filename);

}

