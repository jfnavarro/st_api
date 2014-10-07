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
import com.spatialtranscriptomics.model.ImageMetadata;
import com.spatialtranscriptomics.model.LastModifiedDate;
import com.spatialtranscriptomics.model.S3Resource;
import com.spatialtranscriptomics.serviceImpl.ImageServiceImpl;
import java.awt.image.BufferedImage;
import java.util.List;
import javax.validation.Valid;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
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
        return imageService.list();
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
            throw new CustomNotFoundException("An image with this name does not exist or you do not have permissions to access it.");
        }
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
            throw new CustomNotFoundException("An image with this name does not exist or you do not have permissions to access it.");
        }
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
     * @return the image as JSON.
     */
    @Secured({"ROLE_CM", "ROLE_USER", "ROLE_ADMIN"})
    @RequestMapping(value = "/compressedjson/{id:.+}", method = {RequestMethod.GET, RequestMethod.HEAD})
    public @ResponseBody
    S3Resource getCompressedAsJSON(@PathVariable String id) {
        // this {id:.+} is a workaround for a spring bug that truncates path
        // variables containing a dot
        byte[] image = imageService.getCompressedImage(id);
        if (image == null) {
            throw new CustomNotFoundException("An image with this name does not exist or you do not have permissions to access it.");
        }
        S3Resource wrapper = new S3Resource("image/jpeg", id, image);
        return wrapper;
    }

    /**
     * PUT /imagealignment/
     * 
     * Adds/updates an image as a BufferedImage.
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
            logger.error("Cannot add image: exists " + id);
            throw new CustomBadRequestException(
                    "An image with this name exists already. Image names are unique.");
        }
        imageService.add(id, img);

    }

    /**
     * PUT /imagealignment/compressedjson/{id}
     * 
     * Adds/updates an JPEG image wrapped in JSON.
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
        byte[] img = image.getFile();
        if (id == null || image.getFilename() == null || image.getFilename().equals("")
                || img == null || img.length == 0) {
            logger.error("Cannot add empty image.");
            throw new CustomBadRequestException("The image seems to be empty, lacking name.");

        }
        if (!id.equals(image.getFilename())) {
            throw new CustomBadRequestException("Filename and ID mismatch.");
        }
        if (imageService.getImageMetadata(image.getFilename()) != null) {
            logger.error("Cannot add image: exists " + image.getFilename());
            throw new CustomBadRequestException("An image with this name exists already. Image names are unique.");
        }
        imageService.addCompressed(image.getFilename(), img);
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
