package com.spatialtranscriptomics.file;

import com.mongodb.gridfs.GridFSFile;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

/**
 * Created by henriktreadup on 10/1/15.
 */
public class GridfsFile implements File {

    GridFSFile gridFSFile;

    public GridfsFile(GridFSFile gridFSFile) {
        this.gridFSFile = gridFSFile;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        throw new NotImplementedException();
    }

    @Override
    public String getFilename() {
        return gridFSFile.getFilename();
    }

    @Override
    public Date getUploadDate() {
        return this.gridFSFile.getUploadDate();
    }

    @Override
    public String getEtag() {
        return this.gridFSFile.getMD5();
    }
}
