package com.spatialtranscriptomics.service;


import java.util.List;
import org.springframework.stereotype.Service;

/*
 * Copyright (C) 2012 Spatial Transcriptomics AB
 * Read LICENSE for more information about licensing terms
 * Contact: Jose Fernandez Navarro <jose.fernandez.navarro@scilifelab.se>
 */

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
    
    /**
     * Deletes all the experiments that were created by account given 
     * @param accountId the ID of the account
     */
    public void deleteExperimentDataForAccount(String accountId);
}
