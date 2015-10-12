package com.spatialtranscriptomics.filecontroller.validator;

import com.spatialtranscriptomics.exceptions.CustomBadRequestException;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Iterator;

/**
 * The ImageBytesInputStreamValidator verifies that the incoming InputStream represents an image
 * with the correct content type. It does this by loading the entire image into memory.
 * TODO: This class needs to be tested.
 */
public class ImageBytesInputStreamValidator extends BytesInputStreamValidator {

    @Override
    protected void validateBytes(byte[] bytes, String contentType, String filename) throws IOException {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);

        ImageInputStream iis = ImageIO.createImageInputStream(inputStream);

        // There should be only one imageReader
        Iterator<ImageReader> imageReaderIterator = ImageIO.getImageReadersByMIMEType(contentType);

        if(!imageReaderIterator.hasNext()) {

            // We could not find an image reader for the given content type.
            String message = String.format("Could not get image reader for image with filename %s and content type %s",
                    filename, contentType);
            throw new CustomBadRequestException(message);
        }

        ImageReader imageReader = imageReaderIterator.next();

        validateContentType(imageReader, contentType, filename);
        validateImage(imageReader, filename);

        if(imageReaderIterator.hasNext()) {

            // There should be no more than one image reader.
            String message = String.format("There should not be more than one image reader for the image with filename %s.",
                    filename);
            throw new CustomBadRequestException(filename);
        }
    }

    /**
     * Validates that the given content type actually matches the content type in the file.
     * @param imageReader
     * @param contentType
     * @param filename
     * @throws IOException
     */
    private void validateContentType(ImageReader imageReader, String contentType, String filename) throws IOException {
        String formatName = imageReader.getFormatName();

        if(!contentType.equals(formatName)) {
            String message = String.format("Specified content type %s and the format for the image %s do not match for file %s",
                    filename, contentType, formatName);

            throw new CustomBadRequestException(message);
        }
    }

    /**
     * Validates that the file can be parsed and converted to an image.
     * @param imageReader
     * @param filename
     * @throws IOException
     */
    private void validateImage(ImageReader imageReader, String filename) throws IOException {

        BufferedImage image = null;

        try {
            image = imageReader.read(0);
        } catch (IOException ex) {
            String message = String.format("Could not read image data for image %s", filename);
            throw new CustomBadRequestException(message);
        }

        if(image == null) {
            String message = String.format("Null image for image %s", filename);
            throw new CustomBadRequestException(message);
        }
    }
}
