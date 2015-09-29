package com.spatialtranscriptomics.service;

import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSFile;

import java.io.InputStream;

/**
 * The GridFSService is used to perform CRUD operations on files in GridFS.
 */
public interface GridFSService {

    /**
     * Stores a file in GridFS. Can be used to create or update a file in GridFS.
     *
     * @param inputStream The input stream that should be read and stored in GridFS.
     * @param filename    The filename that the file should be stored under.
     * @return
     */
    GridFSFile storeFile(InputStream inputStream, String filename);

    /**
     * Stores a file in GridFS. Can be used to create or update a file in GridFS.
     *
     * @param contents The byte contents that should be stored in GridFS.
     * @param filename The filename that the file should be stored under.
     * @return
     */
    GridFSFile storeFile(byte[] contents, String filename);

    /**
     * Gets a file from GridFS for the given filename.
     * @param filename
     * @return
     */
    GridFSDBFile getFile(String filename);

    /**
     * Delets a file from GridFS.
     * @param filename The filename of the file that should be deleted.
     */
    void deleteFile(String filename);

}

