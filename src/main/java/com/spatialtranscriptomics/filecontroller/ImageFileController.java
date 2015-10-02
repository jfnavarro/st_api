package com.spatialtranscriptomics.filecontroller;

import com.spatialtranscriptomics.exceptions.CustomBadRequestException;
import com.spatialtranscriptomics.model.ImageAlignment;
import com.spatialtranscriptomics.service.ImageAlignmentService;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Created by henriktreadup on 10/1/15.
 */
// For now use a request mapping that does not collide with the old one.
// Change this once the old controllers have been removed.
@Controller
@RequestMapping(value="/rest/files/images")
public class ImageFileController extends FileController {

    @Autowired
    private ImageAlignmentService imageAlignmentService;

    /**
     * GET /files/images/{filename}
     *
     * Gets an image file.
     *
     * HEAD /files/images/{filename}
     *
     * Gets the headers for a image file.
     *
     * @param filename The filename of the image file.
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
     * PUT /files/images/{filename}
     *
     * Stores an image file for the given file name. Creates a new file if no file exists. Updates an existing
     * file if there is already a file there.
     *
     * @param filename
     * @param requestInputStream
     * @throws IOException
     */
    @Override
    @Secured({"ROLE_CM", "ROLE_ADMIN"})
    @RequestMapping(value= "{filename:.+}", method = {RequestMethod.PUT})
    public ResponseEntity<String> storeFile(@PathVariable String filename, InputStream requestInputStream,
                          @RequestHeader(value="Content-Type") String contentType) throws IOException {

        IOUtils.toByteArray(requestInputStream);
        return super.storeFile(filename, requestInputStream, contentType);
    }

    /**
     * DELETE /file/image/{filename}
     *
     * Deletes an image.
     *
     * @param filename The filename of the image.
     */
    @Override
    @Secured({"ROLE_CM", "ROLE_ADMIN"})
    @RequestMapping(value= "{filename:.+}", method = RequestMethod.DELETE)
    public ResponseEntity<String> deleteFile(@PathVariable String filename) {
        return super.deleteFile(filename);
    }

    @Override
    protected void validateDelete(String filename) {

        List<ImageAlignment> imals = imageAlignmentService.list();
        for (ImageAlignment ia : imals) {
            if (ia.getFigure_blue().equals(filename) || ia.getFigure_red().equals(filename)) {
                getLog().info("Trying to delete an image " + filename + " that is used");
                throw new CustomBadRequestException("Trying to delete an image " + filename
                        + "that used in an Image Alignment object");
            }
        }
    }

    @Override
    protected String getEntityName() {
        return ENTITY_NAME;
    }

    private static final String ENTITY_NAME = "image";

    private static final String[] VALID_IMAGE_CONTENT_TYPES = {"image/jpeg", "image/png"};

    public ImageFileController() {
        setValidContentTypes(VALID_IMAGE_CONTENT_TYPES);
    }
}