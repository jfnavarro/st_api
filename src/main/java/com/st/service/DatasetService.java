package com.st.service;

import java.util.List;
import org.springframework.stereotype.Service;
import com.st.model.Dataset;
import com.st.model.MongoUserDetails;

/**
 * Interface for the dataset service.
 */
@Service
public interface DatasetService {

    /**
     * Finds a dataset.
     * @param id the ID.
     * @return the dataset.
     */
    public Dataset find(String id);

    /**
     * Finds a dataset by name.
     * @param name the dataset name.
     * @return the dataset.
     */
    public Dataset findByName(String name);

    /**
     * Finds all datasets of an account.
     * @param accountId the account ID.
     * @return the list.
     */
    public List<Dataset> findByAccount(String accountId);

    /**
     * Adds a dataset.
     * @param ds the dataset.
     * @return the dataset with ID assigned.
     */
    public Dataset add(Dataset ds);

    /**
     * Lists all datasets.
     * @return the list.
     */
    public List<Dataset> list();

    /**
     * Updates a dataset.
     * @param ds the dataset.
     * @return true if it was updated
     */
    public boolean update(Dataset ds);

    /**
     * Deletes a dataset.
     * @param id the ID.
     * @return true if it was deleted
     */
    public boolean delete(String id);

    /**
     * Sets all dataset created by an account to having an empty creator field.
     * @param accountId the account ID.
     */
    public void clearAccountCreator(String accountId);

    /**
     * Returns true if a user has access to a specific dataset.
     * @param datasetId the dataset ID.
     * @param user the user.
     * @return true ig user has access.
     */
    public boolean datasetIsGranted(String datasetId, MongoUserDetails user);

    /**
     * Checks if exists a dataset with the name given
     * @param name the dataset name.
     * @return true if the dataset exists.
     */
    public boolean datasetNameExist(String name);
    
    /**
     * Checks if a dataset with the same Id and name exists
     * @param name the dataset name
     * @param id the unique id of the dataset
     * @return true if the dataset exists
     */
    public boolean datasetNameIdExist(String name, String id);
}
