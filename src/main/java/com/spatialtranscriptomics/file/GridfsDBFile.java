package com.spatialtranscriptomics.file;

import com.mongodb.gridfs.GridFSDBFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

/**
 * Created by henriktreadup on 10/1/15.
 */
public class GridfsDBFile implements File {

    private GridFSDBFile gridFSDBFile;

    public GridfsDBFile(GridFSDBFile gridFSDBFile) {
        this.gridFSDBFile = gridFSDBFile;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return this.gridFSDBFile.getInputStream();
    }

    @Override
    public String getFilename() {
        return gridFSDBFile.getFilename();
    }

    @Override
    public Date getUploadDate() {
        return this.gridFSDBFile.getUploadDate();
    }

    @Override
    public String getEtag() {
        return this.gridFSDBFile.getMD5();
    }

    @Override
    public String getContentType() {
        return this.gridFSDBFile.getContentType();
    }

    @Override
    public long getLength() {
        return this.gridFSDBFile.getLength();
    }
}
