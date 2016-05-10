package com.st.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.st.model.Selection;

/**
 * Interface for the selection service.
 */
@Service
public interface SelectionService {

    /**
     * Returns a selection.
     * @param id the selection ID.
     * @return the selection.
     */
    public Selection find(String id);

    /**
     * Returns a selection by name.
     * @param name the selection name.
     * @return the selection.
     */
    public Selection findByName(String name);

    /**
     * Returns all selections.
     * @return the list.
     */
    public List<Selection> list();

    /**
     * Returns all selections for an account.
     * @param accountId the account ID.
     * @return the selections.
     */
    public List<Selection> findByAccount(String accountId);

    /**
     * Returns all selections for a dataset.
     * @param datasetId the dataset ID.
     * @return the list.
     */
    public List<Selection> findByDataset(String datasetId);

    /**
     * Returns all selections for a task.
     * @param taskId the task ID.
     * @return the list.
     */
    public List<Selection> findByTask(String taskId);

    /**
     * Adds a selection.
     * @param sel the selection.
     * @return the selection with ID assigned.
     */
    public Selection add(Selection sel);

    /**
     * Updates a seelction.
     * @param sel the selection.
     */
    public void update(Selection sel);

    /**
     * Deletes a selection.
     * @param id the selection ID.
     */
    public void delete(String id);

    /**
     * Deletes all selection for a given dataset.
     * @param datasetId the dataset ID.
     */
    public void deleteForDataset(String datasetId);

    /**
     * Deletes all selections for an account.
     * @param accountId the account ID.
     */
    public void deleteForAccount(String accountId);
}
