package com.spatialtranscriptomics.serviceImpl;

import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSFile;
import com.spatialtranscriptomics.service.GridFSService;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsCriteria;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * The GridFSService is used to perform CRUD operations on files in GridFS.
 */
public class GridFSServiceImpl implements GridFSService {

    // This should be injected with the specific version that you want to use.
    // In other words here is where you decide which database should be used
    // by this GridFSServiceImpl instance.
    private GridFsTemplate mongoGridFsTemplate;

    // Have two classes of methods.
    // One class that takes byte arrays and one class that takes InputStreams.

    /**
     * Stores a file in GridFS. Can be used to create or update a file in GridFS.
     * @param inputStream The input stream that should be read and stored in GridFS.
     * @param filename The filename that the file should be stored under.
     * @return
     */
    @Override
    public GridFSFile storeFile(InputStream inputStream, String filename) {
        return mongoGridFsTemplate.store(inputStream, filename);
    }

    /**
     * Stores a file in GridFS. Can be used to create or update a file in GridFS.
     * @param contents The byte contents that should be stored in GridFS.
     * @param filename The filename that the file should be stored under.
     * @return
     */
    @Override
    public GridFSFile storeFile(byte[] contents, String filename) {   // Perhaps have the bucket we want to put things in.
        InputStream inputStream = new BufferedInputStream(new ByteArrayInputStream(contents));

        return storeFile(inputStream, filename);
    }

    /**
     * Creates a query matching a GridFS file with the given filename.
     * @param filename
     * @return
     */
    private Query getFilenameQuery(String filename) {
        return new Query(GridFsCriteria.whereFilename().is(filename));
    }

    /**
     * Gets a file from GridFS for the given filename.
     * @param filename
     * @return
     */
    @Override
    public GridFSDBFile getFile(String filename) {
        return mongoGridFsTemplate.findOne(getFilenameQuery(filename));
    }

    /**
     * Delets a file from GridFS.
     * @param filename The filename of the file that should be deleted.
     */
    @Override
    public void deleteFile(String filename) {
        mongoGridFsTemplate.delete(getFilenameQuery(filename));
    }

    public void setMongoGridFsTemplate(GridFsTemplate mongoGridFsTemplate) {
        this.mongoGridFsTemplate = mongoGridFsTemplate;
    }
}
