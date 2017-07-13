package com.st.controller;

import com.st.component.StaticContextAccessor;
import com.st.exceptions.BadRequestResponse;
import com.st.exceptions.CustomBadRequestException;
import com.st.exceptions.CustomInternalServerErrorException;
import com.st.exceptions.CustomInternalServerErrorResponse;
import com.st.exceptions.CustomNotFoundException;
import com.st.exceptions.CustomNotModifiedException;
import com.st.exceptions.NotFoundResponse;
import com.st.exceptions.NotModifiedResponse;
import com.st.model.Account;
import com.st.model.AccountId;
import com.st.model.Dataset;
import com.st.model.LastModifiedDate;
import com.st.serviceImpl.AccountServiceImpl;
import com.st.serviceImpl.DatasetInfoServiceImpl;
import com.st.serviceImpl.DatasetServiceImpl;
import com.st.serviceImpl.FileServiceImpl;
import com.st.util.DateOperations;
import static com.st.util.DateOperations.checkIfModified;
import static com.st.util.HTTPOperations.getHTTPHeaderWithCache;
import java.security.Principal;
import java.util.Iterator;
import java.util.List;
import javax.validation.Valid;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
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
    DatasetServiceImpl datasetService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    FileServiceImpl featuresService;

    /**
     * GET|HEAD /account/
     * 
     * Lists enabled/disabled accounts.
     * @param onlyEnabled when true filter out disabled accounts
     * @return the list.
     */
    @Secured({"ROLE_CM", "ROLE_ADMIN"})
    @RequestMapping(method = {RequestMethod.GET, RequestMethod.HEAD})
    public @ResponseBody List<Account> list(
            @RequestParam(value = "onlyEnabled", required = false, defaultValue = "false") boolean onlyEnabled) {     
        List<Account> accounts = accountService.list();
        if (accounts == null) {
            logger.info("Returning empty list of accounts");
            throw new CustomNotFoundException("No accounts found or you don't have permissions to access");
        } 
        // remove disabled accounts
        if (onlyEnabled) {
            Iterator<Account> it = accounts.iterator();
            while (it.hasNext()) {
                Account account = it.next(); // must be called before you can call i.remove()
                if (!account.isEnabled()) {
                    it.remove();
                }
            }
        }
        logger.info("Returning list of accounts");
        return accounts;
    }
    
    
    /**
     * GET|HEAD /account/ids/
     * GET|HEAD /account/ids/?dataset={datasetId}
     * 
     * Lists enabled accounts ids.
     * @param datasetId the dataset ID.
     * @return the list.
     */
    @Secured({"ROLE_CM", "ROLE_ADMIN"})
    @RequestMapping(value = "/ids/", method = {RequestMethod.GET, RequestMethod.HEAD})
    public @ResponseBody List<AccountId> listIds(
            @RequestParam(value = "dataset", required = false) String datasetId) {
        List<AccountId> accounts;
        if (datasetId != null) {
            accounts = accountService.findIdsByDataset(datasetId);
        } else {
            accounts = accountService.listIds();
        }
        if (accounts == null) {
            logger.info("Returning empty list of accounts ids");
            throw new CustomNotFoundException("No accounts found or "
                    + "you don't have permissions to access");
        } 
        logger.info("Returning list of accounts ids");
        return accounts;
    }
    
    /**
     * GET|HEAD /account/{id}
     * 
     * Finds enabled account.
     * @param id the account ID.
     * @param ifModifiedSince request timestamp.
     * @param onlyEnabled when true filter out disabled accounts
     * @return the account.
     */
    @Secured({"ROLE_CM", "ROLE_USER", "ROLE_ADMIN"})
    @RequestMapping(value = "{id}", method = {RequestMethod.GET, RequestMethod.HEAD})
    public @ResponseBody HttpEntity<Account> get(
            @PathVariable String id, 
            @RequestHeader(value="If-Modified-Since", defaultValue="") String ifModifiedSince,
            @RequestParam(value = "onlyEnabled", required = false, defaultValue = "true") boolean onlyEnabled) {
        
        Account account = accountService.find(id);
        if (account == null || (onlyEnabled && !account.isEnabled())) {
            logger.error("Failed to return account " + id);
            throw new CustomNotFoundException("An account with this ID doesn't exist, "
                    + "is disabled, or you don't have permissions to access");
        }
        
        // Check if already newest.
        DateTime reqTime = DateOperations.parseHTTPDate(ifModifiedSince);
        if (reqTime != null && !checkIfModified(account.getLast_modified(), reqTime)) {
            logger.error("Not returning account " + id + " since not modified");
            throw new CustomNotModifiedException("This account has not been modified");
        }
        
        HttpEntity<Account> entity = new HttpEntity<>(account, 
                getHTTPHeaderWithCache(account.getLast_modified()));
        logger.info("Returning account " + id);
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
            logger.error("Failed to return last modified time of enabled/disabled account " + id);
            throw new CustomNotFoundException("An account with this ID does not exist, "
                    + "or you dont have permissions to access it.");
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
        logger.info("Attempting to adquire account for user " + name);
        Account account = accountService.findByUsername(name);
        if (account == null) {
            logger.error("Denied account for unmatched user " + name);
            throw new CustomBadRequestException("Current user " + name + 
                    " could not be matched to an account.");
        }
        logger.info("Returning account for approved user " + name);
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
            logger.error("Failed to add account. Missing fields?");
            throw new CustomBadRequestException(
                    "Account is invalid. Missing required fields?");
        }
        // Checks
        if (account.getId() != null) {
            logger.error("Failed to add account. ID set by user.");
            throw new CustomBadRequestException(
                    "An account must not have an ID. ID will be created automatically.");
        }
        if (accountService.findByUsername(account.getUsername()) != null) {
            logger.error("Failed to add account. Duplicate username.");
            throw new CustomBadRequestException(
                    "An account with this username already exists. Usernames are unique.");
        }
        // Encode Password
        account.setPassword(passwordEncoder.encode(account.getPassword()));
        Account acc = accountService.add(account);
        if (acc != null) {
            // We update granted datasets once the Account has been created (auto asigned Id)
            datasetinfoService.updateForAccount(acc.getId(), acc.getGranted_datasets());
            logger.info("Successfully added account " + acc.getId());
            return acc;
        } else {
            logger.error("Failed to add account. Permissions problem.");
            throw new CustomBadRequestException(
                    "There was an error creating the account, probably permissions.");            
        }
    }

    /**
     * PUT /account/{id}
     * 
     * Updates an account
     * @param id the account ID.
     * @param account the account.
     * @param result binding.
     */
    @Secured({"ROLE_CM", "ROLE_ADMIN", "ROLE_USER"})
    @RequestMapping(value = "{id}", method = RequestMethod.PUT)
    public @ResponseBody
    void update(@PathVariable String id, @RequestBody @Valid Account account, BindingResult result) {
        // Data model validation
        if (result.hasErrors()) {
            logger.error("Failed to update account. Missing fields?");
            throw new CustomBadRequestException(
                    "Account is invalid. Missing required fields?");
        }
        if (!id.equals(account.getId())) {
            logger.error("Failed to update account. ID mismatch.");
            throw new CustomBadRequestException(
                    "Account ID in request URL does not match ID in content body.");
        } else if (accountService.find(id) == null) {
            logger.error("Failed to update account. Missing or failed permissions.");
            throw new CustomBadRequestException(
                    "An account with this ID does not exist or you don't have permissions to access it.");
        } else if (accountService.accountNameIdExist(account.getUsername(), id)) {
            logger.error("Failed to update account. Duplicate username.");
            throw new CustomBadRequestException(
                    "Another account with this username exists already. Usernames are unique.");
        }
        Account oldAcc = accountService.find(id);
        if (!oldAcc.getPassword().equals(account.getPassword())) {
            // Update password only on change (and encrypt cleartext if updated!).
            account.setPassword(passwordEncoder.encode(account.getPassword()));
        }
        if (accountService.update(account)) {
            //TODO should be performed only if the list of granted accounts changed
            datasetinfoService.updateForAccount(account.getId(), account.getGranted_datasets());
            logger.info("Successfully updated account " + account.getId());
        } else {
            logger.error("Failed to update account. Permissions error.");
            throw new CustomBadRequestException(
                    "Coult not update account, probably permissions error.");     
        }
    }

    /**
     * DELETE /account/{id}
     * 
     * Deletes an account
     * @param id the account ID.
     * @param cascade true to cascade delete.
     */
    @Secured({"ROLE_ADMIN"})
    @RequestMapping(value = "{id}", method = RequestMethod.DELETE)
    public @ResponseBody
    void delete(@PathVariable String id,
            @RequestParam(value = "cascade", required = false, defaultValue = "false") boolean cascade) {
        if (accountService.delete(id)) {
            //TODO A error-safe transactional approach should be used here
            if (cascade) {
                // Deleting the datasets created by the user
                List<Dataset> datasets = datasetService.findByAccount(id);
                for (Dataset dataset : datasets) {
                    featuresService.delete(dataset.getId());
                    datasetService.delete(dataset.getId());
                }
                logger.info("Successfully cascade-deleted dependencies for account " + id);
            } else {
                // Datasets and experiments are not deleted as they can be
                // accessed by other users so we reset their account field
                datasetService.clearAccountCreator(id);
            }
            // Always delete the dataset info and selections for the deleted account
            datasetinfoService.deleteForAccount(id);
            logger.info("Successfully deleted account " + id);
        } else {
            logger.error("Failed to delete account " + id + " Missing permissions.");
            throw new CustomBadRequestException("You do not have permission to delete this account.");           
        }
    }

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
        logger.warn(ex);
        return new BadRequestResponse(ex.getMessage());
    }

    @ExceptionHandler(CustomInternalServerErrorException.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public @ResponseBody
    CustomInternalServerErrorResponse 
        handleInternalServerException(CustomInternalServerErrorException ex) {
        logger.error(ex);
        return new CustomInternalServerErrorResponse(ex.getMessage());
    }
    
    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public @ResponseBody
    CustomInternalServerErrorResponse 
        handleRuntimeException(CustomInternalServerErrorException ex) {
        logger.error("Unknown error in account controller: " + ex.getMessage());
        return new CustomInternalServerErrorResponse(ex.getMessage());
    }

}
