package com.spatialtranscriptomics.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.spatialtranscriptomics.model.PipelineExperiment;

/**
 * Interface for the pipelineexperiment service.
 */
@Service
public interface PipelineExperimentService {

    /**
     * Returns an experiment.
     * @param id the experiment ID.
     * @return the experiment.
     */
    public PipelineExperiment find(String id);

    /**
     * Finds an experiment by name.
     * @param name the experiment name.
     * @return the experiment.
     */
    public PipelineExperiment findByName(String name);

    /**
     * Finds all experiments of an account.
     * @param accountId the account ID.
     * @return the list.
     */
    public List<PipelineExperiment> findByAccount(String accountId);

    /**
     * Lists all experiments.
     * @return the list.
     */
    public List<PipelineExperiment> list();

    /**
     * Adds an experiment.
     * @param experiment the experiment.
     * @return the experiment with ID assigned.
     */
    public PipelineExperiment add(PipelineExperiment experiment);

    /**
     * Updates an experiment.
     * @param experiment the experiment.
     */
    public void update(PipelineExperiment experiment);

    /**
     * Deletes an experiment.
     * @param id the experiment ID.
     */
    public void delete(String id);

    /**
     * Clears the account field for all experiments of a certain account.
     * @param accountId the account ID.
     */
    public void clearAccount(String accountId);

    /**
     * Returns true if the current user may delete an experiment.
     * @param id the experiment ID.
     * @return true if delete is OK.
     */
    public boolean deleteIsOkForCurrUser(String id);
}
