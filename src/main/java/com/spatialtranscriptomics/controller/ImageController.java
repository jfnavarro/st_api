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
import com.spatialtranscriptomics.model.S3Resource;
import com.spatialtranscriptomics.serviceImpl.ImageServiceImpl;
import com.spatialtranscriptomics.util.DateOperations;
import java.awt.image.BufferedImage;
import java.util.List;
import javax.validation.Valid;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
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

    private static final Logger logger = Logger
            .getLogger(ImageController.class);

    /**
     * GET|HEAD /image/
     *
     * Lists image metadata.
     *
     * @return the metadata.
     */
    @Secured({"ROLE_CM", "ROLE_ADMIN"})
    @RequestMapping(method = {RequestMethod.GET, RequestMethod.HEAD})
    public @ResponseBody
    List<ImageMetadata> listMetadata() {
        List<ImageMetadata> list = imageService.list();
        if (list == null) {
            logger.info("Returning empty list of image metedata");
            throw new CustomNotFoundException("No image metadata found or you dont have permissions to access them.");
        }
        logger.info("Returning list of image metadata");
        return list;
    }

    /**
     * GET|HEAD /image/{id}
     * 
     * Returns image payload as a decompressed BufferedImage. NOTE: When
     * possible, use getCompressed() or getCompressedAsJSON instead, due to size
     * limitations.
     *
     * @param id the image name.
     * @return the image as a BufferedImage.
     */
    @Secured({"ROLE_CM", "ROLE_USER", "ROLE_ADMIN"})
    @RequestMapping(value = "{id:.+}", produces = MediaType.IMAGE_JPEG_VALUE, method = {RequestMethod.GET, RequestMethod.HEAD})
    public @ResponseBody
    BufferedImage get(@PathVariable String id) {
        // this {id:.+} is a workaround for a spring bug that truncates path
        // variables containing a dot
        BufferedImage img = imageService.getBufferedImage(id);
        if (img == null) {
            logger.info("Returning empty BufferedImage image");
            throw new CustomNotFoundException("No image found or you dont have permissions to access them.");
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
    public @ResponseBody
    LastModifiedDate getLastModified(@PathVariable String id) {
        // this {id:.+} is a workaround for a spring bug that truncates path
        // variables containing a dot
        ImageMetadata img = imageService.getImageMetadata(id);
        if (img == null) {
            logger.info("Failed to return last modified time of image " + id);
            throw new CustomNotFoundException("An image with this name does not exist or you do not have permissions to access it.");
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
     * @return the image as a JPEG.
     */
    @Secured({"ROLE_CM", "ROLE_USER", "ROLE_ADMIN"})
    @RequestMapping(value = "/compressed/{id:.+}", produces = MediaType.IMAGE_JPEG_VALUE, method = {RequestMethod.GET, RequestMethod.HEAD})
    public @ResponseBody
    byte[] getCompressed(@PathVariable String id) {
        // this {id:.+} is a workaround for a spring bug that truncates path
        // variables containing a dot
        byte[] image = imageService.getCompressedImage(id);
        if (image == null) {
            logger.info("Returning empty JPEG image");
            throw new CustomNotFoundException("An image with this name does not exist or you do not have permissions to access it.");
        }
        logger.info("Returning JPEG image");
        return image;
        //HttpHeaders headers = new HttpHeaders();
        //headers.setContentType(MediaType.IMAGE_JPEG);
        //headers.setContentLength(image.length);
        //return new HttpEntity<byte[]>(image, headers);
    }

    /**
     * GET|HEAD /image/compressedjson/{id}
     * 
     * Returns image payload as a JPEG wrapped in JSON.
     *
     * @param id the image name.
     * @param ifModifiedSince request mod date info.
     * @return the image as JSON.
     */
    @Secured({"ROLE_CM", "ROLE_USER", "ROLE_ADMIN"})
    @RequestMapping(value = "/compressedjson/{id:.+}", method = {RequestMethod.GET, RequestMethod.HEAD})
    public @ResponseBody
    HttpEntity<S3Resource> getCompressedAsJSON(@PathVariable String id, @RequestHeader(value="If-Modified-Since", defaultValue="") String ifModifiedSince) {
        // this {id:.+} is a workaround for a spring bug that truncates path
        // variables containing a dot
        byte[] image = imageService.getCompressedImage(id);
        ImageMetadata meta = imageService.getImageMetadata(id);
        if (image == null || meta == null) {
            logger.info("Returning empty S3Resource image");
            throw new CustomNotFoundException("An image with this name does not exist or you do not have permissions to access it.");
        }
        // Check if already newest.
        DateTime reqTime = DateOperations.parseHTTPDate(ifModifiedSince);
        if (reqTime != null) {
            DateTime resTime = meta.getLastModified() == null ? new DateTime(2012,1,1,0,0) : meta.getLastModified();
            // NOTE: Only precision within day.
            resTime = new DateTime(resTime.getYear(), resTime.getMonthOfYear(), resTime.getDayOfMonth(), resTime.getHourOfDay(), resTime.getMinuteOfHour(), resTime.getSecondOfMinute());
            if (!resTime.isAfter(reqTime)) {
                logger.info("Not returning S3Resource image " + id + " since not modified");
                throw new CustomNotModifiedException("This image has not been modified");
            }
        }
        S3Resource wrapper = new S3Resource("image/jpeg", id, image);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Cache-Control", "public, must-revalidate, no-transform");
        headers.add("Vary", "Accept-Encoding");
        headers.add("Last-modified", DateOperations.getHTTPDateSafely(meta.getLastModified()));
        HttpEntity<S3Resource> entity = new HttpEntity<S3Resource>(wrapper, headers);
        logger.info("Returning S3Resource image " + id);
        return entity;
    }

    /**
     * PUT /imagealignment/
     * 
     * Adds an image as a BufferedImage.
     * NOTE: When possible use addAsJSON() instead.
     * 
     * @param id the image name.
     * @param img the image.
     */
    @Secured({"ROLE_CM", "ROLE_ADMIN"})
    @RequestMapping(value = "{id:.+}", method = RequestMethod.PUT)
    public @ResponseBody
    void add(@PathVariable String id, @RequestBody BufferedImage img) {
        // this {id:.+} is a workaround for a spring bug that truncates path
        // variables containing a dot
        if (imageService.getImageMetadata(id) != null) {
            logger.info("Cannot add JPEG image: exists " + id);
            throw new CustomBadRequestException(
                    "An image with this name exists already. Image names are unique.");
        }
        imageService.add(id, img);
        logger.info("Succesfully added BufferedImage image " + id);
    }

    /**
     * PUT /imagealignment/compressedjson/{id}
     * 
     * Adds an JPEG image wrapped in JSON.
     * 
     * @param id the image filename.
     * @param image the image.
     * @param result the binding.
     */
    @Secured({"ROLE_CM", "ROLE_ADMIN"})
    @RequestMapping(value = "/compressedjson/{id:.+}", method = RequestMethod.PUT)
    public @ResponseBody
    void addAsJSON(@PathVariable String id, @RequestBody @Valid S3Resource image, BindingResult result) {
        // this {id:.+} is a workaround for a spring bug that truncates path
        // variables containing a dot
        if (imageService.getImageMetadata(id) != null) {
            logger.info("Cannot add S3Resource image: exists " + id);
            throw new CustomBadRequestException(
                    "An image with this name exists already. Image names are unique.");
        }
        byte[] img = image.getFile();
        if (id == null || image.getFilename() == null || image.getFilename().equals("")
                || img == null || img.length == 0) {
            logger.info("Cannot add empty or nameless S3Resource image.");
            throw new CustomBadRequestException("The image seems to be empty or lacking name.");

        }
        if (!id.equals(image.getFilename())) {
            logger.info("Failed to add S3Resource image. Filename and ID mismatch.");
            throw new CustomBadRequestException("Filename and ID mismatch.");
        }
        imageService.addCompressed(image.getFilename(), img);
        logger.info("Successfully added S3Resource image " + id);
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
    public @ResponseBody
    void delete(@PathVariable String id) {
        // this {id:.+} is a workaround for a spring bug that truncates path
        // variables containing a dot
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
