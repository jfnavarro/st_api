package com.st.service;

import com.st.model.FileMetadata;
import java.awt.image.BufferedImage;
import java.util.List;

/**
 * Interface for the image service.
 */
public interface ImageService {

    /**
     * Lists all image metadata.
     * @return the list.
     */
    public List<FileMetadata> list();

    /**
     * Returns image metadata for an image.
     * @param filename the image name.
     * @return metadata.
     */
    public FileMetadata getImageMetadata(String filename);

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
     * @return true if the image was deleted successfully
     */
    public boolean delete(String filename);
}
