package com.spatialtranscriptomics.serviceImpl;

import com.spatialtranscriptomics.service.S3Service;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.DeleteObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

    private @Value("${s3.imagebucket}")
    String imagesBucket;

    private @Value("${s3.imagepath}")
    String imagesPath;

    private @Value("${s3.pipelinebucket}")
    String pipelineBucket;

    private @Value("${s3.experimentspath}")
    String experimentsPath;

    @Override
    public void deleteExperimentData(String experimentId) {
        String path = experimentsPath + experimentId;
        ObjectListing objects = s3Client.listObjects(pipelineBucket, path);
        List<S3ObjectSummary> objs = objects.getObjectSummaries();
        if (objs.isEmpty()) {
            return;
        }
        List<DeleteObjectsRequest.KeyVersion> keysToDelete = new ArrayList<DeleteObjectsRequest.KeyVersion>();
        for (S3ObjectSummary o : objs) {
            DeleteObjectsRequest.KeyVersion kv = new DeleteObjectsRequest.KeyVersion(o.getKey());
            keysToDelete.add(kv);
        }
        if (keysToDelete.isEmpty()) {
            return;
        }
        DeleteObjectsRequest req = new DeleteObjectsRequest(pipelineBucket);
        req.setKeys(keysToDelete);
        s3Client.deleteObjects(req);
        logger.info("Deleted experiment data for pipeline experiment " + experimentId + " from Amazon S3.");
    }

}
