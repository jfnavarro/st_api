package com.spatialtranscriptomics.filecontroller;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by henriktreadup on 10/1/15.
 */
// For now use a request mapping that does not collide with the old one.
// Change this once the old controllers have been removed.
@Controller
@RequestMapping(value="/rest/files/features")
public class FeatureFileController extends FileController {

    /**
     * GET /files/features/{filename}
     *
     * Gets a feature file.
     *
     * HEAD /files/features/{filename}
     *
     * Gets the headers for a feature file.
     *
     * @param filename The filename of the feature file.
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
     * PUT /files/features/{filename}
     *
     * Stores a chip file for the given file name. Creates a new file if no file exists. Updates an existing
     * file if there is already a file there.
     *
     * @param filename
     * @param requestInputStream
     * @parem contentType
     * @throws IOException
     */
    @Override
    @Secured({"ROLE_CM", "ROLE_ADMIN"})
    @RequestMapping(value= "{filename:.+}", method = {RequestMethod.PUT})
    public ResponseEntity<String> storeFile(@PathVariable String filename, InputStream requestInputStream,
                          @RequestHeader(value = "Content-Type") String contentType) throws IOException {
        return super.storeFile(filename, requestInputStream, contentType);
    }

    /**
     * DELETE /file/features/{filename}
     *
     * Deletes an feature file.
     *
     * @param filename The filename of the feature file.
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

    private static final String ENTITY_NAME = "feature";

    private static final String[] VALID_FEATURE_CONTENT_TYPES = {"application/octet-stream"};

    public FeatureFileController() {
        setValidContentTypes(VALID_FEATURE_CONTENT_TYPES);
    }
}
