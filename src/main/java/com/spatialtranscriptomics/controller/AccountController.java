/*
 * Copyright (C) 2012 Spatial Transcriptomics AB
 * Read LICENSE for more information about licensing terms
 * Contact: Jose Fernandez Navarro <jose.fernandez.navarro@scilifelab.se>
 */
package com.spatialtranscriptomics.controller;

import com.spatialtranscriptomics.component.StaticContextAccessor;
import com.spatialtranscriptomics.exceptions.BadRequestResponse;
import com.spatialtranscriptomics.exceptions.CustomBadRequestException;
import com.spatialtranscriptomics.exceptions.CustomInternalServerErrorException;
import com.spatialtranscriptomics.exceptions.CustomInternalServerErrorResponse;
import com.spatialtranscriptomics.exceptions.CustomNotFoundException;
import com.spatialtranscriptomics.exceptions.CustomNotModifiedException;
import com.spatialtranscriptomics.exceptions.NotFoundResponse;
import com.spatialtranscriptomics.exceptions.NotModifiedResponse;
import com.spatialtranscriptomics.model.Account;
import com.spatialtranscriptomics.model.LastModifiedDate;
import com.spatialtranscriptomics.serviceImpl.AccountServiceImpl;
import com.spatialtranscriptomics.serviceImpl.DatasetInfoServiceImpl;
import com.spatialtranscriptomics.serviceImpl.DatasetServiceImpl;
import com.spatialtranscriptomics.serviceImpl.PipelineExperimentServiceImpl;
import com.spatialtranscriptomics.serviceImpl.SelectionServiceImpl;
import com.spatialtranscriptomics.serviceImpl.TaskServiceImpl;
import com.spatialtranscriptomics.util.DateOperations;
import java.security.Principal;
import java.util.Iterator;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * This class is Spring MVC controller class for the API endpoint
 * "rest/account". It implements the methods available at this endpoint.
 */
@Repository
@Controller
@RequestMapping("/rest/account")
public class AccountController {

    private static final Logger logger = Logger
            .getLogger(AccountController.class);

    @Autowired
    AccountServiceImpl accountService;

    @Autowired
    DatasetInfoServiceImpl datasetinfoService;

    @Autowired
    TaskServiceImpl taskService;

    @Autowired
    SelectionServiceImpl selectionService;

    @Autowired
    DatasetServiceImpl datasetService;

    @Autowired
    PipelineExperimentServiceImpl pipelineexperimentService;

    @Autowired
    PasswordEncoder passwordEncoder;

    /**
     * GET|HEAD /account/
     * GET|HEAD /account/?dataset={datasetId}
     * 
     * Lists enabled accounts.
     * @param datasetId the dataset ID.
     * @return the list.
     */
    @Secured({"ROLE_CM", "ROLE_USER", "ROLE_ADMIN"})
    @RequestMapping(method = {RequestMethod.GET, RequestMethod.HEAD})
    public @ResponseBody
    List<Account> list(@RequestParam(value = "dataset", required = false) String datasetId) {
        List<Account> accs;
        if (datasetId != null) {
            accs = accountService.findByDataset(datasetId);
        } else {
            accs = accountService.list();
        }
        if (accs == null) {
            logger.info("Returning empty list of accounts");
            throw new CustomNotFoundException("No accounts found or you dont have permissions to access them.");
        } 
        Iterator<Account> i = accs.iterator();
        while (i.hasNext()) {
            Account a = i.next(); // must be called before you can call i.remove()
            if (!a.isEnabled()) {
                i.remove();
            }
        }
        logger.info("Returning list of enabled accounts");
        return accs;
    }

    /**
     * GET|HEAD /account/all/
     * GET|HEAD /account/all/?dataset={datasetId}
     * 
     * Lists enabled/disabled accounts.
     * @param datasetId the dataset ID.
     * @return the list.
     */
    @Secured({"ROLE_CM", "ROLE_USER", "ROLE_ADMIN"})
    @RequestMapping(value = "/all", method = {RequestMethod.GET, RequestMethod.HEAD})
    public @ResponseBody
    List<Account> listAll(@RequestParam(value = "dataset", required = false) String datasetId) {
        List<Account> accs;
        if (datasetId != null) {
            logger.info("Returning list of enabled/disabled accounts for dataset " + datasetId);
            accs = accountService.findByDataset(datasetId);
        } else {
            logger.info("Returning list of enabled/disabled accounts");
            accs = accountService.list();
        }
        if (accs == null) {
            logger.info("Returning empty list of enabled/disabled accounts");
            throw new CustomNotFoundException("No accounts found or you dont have permissions to access them.");
        }
        return accs;
    }

