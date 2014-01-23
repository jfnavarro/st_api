/*
 * Copyright (C) 2012 Spatial Transcriptomics AB
 * Read LICENSE for more information about licensing terms
 * Contact: Jose Fernandez Navarro <jose.fernandez.navarro@scilifelab.se>
 */

package com.spatialtranscriptomics.controller;

import java.awt.image.BufferedImage;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.spatialtranscriptomics.exceptions.BadRequestResponse;
import com.spatialtranscriptomics.exceptions.CustomBadRequestException;
import com.spatialtranscriptomics.model.ImageMetadata;
import com.spatialtranscriptomics.serviceImpl.ImageServiceImpl;

/**
 * This class is Spring MVC controller class for the API endpoint "rest/image". It implements the methods available at this endpoint.
 */
@Controller
@RequestMapping("/rest/image")
public class ImageController {

	@Autowired
	ImageServiceImpl imageService;

	private static final Logger logger = Logger
			.getLogger(ImageController.class);

	// list image metadata
	@Secured({"ROLE_CM","ROLE_ADMIN"})
	@RequestMapping(method = RequestMethod.GET)
	public @ResponseBody
	List<ImageMetadata> listMetadata(){

		return imageService.list();
	}

	// get image payload
	// this {id:.+} is a workaround for a spring bug that truncates path
	// variables containing a dot
	@Secured({"ROLE_CM","ROLE_USER","ROLE_ADMIN"})
	@RequestMapping(value = "{id:.+}", headers = "Accept=image/jpeg, image/jpg", method = RequestMethod.GET)
	public @ResponseBody
	BufferedImage get(@PathVariable String id) {

		BufferedImage img = imageService.getBufferedImage(id);
		return img;
	}

	// add
	@Secured({"ROLE_CM","ROLE_ADMIN"})
	@RequestMapping(value = "{id:.+}", method = RequestMethod.PUT)
	public @ResponseBody
	void add(@PathVariable String id, @RequestBody BufferedImage img) {

		if(imageService.getImageMetadata(id)!=null){
			logger.error("image exists "+id);
			throw new CustomBadRequestException(
					"An image with this name exists already. Image names are unique.");
		}
		imageService.add(id, img);

	}

	// delete
	@Secured({"ROLE_CM","ROLE_ADMIN"})
	@RequestMapping(value = "{id:.+}", method = RequestMethod.DELETE)
	public @ResponseBody
	void delete(@PathVariable String id) {

		imageService.delete(id);
	}
	

	
	@ExceptionHandler(CustomBadRequestException.class)
	@ResponseStatus(value = HttpStatus.BAD_REQUEST)
	public @ResponseBody
	BadRequestResponse handleNotFoundException(CustomBadRequestException ex) {
		return new BadRequestResponse(ex.getMessage());
	}


}
