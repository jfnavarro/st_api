/*
 * Copyright (C) 2012 Spatial Transcriptomics AB
 * Read LICENSE for more information about licensing terms
 * Contact: Jose Fernandez Navarro <jose.fernandez.navarro@scilifelab.se>
 */
package com.spatialtranscriptomics.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.spatialtranscriptomics.model.Dataset;
import com.spatialtranscriptomics.model.MongoUserDetails;

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
     */
    public void update(Dataset ds);

    /**
     * Deletes a dataset.
     * @param id the ID.
     */
    public void delete(String id);

    /**
     * Sets all dataset created by an account to having an empty creator field.
     * @param accountId the account ID.
     */
    public void clearAccountCreator(String accountId);

    /**
     * Unables all datasets referencing an image alignment.
     * @param imalId the image alignment ID.
     */
    public void setUnabledForImageAlignment(String imalId);

    /**
     * Returns true if a user has access to a specific dataset.
     * @param datasetId the dataset ID.
     * @param user the user.
     * @return true ig user has access.
     */
    public boolean datasetIsGranted(String datasetId, MongoUserDetails user);

    /**
     * Finds a dataset by name, irrespective of the current user's privelegies.
     * @param name the dataset name.
     * @return the dataset.
     */
    public Dataset findByNameInternal(String name);

    /**
     * Returns true if the current user may delete a dataset.
     * @param id the dataset ID.
     * @return true if granted delete rights.
     */
    public boolean deleteIsOkForCurrUser(String id);
}
