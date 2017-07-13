package com.st.serviceImpl;

import com.amazonaws.AmazonClientException;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.st.model.FileMetadata;
import com.st.model.MongoUserDetails;
import com.st.service.FileService;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.stereotype.Service;

/**
 * This class retrieves/stores files and metadata from Amazon
 * S3. It uses the Amazon AWS Java SDK, see http://aws.amazon.com/sdkforjava/
 * The AmazonS3Client is configured in the mvc-dispatcher-servlet.xml
 */
@Service
public class FileServiceImpl implements FileService {

    @Autowired
    AmazonS3Client s3Client;

    @Autowired
    MongoOperations mongoTemplateUserDB;

    @Autowired
    MongoUserDetailsServiceImpl customUserDetailsService;

    private @Value("${s3.featuresbucket}")
    String featuresBucket;

    private static final Logger logger = Logger.getLogger(ImageServiceImpl.class);

    // ROLE_ADMIN: ok.
    // ROLE_CM:    ok.
    // ROLE_USER:  ok.
    @Override
    public List<FileMetadata> listMetadata() {
        List<FileMetadata> metadataList = new ArrayList<>();
        ObjectListing objects = s3Client.listObjects(featuresBucket);
        List<S3ObjectSummary> objs = objects.getObjectSummaries();
        for (S3ObjectSummary o : objs) {
            FileMetadata fm = new FileMetadata();
            String fn = o.getKey();
            fm.setFilename(fn);
            fm.setDatasetId(fn.substring(0, fn.length() - 3)); // Remove .gz
            fm.setLastModified(new DateTime(o.getLastModified()));
            fm.setCreated(new DateTime(o.getLastModified()));
            fm.setSize(o.getSize());
            metadataList.add(fm);
        }
        return metadataList;
    }

    // ROLE_ADMIN: ok.
    // ROLE_CM:    ok.
    // ROLE_USER:  ok.
    @Override
    public FileMetadata getMetadata(String id) {
        List<FileMetadata> metadataList = this.listMetadata();
        for (FileMetadata fm : metadataList) {
            if (fm.getDatasetId().equals(id)) {
                return fm;
            }
        }
        return null;
    }

    // ROLE_ADMIN: ok.
    // ROLE_CM:    ok.
    // ROLE_USER:  ok.
    @Override
    public InputStream find(String id) {
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
        } catch (AmazonClientException | IOException e) {
            logger.error("Failed to download file " + id, e);
            return null;
        }
    }

    // ROLE_ADMIN: ok.
    // ROLE_CM:    ok.
    // ROLE_USER:  nope.
    /**
     * Returns true if the file correctly added/updated.
     * @param id the dataset Id 
     * @param file the byte array representation of the file
     */
    @Override
    public boolean addUpdate(String id, byte[] file) {
        MongoUserDetails currentUser = customUserDetailsService.loadCurrentUser();
        if (currentUser.isUser()) {
            return false;
        }
        // Create the meta data for S3
        ObjectMetadata om = new ObjectMetadata();
        om.setContentType("text/plain");
        om.setContentEncoding("gzip");
        InputStream is = new ByteArrayInputStream(file);
        // Filename is the dataset Id given as input plus the gz extension
        String filename = id + ".gz";
        boolean exists = (getMetadata(id) != null);
        
        try {
            s3Client.putObject(featuresBucket, filename, is, om);
        } catch(AmazonClientException e) {
            logger.info("Error putting file " + id + "on Amazon S3", e);
            return false;           
        }
        
        if (exists) {
            logger.info("Updated file " + id + "on Amazon S3");
        } else {
            logger.info("Added file " + id + "on Amazon S3");
        }
        
        return true;
    }
    
    // ROLE_ADMIN: ok.
    // ROLE_CM:    ok.
    // ROLE_USER:  nope.
    @Override
    public boolean delete(String id) {
        MongoUserDetails currentUser = customUserDetailsService.loadCurrentUser();
        if (currentUser.isUser()) {
            return false;
        }
        String filename = id + ".gz";
        try {
            s3Client.deleteObject(featuresBucket, filename);
            logger.info("Deleted file " + id + " from Amazon S3");
            return true;
        } catch(AmazonClientException e) {
            logger.info("Error file " + id + " on Amazon S3.", e);
            return false;
        }
    }

}
