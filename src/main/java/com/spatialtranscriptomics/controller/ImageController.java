/*
 * Copyright (C) 2012 Spatial Transcriptomics AB
 * Read LICENSE for more information about licensing terms
 * Contact: Jose Fernandez Navarro <jose.fernandez.navarro@scilifelab.se>
 */

package com.spatialtranscriptomics.controller;

import com.spatialtranscriptomics.exceptions.BadRequestResponse;
import com.spatialtranscriptomics.exceptions.CustomBadRequestException;
import com.spatialtranscriptomics.exceptions.CustomInternalServerErrorException;
import com.spatialtranscriptomics.exceptions.CustomInternalServerErrorResponse;
import com.spatialtranscriptomics.exceptions.CustomNotFoundException;
import com.spatialtranscriptomics.exceptions.CustomNotModifiedException;
import com.spatialtranscriptomics.exceptions.NotFoundResponse;
import com.spatialtranscriptomics.exceptions.NotModifiedResponse;
import com.spatialtranscriptomics.model.ImageAlignment;
import com.spatialtranscriptomics.model.ImageMetadata;
import com.spatialtranscriptomics.model.LastModifiedDate;
import com.spatialtranscriptomics.serviceImpl.ImageAlignmentServiceImpl;
import com.spatialtranscriptomics.serviceImpl.ImageServiceImpl;
import com.spatialtranscriptomics.util.DateOperations;
import static com.spatialtranscriptomics.util.DateOperations.checkIfModified;
import java.awt.image.BufferedImage;
import java.util.List;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * This class is Spring MVC controller class for the API endpoint "rest/image".
 * It implements the methods available at this endpoint. Note that the images
 * are stored on Amazon S3, and not in Mongo.
 */

@Controller
@RequestMapping("/rest/image")
public class ImageController {

    @Autowired
    ImageServiceImpl imageService;
    
    @Autowired
    ImageAlignmentServiceImpl imagealignmentService;

    private static final Logger logger = Logger.getLogger(ImageController.class);

    /**
     * GET|HEAD /image/
     *
     * Lists image metadata.
     *
     * @return the metadata.
     */
    @Secured({"ROLE_CM", "ROLE_ADMIN"})
    @RequestMapping(method = {RequestMethod.GET, RequestMethod.HEAD})
    public @ResponseBody List<ImageMetadata> listMetadata() {
        
        List<ImageMetadata> list = imageService.list();
        if (list == null) {
            logger.info("Returning empty list of image metedata");
            throw new CustomNotFoundException("No image metadata found or "
                    + "you don't have permissions to access");
        }
        
        logger.info("Returning list of image metadata");
        return list;
    }

    /**
     * GET|HEAD /image/{id}
     * 
     * Returns image payload as a decompressed BufferedImage. NOTE: When
     * possible, use getCompressed() instead, due to size
     * limitations.
     *
     * @param id the image name.
     * @return the image as a BufferedImage.
     */
    @Secured({"ROLE_CM", "ROLE_USER", "ROLE_ADMIN"})
    @RequestMapping(value = "{id:.+}",
            produces = MediaType.IMAGE_JPEG_VALUE, 
            method = {RequestMethod.GET, RequestMethod.HEAD})
    public @ResponseBody BufferedImage get(@PathVariable String id) {
        // this {id:.+} is a workaround for a spring bug that truncates path
        // variables containing a dot
        BufferedImage img = imageService.getBufferedImage(id);
        if (img == null) {
            logger.info("Returning empty BufferedImage image");
            throw new CustomNotFoundException("No image found or you don't "
                    + "have permissions to access");
        }
        
        logger.info("Returning BufferedImage image " + id);
        return img;
    }

    /**
     * GET|HEAD /image/lastmodified/{id}
     * 
     * Returns the last modified date of an image.
     *
     * @param id the image name.
     * @return the date.
     */
    @Secured({"ROLE_CM", "ROLE_USER", "ROLE_ADMIN"})
    @RequestMapping(value = "/lastmodified/{id:.+}", method = {RequestMethod.GET, RequestMethod.HEAD})
    public @ResponseBody LastModifiedDate getLastModified(@PathVariable String id) {
        // this {id:.+} is a workaround for a spring bug that truncates path
        // variables containing a dot
        ImageMetadata img = imageService.getImageMetadata(id);
        if (img == null) {
            logger.info("Failed to return last modified time of image " + id);
            throw new CustomNotFoundException("An image with name " + id 
                    + "doesn't exist or you don't have permissions to access");
        }
        
        logger.info("Returning last modified time of image " + id);
        return new LastModifiedDate(img.getLastModified());
    }

