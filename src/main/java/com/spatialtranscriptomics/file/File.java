package com.spatialtranscriptomics.file;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

/**
 * The File interface is an abstraction of a File object. It is returned by the FileService.
 * Each different type of FileService implementation will have its own File implementation.
 */
public interface File {

    /**
     * Gets the files input stream.
     * @return
     * @throws IOException
     */
    InputStream getInputStream() throws IOException;

    /**
     * Gets the filename of the file.
     * @return
     */
    String getFilename();

    /**
     * The date this file was uploaded to the file store.
     * @return
     */
    Date getUploadDate();

    /**
     * A string derived from the file that is appropriate to use as an Etag.
     * @return
     */
    String getEtag();

    /**
     * The content type of the file.
     * @return
     */
    String getContentType();

    /**
     * The length of the file in bytes.
     * @return
     */
    long getLength();
}
