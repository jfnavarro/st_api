package com.spatialtranscriptomics.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.spatialtranscriptomics.model.PipelineStats;

/**
 * Interface for the pipelinestats service.
 */
@Service
public interface PipelineStatsService {

    /**
     * Returns a stats.
     * @param id the stats ID.
     * @return the stats.
     */
    public PipelineStats find(String id);

    /**
     * Returns the stats of an experiment.
     * @param experimentId the experiment ID.
     * @return the stats.
     */
    public PipelineStats findByExperiment(String experimentId);

    /**
     * Returns all stats.
     * @return the list.
     */
    public List<PipelineStats> list();

    /**
     * Adds a stats.
     * @param stats the stats.
     * @return the stats with ID assigned.
     */
    public PipelineStats add(PipelineStats stats);

    /**
     * Updates a stats.
     * @param stats the stats.
     */
    public void update(PipelineStats stats);

    /**
     * Deletes a stats.
     * @param id the stats ID.
     */
    public void delete(String id);

    /**
     * Deletes the stats of an experiment.
     * @param experimentId the experiment.
     */
    public void deleteForExperiment(String experimentId);
}
