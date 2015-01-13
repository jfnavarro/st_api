/*
 * Copyright (C) 2012 Spatial Transcriptomics AB
 * Read LICENSE for more information about licensing terms
 * Contact: Jose Fernandez Navarro <jose.fernandez.navarro@scilifelab.se>
 */
package com.spatialtranscriptomics.serviceImpl;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.spatialtranscriptomics.model.DatasetInfo;
import com.spatialtranscriptomics.model.FeaturesMetadata;
import com.spatialtranscriptomics.model.MongoUserDetails;
import com.spatialtranscriptomics.service.FeaturesService;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

/**
 * This class retrieves/stores feature files and features metadata from Amazon
 * S3. It uses the Amazon AWS Java SDK, see http://aws.amazon.com/sdkforjava/
 * The AmazonS3Client is configured in the mvc-dispatcher-servlet.xml
 */
@Service
public class FeaturesServiceImpl implements FeaturesService {

    @Autowired
    AmazonS3Client s3Client;

    @Autowired
    MongoOperations mongoTemplateUserDB;

    @Autowired
    MongoUserDetailsServiceImpl customUserDetailsService;

    private @Value("${s3.featuresbucket}")
    String featuresBucket;

    private @Value("${s3.featurespath}")
    String featuresPath;

    private static final Logger logger = Logger.getLogger(ImageServiceImpl.class);

    @Override
    public boolean datasetIsGranted(String datasetId, MongoUserDetails user) {
        List<DatasetInfo> dsis = mongoTemplateUserDB.find(new Query(Criteria.where("dataset_id").is(datasetId).and("account_id").is(user.getId())), DatasetInfo.class);
        return (dsis != null && dsis.size() > 0);
    }

    // ROLE_ADMIN: ok.
    // ROLE_CM:    ok.
    // ROLE_USER:  nope.
    @Override
    public List<FeaturesMetadata> listMetadata() {
        
        ObjectListing objects = s3Client.listObjects(featuresBucket);

        List<S3ObjectSummary> objs = objects.getObjectSummaries();

        List<FeaturesMetadata> featuresMetadataList = new ArrayList<FeaturesMetadata>();
        for (S3ObjectSummary o : objs) {
            FeaturesMetadata fm = new FeaturesMetadata();
            String fn = o.getKey();
            fm.setFilename(fn);
            fm.setDatasetId(fn.substring(0, fn.length() - 3)); // Remove .gz
            fm.setLastModified(new DateTime(o.getLastModified()));
            fm.setCreated(new DateTime(o.getLastModified()));
            fm.setSize(o.getSize());
            featuresMetadataList.add(fm);
        }
        return featuresMetadataList;
    }

    // ROLE_ADMIN: ok.
    // ROLE_CM:    ok.
    // ROLE_USER:  ok.
    @Override
    public FeaturesMetadata getMetadata(String id) {
        List<FeaturesMetadata> featureList = this.listMetadata();
        for (FeaturesMetadata fm : featureList) {
            if (fm.getDatasetId().equals(id)) {
                return fm;
            }
        }
        return null;
    }

    // ROLE_ADMIN: all.
    // ROLE_CM:    all.
    // ROLE_USER:  granted datasets.
    @Override
    public InputStream find(String id) {
        MongoUserDetails currentUser = customUserDetailsService.loadCurrentUser();
        if (currentUser.isContentManager() || currentUser.isAdmin() || datasetIsGranted(id, currentUser)) {
            try {
                String filename = id + ".gz";
                // We cache the contents in a byte array so that the S3 stream can be closed ASAP.
                ByteArrayOutputStream bos = new ByteArrayOutputStream(30 * 1024 * 1024);
                S3ObjectInputStream in = s3Client.getObject(featuresBucket, filename).getObjectContent();
                IOUtils.copy(in, bos);
                in.close();   // ASAP!
                InputStream bis = new ByteArrayInputStream(bos.toByteArray());
                bos.close();
                return bis;
            } catch (Exception ex) {
                logger.error("Failed to download features for dataset " + id);
                return null;
            }
        } else {
            return null; // user has no permissions on dataset
        }
    }

    // ROLE_ADMIN: ok.
    // ROLE_CM:    ok.
    // ROLE_USER:  nope.
    /**
     * Returns true if the file already existed and was updated, false if added.
     */
    @Override
    public boolean addUpdate(String id, byte[] file) {
        
        ObjectMetadata om = new ObjectMetadata();
        om.setContentType("application/json");
        om.setContentEncoding("gzip");
        InputStream is = new ByteArrayInputStream(file);

        String filename = id + ".gz";
        boolean exists = (getMetadata(id) != null);
        if (exists) {
            s3Client.putObject(featuresBucket, filename, is, om);
            logger.info("Updated features for dataset " + id + "on Amazon S3");
            return true;
        } else {
            s3Client.putObject(featuresBucket, filename, is, om);
            logger.info("Added features for dataset " + id + "on Amazon S3");
            return false;
        }
    }

    // ROLE_ADMIN: ok.
    // ROLE_CM:    ok.
    // ROLE_USER:  nope.
    @Override
    public void delete(String id) {
        String filename = id + ".gz";
        s3Client.deleteObject(featuresBucket, filename);
        logger.info("Deleted features for dataset " + id + " from Amazon S3");
    }

}
