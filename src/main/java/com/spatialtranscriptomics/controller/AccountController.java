/*
 * Copyright (C) 2012 Spatial Transcriptomics AB
 * Read LICENSE for more information about licensing terms
 * Contact: Jose Fernandez Navarro <jose.fernandez.navarro@scilifelab.se>
 */

package com.spatialtranscriptomics.controller;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
//import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.spatialtranscriptomics.exceptions.BadRequestResponse;
import com.spatialtranscriptomics.exceptions.CustomBadRequestException;
import com.spatialtranscriptomics.exceptions.CustomNotFoundException;
import com.spatialtranscriptomics.exceptions.NotFoundResponse;
import com.spatialtranscriptomics.model.Account;
import com.spatialtranscriptomics.serviceImpl.AccountServiceImpl;

import java.security.Principal;
import java.util.List;

import javax.validation.Valid;

/**
 * This class is Spring MVC controller class for the API endpoint "rest/account". It implements the methods available at this endpoint.
 */

@Repository
@Controller
@RequestMapping("/rest/account")
public class AccountController {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger
			.getLogger(AccountController.class);

	@Autowired
	AccountServiceImpl accountService;

	@Autowired
	PasswordEncoder passwordEncoder;

	// list
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.GET)
	public @ResponseBody
	List<Account> list() {

		return accountService.list();
	}

	// get by id
	@Secured("ROLE_ADMIN")
	@RequestMapping(value = "{id}", method = RequestMethod.GET)
	public @ResponseBody
	Account get(@PathVariable String id) {

		Account account = accountService.find(id);
		if (account == null) {
			throw new CustomNotFoundException(
					"An account with this ID does not exist or you dont have permissions to access it.");
		}
		return account;
	}

	// get current account
	@Secured({ "ROLE_CM", "ROLE_USER", "ROLE_ADMIN" })
	@RequestMapping(value = "/current/user", method = RequestMethod.GET)
	public @ResponseBody
	Account getCurrent(Principal principal) {

		if (principal == null) {
			throw new CustomBadRequestException("You are not logged in.");
		}

		Account account = accountService.findByName(principal.getName());

		if (account == null) {
			throw new CustomBadRequestException("Current user "
					+ " currentUsername "
					+ "could not be matched to an account.");
		}
		return account;
	}

	// add
	@Secured("ROLE_ADMIN")
	@RequestMapping(method = RequestMethod.POST)
	public @ResponseBody
	Account add(@RequestBody @Valid Account account, BindingResult result) {

		// Data model validation
		if (result.hasErrors()) {
			throw new CustomBadRequestException(
					"Data model is invalid. Missing required fields?");
		}

		// Checks
		if (account.getId() != null) {
			throw new CustomBadRequestException(
					"An account must not have an ID. ID will be created automatically.");
		}
		if (accountService.findByName(account.getUsername()) != null) {
			throw new CustomBadRequestException(
					"An account with this username already exists. Usernames are unique.");
		}
		// Encode Password
		account.setPassword(passwordEncoder.encode(account.getPassword()));

		return accountService.add(account);
	}

	// update account
	@Secured("ROLE_ADMIN")
	@RequestMapping(value = "{id}", method = RequestMethod.PUT)
	public @ResponseBody
	void update(@PathVariable String id, @RequestBody @Valid Account account,
			BindingResult result) {

		// Data model validation
		if (result.hasErrors()) {
			throw new CustomBadRequestException(
					"Data model is invalid. Missing required fields?");
		}

		if (!id.equals(account.getId())) {
			throw new CustomBadRequestException(
					"ID in request URL does not match ID in content body.");
		} else if (accountService.find(id) == null) {
			throw new CustomBadRequestException(
					"An account with this id does not exist or you don't have permissions to access it.");
		} else if (accountService.findByName(account.getUsername()) != null) {
			if (!accountService.findByName(account.getUsername()).getId()
					.equals(id)) {
				// account with this name and another ID exists
				throw new CustomBadRequestException(
						"Another account with this username exists already. Usernames are unique.");
			}
		}

		accountService.update(account);

	}

	// delete
	@Secured("ROLE_ADMIN")
	@RequestMapping(value = "{id}", method = RequestMethod.DELETE)
	public @ResponseBody
	void delete(@PathVariable String id) {
		accountService.delete(id);
	}

	// // encode Password
	// this is only a utility endpoint for development. Not part of API
	// specification.
	// // You can use it to create encoded passwords from plain text. You need
	// this if you want to enter a Pwd to MongoDB manually
	// @RequestMapping(value = "/encode", method = RequestMethod.GET)
	// public @ResponseBody
	// String encodePwd(@RequestParam(value = "pwd", required = true) String
	// pwd) {
	// return passwordEncoder.encode(pwd);
	// }

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
