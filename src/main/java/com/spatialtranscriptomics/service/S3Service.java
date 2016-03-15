package com.spatialtranscriptomics.service;

import java.util.List;
import org.springframework.stereotype.Service;


/**
 * Interface for the S3 service.
 */
@Service
public interface S3Service {
    
    /**
     * Deletes the experiment data on Amazon S3 for an experiment.
     * @param experimentId the experiment ID.
     */
    public void deleteExperimentData(String experimentId);
}
