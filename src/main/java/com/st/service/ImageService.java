package com.st.service;

import java.awt.image.BufferedImage;
import java.util.List;

import com.st.model.ImageMetadata;

/**
 * Interface for the image service.
 */
public interface ImageService {

    /**
     * Lists all image metadata.
     * @return the list.
     */
    public List<ImageMetadata> list();

    /**
     * Returns image metadata for an image.
     * @param filename the image name.
     * @return metadata.
     */
    public ImageMetadata getImageMetadata(String filename);

    /**
     * Returns an image as a BufferedImage.
     * @param filename the image name.
     * @return the image.
     */
    public BufferedImage getBufferedImage(String filename);

    /**
     * Returns an image a JPEG stream.
     * @param filename the image name.
     * @return the image.
     */
    public byte[] getCompressedImage(String filename);

    /**
     * Adds a JPEG image via a BufferedImage.
     * @param filename the image name.
     * @param img the image.
     */
    public void add(String filename, BufferedImage img);

    /**
     * Adds an image as a JPEG.
     * @param filename the image name.
     * @param img the JPEG image, stored in BASE64.
     */
    public void addCompressed(String filename, byte[] img);

    /**
     * Deletes an image.
     * @param filename the image name.
     */
    public boolean delete(String filename);
}
