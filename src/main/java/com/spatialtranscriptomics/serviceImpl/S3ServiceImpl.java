/*
 * Copyright (C) 2012 Spatial Transcriptomics AB
 * Read LICENSE for more information about licensing terms
 * Contact: Jose Fernandez Navarro <jose.fernandez.navarro@scilifelab.se>
 */
package com.spatialtranscriptomics.serviceImpl;

import com.amazonaws.AmazonClientException;
import com.spatialtranscriptomics.service.S3Service;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.DeleteObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.spatialtranscriptomics.model.PipelineExperiment;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

/**
 * Class for things pertaining to Amazon S3 that goes outside of the ordinary
 * services.
 */
@Service
public class S3ServiceImpl implements S3Service {

    @SuppressWarnings("unused")
    private static final Logger logger = Logger.getLogger(S3ServiceImpl.class);

    @Autowired
    AmazonS3Client s3Client;

    private @Value("${s3.pipelinebucket}")
    String pipelineBucket;

    private @Value("${s3.experimentspath}")
    String experimentsPath;
    
    @Autowired
    MongoOperations mongoTemplateExperimentDB;

    @Override
    public void deleteExperimentData(String experimentId) {
        try {
            String path = experimentsPath + experimentId;
            ObjectListing objects = s3Client.listObjects(pipelineBucket, path);
        
            List<S3ObjectSummary> objs = objects.getObjectSummaries();
            if (objs.isEmpty()) {
                return;
            }
        
            List<DeleteObjectsRequest.KeyVersion> keysToDelete = 
                    new ArrayList<DeleteObjectsRequest.KeyVersion>();
            for (S3ObjectSummary o : objs) {
                DeleteObjectsRequest.KeyVersion kv = 
                        new DeleteObjectsRequest.KeyVersion(o.getKey());
                keysToDelete.add(kv);
            }
        
            if (keysToDelete.isEmpty()) {
                return;
            }
        
            DeleteObjectsRequest req = new DeleteObjectsRequest(pipelineBucket);
            req.setKeys(keysToDelete);
            s3Client.deleteObjects(req);
            logger.info("Deleted experiment data for pipeline experiment " 
                    + experimentId + " from Amazon S3.");
        } catch (AmazonClientException e) {
            logger.info("Error deleting experiment data for pipeline experiment " 
                    + experimentId + " from Amazon S3.");
        }
    }

    @Override
    public void deleteExperimentDataForAccount(String accountId) {
        List<PipelineExperiment> experiments = mongoTemplateExperimentDB.find(
                new Query(Criteria.where("account_id").is(accountId)), PipelineExperiment.class);
        
        if (experiments == null) {
            logger.info("No Pipeline Experiments were found by the given account ID " + accountId);
            return;
        }
        
        for (PipelineExperiment exp : experiments) {
            deleteExperimentData(exp.getId());
        }
        
    }

}
