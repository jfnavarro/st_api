package com.spatialtranscriptomics.filecontroller;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by henriktreadup on 10/1/15.
 */
// For now use a request mapping that does not collide with the old one.
// Change this once the old controllers have been removed.
@Controller
@RequestMapping(value="/rest/files/chips")
public class ChipFileController extends FileController {

    /**
     * GET /files/chips/{filename}
     *
     * Gets a chip file.
     *
     * HEAD /files/chips/{filename}
     *
     * Gets the headers for a file.
     *
     * @param filename The filename of the chip file.
     */
    @Override
    @Secured({"ROLE_CM", "ROLE_USER", "ROLE_ADMIN"})
    @RequestMapping(value= "{filename:.+}", method = {RequestMethod.GET, RequestMethod.HEAD})
    public ResponseEntity<InputStreamResource> getFile(@PathVariable String filename,
                                                       @RequestHeader(value="If-None-Match", required = false)
                                                       String requestIfNoneMatchHeader,
                                                       @RequestHeader(value="Accept", required = false)
                                                       String requestAcceptHeader,
                                                       @RequestHeader(value="If-Modified-Since", required = false)
                                                           String requestIfModifiedSinceHeader) throws IOException {
        return super.getFile(filename, requestIfNoneMatchHeader, requestAcceptHeader, requestIfModifiedSinceHeader);
    }

    /**
     * PUT /files/chips/{filename}
     *
     * Stores a chip file for the given file name. Creates a new file if no file exists. Updates an existing
     * file if there is already a file there.
     *
     * @param filename
     * @param requestInputStream
     * @param contentType
     * @throws IOException
     */
    @Override
    @Secured({"ROLE_CM", "ROLE_ADMIN"})
    @RequestMapping(value= "{filename:.+}", method = {RequestMethod.PUT})
    public ResponseEntity<String> storeFile(@PathVariable String filename, InputStream requestInputStream,
                          @RequestHeader(value="Content-Type") String contentType) throws IOException {
        return super.storeFile(filename, requestInputStream, contentType);
    }

    /**
     * DELETE /files/chips/{filename}
     *
     * Deletes a chip file.
     *
     * @param filename The filename of the chip file.
     */
    @Override
    @Secured({"ROLE_CM", "ROLE_ADMIN"})
    @RequestMapping(value= "{filename:.+}", method = RequestMethod.DELETE)
    public ResponseEntity<String> deleteFile(@PathVariable String filename) {
        return super.deleteFile(filename);
    }

    @Override
    protected void validateDelete(String filename) {
        // TODO: Add implementation.
    }

    @Override
    protected String getEntityName() {
        return ENTITY_NAME;
    }

    private static final String ENTITY_NAME = "chip";
    private static final String[] VALID_CHIP_CONTENT_TYPES = {"application/octet-stream"};

    public ChipFileController() {
        setValidContentTypes(VALID_CHIP_CONTENT_TYPES);
    }
}