    /**
     * GET|HEAD /image/compressed/{id}
     * 
     * Returns image payload as a compressed JPEG.
     *
     * @param id the image name.
     * @param ifModifiedSince client's last date of modification of the object
     * @return the image as a JPEG.
     */
    @Secured({"ROLE_CM", "ROLE_USER", "ROLE_ADMIN"})
    @RequestMapping(value = "/compressed/{id:.+}", 
            produces = MediaType.IMAGE_JPEG_VALUE,
            method = {RequestMethod.GET, RequestMethod.HEAD})
    public @ResponseBody HttpEntity<byte[]> getCompressed(
            @PathVariable String id, 
            @RequestHeader(value="If-Modified-Since", defaultValue="") String ifModifiedSince) {
        // this {id:.+} is a workaround for a spring bug that truncates path
        // variables containing a dot
        byte[] image = imageService.getCompressedImage(id);
        if (image == null) {
            logger.info("Returning empty JPEG image");
            throw new CustomNotFoundException("An image with name " + id 
                    + " doesn't exist or you don't have permissions to access");
        }
        
        ImageMetadata meta = imageService.getImageMetadata(id);
        // Check if already newest.
        DateTime reqTime = DateOperations.parseHTTPDate(ifModifiedSince);
        if (reqTime != null && !checkIfModified(meta.getLastModified(), reqTime)) {
            logger.info("Not returning S3Resource image " + id + " since not modified");
            throw new CustomNotModifiedException("This image has not been modified");
        }
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);
        headers.setContentLength(image.length);
        headers.add("Cache-Control", "public, must-revalidate, no-transform");
        headers.add("Vary", "Accept-Encoding");
        headers.add("Last-modified", DateOperations.getHTTPDateSafely(meta.getLastModified()));
        logger.info("Returning JPEG image");
        return new HttpEntity<byte[]>(image, headers);
    }

    /**
     * PUT /image/
     * 
     * Adds an image as a BufferedImage.
     * NOTE: When possible use addAsJSON() instead.
     * 
     * @param id the image name.
     * @param img the image.
     */
    @Secured({"ROLE_CM", "ROLE_ADMIN"})
    @RequestMapping(value = "{id:.+}", method = RequestMethod.PUT)
    public @ResponseBody void add(
            @PathVariable String id, 
            @RequestBody BufferedImage img) {
        // this {id:.+} is a workaround for a spring bug that truncates path
        // variables containing a dot
        if (imageService.getImageMetadata(id) != null) {
            logger.info("Cannot add JPEG image: exists " + id);
            throw new CustomBadRequestException(
                    "An image with this name exists already. Image names are unique");
        }
        
        imageService.add(id, img);
        logger.info("Succesfully added BufferedImage image " + id);
    }

    /**
     * DELETE /image/{id}
     * 
     * Deletes an image.
     * 
     * @param id the image name.
     */
    @Secured({"ROLE_CM", "ROLE_ADMIN"})
    @RequestMapping(value = "{id:.+}", method = RequestMethod.DELETE)
    public @ResponseBody void delete(@PathVariable String id) {
        // this {id:.+} is a workaround for a spring bug that truncates path
        // variables containing a dot
        //check that the image is not used in an image alignment object
        List<ImageAlignment> imals = imagealignmentService.list();
        for (ImageAlignment ia : imals) {
            if (ia.getFigure_blue().equals(id) || ia.getFigure_red().equals(id)) {
                logger.info("Trying to delete an image " + id + " that is used");
                throw new CustomBadRequestException("Trying to delete an image " + id 
                        + "that used in an Image Alignment object");
            }
        }
        
        imageService.delete(id);
        logger.info("Successfully deleted image " + id);
    }

    @ExceptionHandler(CustomNotModifiedException.class)
    @ResponseStatus(value = HttpStatus.NOT_MODIFIED)
    public @ResponseBody
    NotModifiedResponse handleNotModifiedException(CustomNotModifiedException ex) {
        return new NotModifiedResponse(ex.getMessage());
    }

    @ExceptionHandler(CustomNotFoundException.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public @ResponseBody
    NotFoundResponse handleNotFoundException(CustomNotFoundException ex) {
        return new NotFoundResponse(ex.getMessage());
    }

    @ExceptionHandler(CustomBadRequestException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public @ResponseBody
    BadRequestResponse handleBadRequestException(CustomBadRequestException ex) {
        return new BadRequestResponse(ex.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public @ResponseBody
    CustomInternalServerErrorResponse handleRuntimeException(CustomInternalServerErrorException ex) {
        logger.error("Unknown error in image controller: " + ex.getMessage());
        return new CustomInternalServerErrorResponse(ex.getMessage());
    }

}
