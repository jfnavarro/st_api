/*
 * Copyright (C) 2012 Spatial Transcriptomics AB
 * Read LICENSE for more information about licensing terms
 * Contact: Jose Fernandez Navarro <jose.fernandez.navarro@scilifelab.se>
 */

package com.spatialtranscriptomics.controller;

import com.spatialtranscriptomics.exceptions.BadRequestResponse;
import com.spatialtranscriptomics.exceptions.CustomBadRequestException;
import com.spatialtranscriptomics.exceptions.CustomNotFoundException;
import com.spatialtranscriptomics.exceptions.NotFoundResponse;
import com.spatialtranscriptomics.model.Chip;
import com.spatialtranscriptomics.model.ImageAlignment;
import com.spatialtranscriptomics.serviceImpl.ChipServiceImpl;
import com.spatialtranscriptomics.serviceImpl.DatasetServiceImpl;
import com.spatialtranscriptomics.serviceImpl.ImageAlignmentServiceImpl;
import java.util.List;
import javax.validation.Valid;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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

/**
 * This class is Spring MVC controller class for the API endpoint "rest/chip". It implements the methods available at this endpoint.
 */

@Repository
@Controller
@RequestMapping("/rest/chip")
public class ChipController {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(ChipController.class);

	@Autowired
	ChipServiceImpl chipService;
        
        @Autowired
	ImageAlignmentServiceImpl imageAlignmentService;

        @Autowired
	DatasetServiceImpl datasetService;
        
	// list
	@Secured({"ROLE_CM","ROLE_ADMIN"})
	@RequestMapping(method = RequestMethod.GET)
	public @ResponseBody
	List<Chip> list() {
		List<Chip> chips = chipService.list();
		if (chips == null) {
			throw new CustomNotFoundException("No chips found or you dont have permissions to access them.");
		}
		return chips;
	}

	// get
	@Secured({"ROLE_CM","ROLE_USER","ROLE_ADMIN"})
	@RequestMapping(value = "{id}", method = RequestMethod.GET)
	public @ResponseBody
	Chip get(@PathVariable String id) {
		Chip chip = chipService.find(id);
		if (chip == null) {
			throw new CustomNotFoundException("A chip with this ID does not exist or you dont have permissions to access it.");
		}
		return chip;
	}
        
        // get last modified
	@Secured({"ROLE_CM","ROLE_USER","ROLE_ADMIN"})
	@RequestMapping(value = "/lastmodified/{id}", method = RequestMethod.GET)
	public @ResponseBody
	DateTime getLastModified(@PathVariable String id) {
		Chip chip = chipService.find(id);
		if (chip == null) {
			throw new CustomNotFoundException("A chip with this ID does not exist or you dont have permissions to access it.");
		}
		return chip.getLast_modified();
	}

	// add
	@Secured({"ROLE_CM","ROLE_ADMIN"})
	@RequestMapping(method = RequestMethod.POST)
	public @ResponseBody
	Chip add(@RequestBody @Valid Chip chip, BindingResult result) {
		// Data model validation
		if (result.hasErrors()) {
			throw new CustomBadRequestException("Chip is invalid. Missing required fields?");
		}
		if (chip.getId() != null) {
			throw new CustomBadRequestException("The chip you want to add must not have an ID. The ID will be autogenerated.");
		} else if (chipService.findByName(chip.getName()) != null) {
			throw new CustomBadRequestException("A chip with this name exists already. Chip names are unique.");
		}
		return chipService.add(chip);
	}

	// update
	@Secured({"ROLE_CM","ROLE_ADMIN"})
	@RequestMapping(value = "{id}", method = RequestMethod.PUT)
	public @ResponseBody
	void update(@PathVariable String id, @RequestBody @Valid Chip chip, BindingResult result) {
		// Data model validation
		if (result.hasErrors()) {
			// TODO send error messages here
			throw new CustomBadRequestException("Chip is invalid. Missing required fields?");
		}
		if (!id.equals(chip.getId())) {
			throw new CustomBadRequestException("Chip ID in request URL does not match ID in content body.");
		} else if (chipService.find(id) == null) {
			throw new CustomBadRequestException("A chip with this ID does not exist or you don't have permissions to access it.");
		} else {
			chipService.update(chip);
		}
	}

	// delete
	@Secured({"ROLE_CM","ROLE_ADMIN"})
	@RequestMapping(value = "{id}", method = RequestMethod.DELETE)
	public @ResponseBody
	void delete(@PathVariable String id,
                @RequestParam(value="cascade", required = false, defaultValue = "true") boolean cascade) {
            if (cascade) {
                List<ImageAlignment> imals = imageAlignmentService.deleteForChip(id);
                if (imals != null) {
                    for (ImageAlignment imal : imals) {
                            datasetService.setUnabledForImageAlignment(imal.getId());
                    }
                }
            }
            chipService.delete(id);
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
	BadRequestResponse handleNotFoundException(CustomBadRequestException ex) {
		return new BadRequestResponse(ex.getMessage());
	}

}
