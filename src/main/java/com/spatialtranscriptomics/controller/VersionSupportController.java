/*
 * Copyright (C) 2012 Spatial Transcriptomics AB
 * Read LICENSE for more information about licensing terms
 * Contact: Jose Fernandez Navarro <jose.fernandez.navarro@scilifelab.se>
 */

package com.spatialtranscriptomics.controller;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.spatialtranscriptomics.model.VersionSupportInfo;

/**
 * This class is Spring MVC controller class for the API endpoint "rest/versionsupportinfo". It implements the methods available at this endpoint.
 */

@Repository
@Controller
@RequestMapping("/rest/versionsupportinfo")
public class VersionSupportController {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger
			.getLogger(VersionSupportController.class);

	private @Value("${client.minsupportedversion}")
	String minSupportedClientVersion;

	// get
	@RequestMapping(method = RequestMethod.GET)
	public @ResponseBody
	VersionSupportInfo getInfo() {

		VersionSupportInfo info = new VersionSupportInfo();
		info.setMinSupportedClientVersion(minSupportedClientVersion);
		return info;
	}

}
