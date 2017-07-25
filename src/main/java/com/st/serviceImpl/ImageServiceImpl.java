package com.st.serviceImpl;

import com.amazonaws.AmazonClientException;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.st.model.FileMetadata;
import com.st.model.MongoUserDetails;
import com.st.service.ImageService;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * This class retrieves/stores images and image metadata from Amazon S3. It uses
 * the Amazon AWS Java SDK, see http://aws.amazon.com/sdkforjava/ The
 * AmazonS3Client is configured in the mvc-dispatcher-servlet.xml
 */
@Service
public class ImageServiceImpl implements ImageService {

    @Autowired
    AmazonS3Client s3Client;
    
    @Autowired
    MongoUserDetailsServiceImpl customUserDetailsService;
    
    private @Value("${s3.imagebucket}")
    String imageBucket;

    private static final Logger logger = Logger
            .getLogger(ImageServiceImpl.class);

    // ROLE_ADMIN: ok.
    // ROLE_CM:    ok.
    // ROLE_USER:  nope.
    @Override
    public List<FileMetadata> list() {
        List<FileMetadata> imageMetadataList = new ArrayList<>();
        MongoUserDetails currentUser = customUserDetailsService.loadCurrentUser();
        if (currentUser.isAdmin() || currentUser.isContentManager()) {
            ObjectListing objects = s3Client.listObjects(imageBucket);
            List<S3ObjectSummary> objs = objects.getObjectSummaries();
            for (S3ObjectSummary o : objs) {
                FileMetadata im = new FileMetadata();
                im.setFilename(o.getKey());
                im.setLastModified(new DateTime(o.getLastModified()));
                    im.setCreated(new DateTime(o.getLastModified()));
                im.setSize(o.getSize());
                imageMetadataList.add(im);
            }
        }
        return imageMetadataList;
    }

    // ROLE_ADMIN: ok.
    // ROLE_CM:    ok.
    // ROLE_USER:  ok.
    @Override
    public FileMetadata getImageMetadata(String filename) {
        List<FileMetadata> imList = this.list();
        for (FileMetadata im : imList) {
            if (im.getFilename().equals(filename)) {
                return im;
            }
        }
        return null;
    }

    // ROLE_ADMIN: ok.
    // ROLE_CM:    ok.
    // ROLE_USER:  ok.
    @Override
    public byte[] getCompressedImage(String filename) {
        try {
            S3ObjectInputStream in = s3Client.getObject(imageBucket, filename).getObjectContent();
            byte[] bytes = IOUtils.toByteArray(in);
            in.close();   // As soon as possible.
            return bytes;
        } catch (IOException | AmazonClientException e) {
            logger.error("Error getting JPEG " + filename + " from Amazon S3.", e);
            return null;
        }
    }

    // ROLE_ADMIN: ok.
    // ROLE_CM:    ok.
    // ROLE_USER:  ok.
    @Override
    public BufferedImage getBufferedImage(String filename) {
        try {
            S3ObjectInputStream in = s3Client.getObject(imageBucket, filename).getObjectContent();
            BufferedImage img = ImageIO.read(in);
            in.close();   // As soon as possible.
            return img;
        } catch (IOException e) {
            logger.error("Error getting BufferedImage " + filename + " from Amazon S3.", e);
            return null;
        }

    }

    // ROLE_ADMIN: ok.
    // ROLE_CM:    ok.
    // ROLE_USER:  nope.
    @Override
    public void add(String filename, BufferedImage img) {
        try {
            ObjectMetadata om = new ObjectMetadata();
            om.setContentType("image/jpeg");
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(img, "jpeg", baos);
            InputStream is = new ByteArrayInputStream(baos.toByteArray());
            s3Client.putObject(imageBucket, filename, is, om);
            logger.info("Added image from BuffereedImage " + filename + " to Amazon S3.");
        } catch (IOException | AmazonClientException e) {
            logger.error("Error adding image " + filename + " to Amazon S3:", e);
            throw new RuntimeException("Error adding image " + filename + " to Amazon S3", e);
        }
    }

    // ROLE_ADMIN: ok.
    // ROLE_CM:    ok.
    // ROLE_USER:  nope.
    @Override
    public void addCompressed(String filename, byte[] img) {
        try {
            ObjectMetadata om = new ObjectMetadata();
            om.setContentType("image/jpeg");
            InputStream is = new ByteArrayInputStream(img);
            s3Client.putObject(imageBucket, filename, is, om);
            logger.info("Added image from JPEG " + filename + " to Amazon S3.");
        } catch (AmazonClientException e) {
            logger.error("Error adding image " + filename + " to Amazon S3:" + e.getMessage());
            throw new RuntimeException("Error adding image " + filename + " to Amazon S3", e);
        }
    }

    // ROLE_ADMIN: ok.
    // ROLE_CM:    ok.
    // ROLE_USER:  nope.
    @Override
    public boolean delete(String filename) {
        MongoUserDetails currentUser = customUserDetailsService.loadCurrentUser();
        if (currentUser.isAdmin() || currentUser.isContentManager()) {
            try {
                s3Client.deleteObject(imageBucket, filename);
                logger.info("Deleted image " + filename + " from Amazon S3.");
                return true;
            } catch(AmazonClientException e) {
                logger.info("Could not delete image " + filename + " from Amazon S3.", e);
                return false;
            }
        }
        return false;
    }

}
