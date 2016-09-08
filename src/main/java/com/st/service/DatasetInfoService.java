package com.st.service;

import java.util.List;
import org.springframework.stereotype.Service;
import com.st.model.DatasetInfo;

/**
 * Interface for the datasetinfo service.
 */
@Service
public interface DatasetInfoService {

    /**
     * Finds a dataset info.
     * @param id the ID.
     * @return the dataset info or null if not found.
     */
    public DatasetInfo find(String id);

    /**
     * Adds a dataset info.
     * @param ds the info to add.
     * @return the info with ID assigned or null if not found.
     */
    public DatasetInfo add(DatasetInfo ds);

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

    /**
     * Updates all the entries for account with the given datasets
     * @param accountId the id of the account to be updated
     * @param datasetsIds the new list of granted datasets ids
     */
    public void updateForAccount(String accountId, List<String> datasetsIds);
    
    /**
     * Updates all the entries for dataset with the given account
     * @param datasetId the id of the dataset to be updated
     * @param accountsIds the new list of ids of granted accounts
     */
    public void updateForDataset(String datasetId, List<String> accountsIds);
      
    /**
     * Delete all the entries for the given dataset id
     * @param datasetId 
     */
    public void deleteForDataset(String datasetId);

    /**
     * Delete all the entries for the given account id
     * @param accountId 
     */
    public void deleteForAccount(String accountId);

}
