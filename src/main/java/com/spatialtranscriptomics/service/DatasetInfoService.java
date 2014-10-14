/*
 * Copyright (C) 2012 Spatial Transcriptomics AB
 * Read LICENSE for more information about licensing terms
 * Contact: Jose Fernandez Navarro <jose.fernandez.navarro@scilifelab.se>
 */
package com.spatialtranscriptomics.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.spatialtranscriptomics.model.DatasetInfo;

/**
 * Interface for the datasetinfo service.
 */
@Service
public interface DatasetInfoService {

    /**
     * Finds a dataset info.
     * @param id the ID.
     * @return the dataset info.
     */
    public DatasetInfo find(String id);

    /**
     * Adds a dataset info.
     * @param ds the info to add.
     * @return the info with ID assigned.
     */
    public DatasetInfo add(DatasetInfo ds);

    /**
     * Lists all dataset infos.
     * @return the list.
     */
    public List<DatasetInfo> list();

    /**
     * Finds all dataset infos for an account.
     * @param accountId the account ID.
     * @return the list.
     */
    public List<DatasetInfo> findByAccount(String accountId);

    /**
     * Finds all dataset infos for a dataset.
     * @param datasetId the dataset ID.
     * @return the list.
     */
    public List<DatasetInfo> findByDataset(String datasetId);

    /**
     * Updates a dataset info.
     * @param dsi the info.
     */
    public void update(DatasetInfo dsi);

    /**
     * Deletes a dataset info.
     * @param id the ID.
     */
    public void delete(String id);

    public void deleteForDataset(String datasetId);

    public void deleteForAccount(String accountId);

}
