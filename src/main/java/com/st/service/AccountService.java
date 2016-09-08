package com.st.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.st.model.Account;
import com.st.model.AccountId;

/**
 * Interface for the account service.
 */
@Service
public interface AccountService {

    /**
     * Finds an account.
     * @param id the ID.
     * @return the account or null if not found.
     */
    public Account find(String id);

    /**
     * Finds an account by username.
     * @param username the username.
     * @return the account or null if not found.
     */
    public Account findByUsername(String username);

    /**
     * Finds all accounts ids granted a dataset.
     * @param datasetId the dataset ID.
     * @return the accounts ids or null if empty.
     */
    public List<AccountId> findIdsByDataset(String datasetId);

    /**
     * Finds all accounts.
     * @return the accounts.
     */
    public List<Account> list();

    /**
     * Finds all accounts ids.
     * @return the accounts ids.
     */
    public List<AccountId> listIds();
    
    /**
     * Adds an account.
     * @param account the account.
     * @return the account with ID assigned or null of the account was not created.
     */
    public Account add(Account account);

    /**
     * Updates an account.
     * @param account the account.
     * @return true if the updated went fine
     */
    public boolean update(Account account);

    /**
     * Deletes an account.
     * @param id the account ID.
     * @return true if the deletion was performed
     */
    public boolean delete(String id);

    /**
     * Checks that there is not any account with the name given
     * @param username the user name
     * @return true if there exist any account with the same name
     */
    public boolean accountNameExist(String username);

    /**
     * Checks that there is not any account with the name and id given
     * @param username the user name
     * @param id the unique account id
     * @return true if there exist any account with the same name and id
     */
    public boolean accountNameIdExist(String username, String id);
}
