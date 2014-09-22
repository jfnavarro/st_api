/*
 * Copyright (C) 2012 Spatial Transcriptomics AB
 * Read LICENSE for more information about licensing terms
 * Contact: Jose Fernandez Navarro <jose.fernandez.navarro@scilifelab.se>
 */

package com.spatialtranscriptomics.controller;

import com.spatialtranscriptomics.exceptions.BadRequestResponse;
import com.spatialtranscriptomics.exceptions.CustomBadRequestException;
import com.spatialtranscriptomics.exceptions.CustomNotFoundException;
import com.spatialtranscriptomics.model.DatasetInfo;
import com.spatialtranscriptomics.model.FeaturesMetadata;
import com.spatialtranscriptomics.model.FeaturesWrapper;
import com.spatialtranscriptomics.model.LastModifiedDate;
import com.spatialtranscriptomics.model.MongoUserDetails;
import com.spatialtranscriptomics.serviceImpl.FeaturesServiceImpl;
import com.spatialtranscriptomics.serviceImpl.MongoUserDetailsServiceImpl;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MultipartFile;

/**
 * This class is Spring MVC controller class for the API endpoint "rest/features". It implements the methods available at this endpoint.
 */

@Controller
@RequestMapping("/rest/features")
public class FeaturesController {

    private static final Logger logger = Logger.getLogger(FeaturesController.class);
    
    @Autowired
    FeaturesServiceImpl featuresService;
    
    @Autowired
    MongoUserDetailsServiceImpl customUserDetailsService;

    @Autowired
    MongoOperations mongoTemplateFeatureDB;
    
    // list image metadata
    @Secured({"ROLE_CM","ROLE_ADMIN"})
    @RequestMapping(method = RequestMethod.GET)
    public @ResponseBody
    List<FeaturesMetadata> listMetadata(){
            return featuresService.listMetadata();
    }
    
    // get features payload wrapped in JSON
    @Secured({"ROLE_CM", "ROLE_USER", "ROLE_ADMIN"})
    @RequestMapping(value = "{id}", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody
    FeaturesWrapper get(@PathVariable String id) {
        try {
            System.out.println("In GET");
            InputStream is = featuresService.find(id);
            byte[] bytes = IOUtils.toByteArray(is);
            System.out.println("Fetched " + bytes.length);
            FeaturesWrapper wrap = new FeaturesWrapper(id, bytes);
            return wrap;
        } catch (IOException ex) {
            logger.info("Error writing file to output stream with file " + id);
            throw new CustomBadRequestException("IOError writing file to output stream");
          }
    }
        
    // add / update
    @Secured({"ROLE_CM", "ROLE_ADMIN"})
    @RequestMapping(value = "{id}", method=RequestMethod.PUT)
    public @ResponseBody
    void addOrUpdate(@PathVariable String id, @RequestBody FeaturesWrapper feats){
        System.out.println("Inside addUpdate()");
        byte[] bytes = feats.getFile();
        if (id != null && bytes != null && bytes.length != 0) {
            try {
                System.out.println("Trying to add features.");
                boolean updated = featuresService.addUpdate(id, bytes);
                System.out.println("Added features!");
            } catch (Exception e) {
                System.out.println("Failed to add features.");
                throw new CustomBadRequestException("Failed to add features.");
            }
        } else {
            System.out.println("Failed to add features. Is the file empty?");
            throw new CustomBadRequestException("Failed to add features. Is the file empty?");
        }
    }
    
    // delete
    @Secured({"ROLE_CM","ROLE_ADMIN"})
    @RequestMapping(value = "{id}", method = RequestMethod.DELETE)
    public @ResponseBody
    void delete(@PathVariable String id) {
        featuresService.delete(id);
    }
    
    // get last modified
    @Secured({"ROLE_CM","ROLE_USER","ROLE_ADMIN"})
    @RequestMapping(value = "/lastmodified/{id}", method = RequestMethod.GET)
    public @ResponseBody
    LastModifiedDate getLastModified(@PathVariable String id) {
            FeaturesMetadata feat = featuresService.getMetadata(id);
            if (feat == null) {
                throw new CustomNotFoundException("A features file with this id does not exist or you do not have permissions to access it.");
            }
            return new LastModifiedDate(feat.getLastModified());
    }
        

	
    @ExceptionHandler(CustomBadRequestException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public @ResponseBody
    BadRequestResponse handleNotFoundException(CustomBadRequestException ex) {
        return new BadRequestResponse(ex.getMessage());
    }
    
}