    /**
     * GET|HEAD /account/{id}
     * 
     * Finds enabled account.
     * @param id the account ID.
     * @param ifModifiedSince request timestamp.
     * @return the account.
     */
    @Secured({"ROLE_CM", "ROLE_USER", "ROLE_ADMIN"})
    @RequestMapping(value = "{id}", method = {RequestMethod.GET, RequestMethod.HEAD})
    public @ResponseBody
    HttpEntity<Account> get(@PathVariable String id, @RequestHeader(value="If-Modified-Since", defaultValue="") String ifModifiedSince) {
        Account account = accountService.find(id);
        if (account == null || !account.isEnabled()) {
            logger.info("Failed to return enabled account " + id);
            throw new CustomNotFoundException("An account with this ID does not exist, is disabled, or you dont have permissions to access it.");
        }
        // Check if already newest.
        DateTime reqTime = DateOperations.parseHTTPDate(ifModifiedSince);
        if (reqTime != null) {
            DateTime resTime = account.getLast_modified() == null ? new DateTime(2012,1,1,0,0) : account.getLast_modified();
            // NOTE: Only precision within day.
            resTime = new DateTime(resTime.getYear(), resTime.getMonthOfYear(), resTime.getDayOfMonth(), resTime.getHourOfDay(), resTime.getMinuteOfHour(), resTime.getSecondOfMinute());
            if (!resTime.isAfter(reqTime)) {
                logger.info("Not returning enabled account " + id + " since not modified");
                throw new CustomNotModifiedException("This account has not been modified");
            }
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Cache-Control", "public, must-revalidate, no-transform");
        headers.add("Vary", "Accept-Encoding");
        headers.add("Last-modified", DateOperations.getHTTPDateSafely(account.getLast_modified()));
        HttpEntity<Account> entity = new HttpEntity<Account>(account, headers);
        logger.info("Returning enabled account " + id);
        return entity;
    }

    /**
     * GET|HEAD /account/all/{id}
     * 
     * Finds enabled/disabled account.
     * @param id the account ID.
     * @param ifModifiedSince request timestamp.
     * @return the account.
     */
    @Secured({"ROLE_CM", "ROLE_USER", "ROLE_ADMIN"})
    @RequestMapping(value = "/all/{id}", method = RequestMethod.GET)
    public @ResponseBody
    HttpEntity<Account> getAll(@PathVariable String id, @RequestHeader(value="If-Modified-Since", defaultValue="") String ifModifiedSince) {
        Account account = accountService.find(id);
        if (account == null) {
            logger.info("Failed to return enabled/disabled account " + id);
            throw new CustomNotFoundException("An account with this ID does not exist, or you dont have permissions to access it.");
        }
        // Check if already newest.
        DateTime reqTime = DateOperations.parseHTTPDate(ifModifiedSince);
        if (reqTime != null) {
            DateTime resTime = account.getLast_modified() == null ? new DateTime(2012,1,1,0,0) : account.getLast_modified();
            // NOTE: Only precision within day.
            resTime = new DateTime(resTime.getYear(), resTime.getMonthOfYear(), resTime.getDayOfMonth(), resTime.getHourOfDay(), resTime.getMinuteOfHour(), resTime.getSecondOfMinute());
            if (!resTime.isAfter(reqTime)) {
                logger.info("Not returning enabled/disabled account " + id + " since not modified");
                throw new CustomNotModifiedException("This account has not been modified");
            }
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Cache-Control", "public, must-revalidate, no-transform");
        headers.add("Vary", "Accept-Encoding");
        headers.add("Last-modified", DateOperations.getHTTPDateSafely(account.getLast_modified()));
        HttpEntity<Account> entity = new HttpEntity<Account>(account, headers);
        logger.info("Returning enabled/disabled account " + id);
        return entity;
    }

    /**
     * GET|HEAD /account/lastmodified/{id}
     * 
     * Finds last modified timestamp of account.
     * @param id the account ID.
     * @return the timestamp.
     */
    @Secured({"ROLE_CM", "ROLE_USER", "ROLE_ADMIN"})
    @RequestMapping(value = "/lastmodified/{id}", method = {RequestMethod.GET, RequestMethod.HEAD})
    public @ResponseBody
    LastModifiedDate getLastModified(@PathVariable String id) {
        Account account = accountService.find(id);
        if (account == null) {
            logger.info("Failed to return last modified time of enabled/disabled account " + id);
            throw new CustomNotFoundException("An account with this ID does not exist, or you dont have permissions to access it.");
        }
        logger.info("Returning last modified time of enabled/disabled account " + id);
        return new LastModifiedDate(account.getLast_modified());
    }

    /**
     * GET|HEAD /account/current/user
     * 
     * Finds account currently logged in.
     * @param principal the principal.
     * @return the account.
     */
    @Secured({"ROLE_CM", "ROLE_USER", "ROLE_ADMIN"})
    @RequestMapping(value = "/current/user", method = {RequestMethod.GET, RequestMethod.HEAD})
    public @ResponseBody
    Account getCurrent(Principal principal) {
        if (principal == null) {
            logger.info("Denied account for user not logged in.");
            throw new CustomBadRequestException("You are not logged in.");
        }
        String name = principal.getName();
        logger.info("Attempting to acquire account for user " + name);
        Account account = accountService.findByUsername(name);
        if (account == null) {
            logger.info("Denied account for unmatched user " + name);
            throw new CustomBadRequestException("Current user " + name + " could not be matched to an account.");
        } else {
            logger.info("Returning account for approved user " + name);
        }
        return account;
    }

    /**
     * POST /account/
     * 
     * Adds an account
     * @param account the account.
     * @param result binding.
     * @return the account with ID assigned.
     */
    @Secured("ROLE_ADMIN")
    @RequestMapping(method = RequestMethod.POST)
    public @ResponseBody
    Account add(@RequestBody @Valid Account account, BindingResult result) {
        // Data model validation
        if (result.hasErrors()) {
            logger.info("Failed to add account. Missing fields?");
            throw new CustomBadRequestException(
                    "Account is invalid. Missing required fields?");
        }
        // Checks
        if (account.getId() != null) {
            logger.info("Failed to add account. ID set by user.");
            throw new CustomBadRequestException(
                    "An account must not have an ID. ID will be created automatically.");
        }
        if (accountService.findByUsername(account.getUsername()) != null) {
            logger.info("Failed to add account. Duplicate username.");
            throw new CustomBadRequestException(
                    "An account with this username already exists. Usernames are unique.");
        }
        // Encode Password
        account.setPassword(passwordEncoder.encode(account.getPassword()));
        Account acc = accountService.add(account);
        // HACK: Id needs to be autogenerated at persisting before updating DatasetInfos.
        acc.updateGranted_datasets();
        logger.info("Successfully added account " + acc.getId());
        return acc;
    }

    /**
     * PUT /account/{id}
     * 
     * Updates an account
     * @param id the account ID.
     * @param account the account.
     * @param result binding.
     */
    @Secured({"ROLE_CM", "ROLE_USER", "ROLE_ADMIN"})
    @RequestMapping(value = "{id}", method = RequestMethod.PUT)
    public @ResponseBody
    void update(@PathVariable String id, @RequestBody @Valid Account account, BindingResult result) {
        // Data model validation
        if (result.hasErrors()) {
            logger.info("Failed to update account. Missing fields?");
            throw new CustomBadRequestException(
                    "Account is invalid. Missing required fields?");
        }
        if (!id.equals(account.getId())) {
            logger.info("Failed to update account. ID mismatch.");
            throw new CustomBadRequestException(
                    "Account ID in request URL does not match ID in content body.");
        } else if (accountService.find(id) == null) {
            logger.info("Failed to update account. Missing or failed permissions.");
            throw new CustomBadRequestException(
                    "An account with this ID does not exist or you don't have permissions to access it.");
        } else if (accountService.findByUsername(account.getUsername()) != null) {
            if (!accountService.findByUsername(account.getUsername()).getId().equals(id)) {
                logger.info("Failed to update account. Duplicate username.");
                throw new CustomBadRequestException(
                        "Another account with this username exists already. Usernames are unique.");
            }
        }
        Account oldAcc = accountService.find(id);
        if (!oldAcc.getPassword().equals(account.getPassword())) {
            // Update password only on change (and encrypt cleartext if updated!).
            account.setPassword(passwordEncoder.encode(account.getPassword()));
        }
        logger.info("Successfully updated account " + account.getId());
        accountService.update(account);
    }

    /**
     * DELETE /account/{id}
     * 
     * Deletes an account
     * @param id the account ID.
     * @param cascade true to cascade delete.
     */
    @Secured("ROLE_ADMIN")
    @RequestMapping(value = "{id}", method = RequestMethod.DELETE)
    public @ResponseBody
    void delete(@PathVariable String id,
            @RequestParam(value = "cascade", required = false, defaultValue = "true") boolean cascade) {
        if (!accountService.deleteIsOk(id)) {
            logger.info("Failed to delete account " + id + " Missing permissions.");
            throw new CustomBadRequestException("You do not have permission to delete this account.");
        }
        accountService.delete(id);
        logger.info("Successfully deleted account " + id);
        if (cascade) {
            datasetinfoService.deleteForAccount(id);
            taskService.deleteForAccount(id);
            selectionService.deleteForAccount(id);
            datasetService.clearAccountCreator(id);
            pipelineexperimentService.clearAccount(id);
            logger.info("Successfully cascade-deleted dependencies for account " + id);
        }
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
    
    /**
     * Static access to account service.
     * @return the bean.
     */
    public static AccountServiceImpl getStaticAccountService() {
        return StaticContextAccessor.getBean(AccountController.class).getAccountService();
    }

    /**
     * Access to account service.
     * @return the bean.
     */
    public AccountServiceImpl getAccountService() {
        return this.accountService;
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
        logger.error("Unknown error in account controller: " + ex.getMessage());
        return new CustomInternalServerErrorResponse(ex.getMessage());
    }

}
