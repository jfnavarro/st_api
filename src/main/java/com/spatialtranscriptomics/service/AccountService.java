/*
 * Copyright (C) 2012 Spatial Transcriptomics AB
 * Read LICENSE for more information about licensing terms
 * Contact: Jose Fernandez Navarro <jose.fernandez.navarro@scilifelab.se>
 */
package com.spatialtranscriptomics.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.spatialtranscriptomics.model.Account;

/**
 * Interface for the account service.
 */
@Service
public interface AccountService {

    /**
     * Finds an account.
     * @param id the ID.
     * @return the account.
     */
    public Account find(String id);

    /**
     * Finds an account by username.
     * @param username the username.
     * @return the account.
     */
    public Account findByUsername(String username);

    /**
     * Finds all accounts granted a dataset.
     * @param datasetId the dataset ID.
     * @return the accounts.
     */
    public List<Account> findByDataset(String datasetId);

    /**
     * Finds all accounts.
     * @return the accounts.
     */
    public List<Account> list();

    /**
     * Adds an account.
     * @param account the account.
     * @return the account with ID assigned.
     */
    public Account add(Account account);

    /**
     * Updates an account.
     * @param account the account.
     */
    public void update(Account account);

    /**
     * Deletes an account.
     * @param id the account ID.
     */
    public void delete(String id);

    /**
     * Verifies that deletion is OK based on the current user.
     * @param id the account ID.
     * @return true if OK.
     */
    public boolean deleteIsOkForCurrUser(String id);
}
