
package com.st.util;

import org.springframework.http.HttpHeaders;
import org.joda.time.DateTime;
import org.springframework.http.MediaType;

import java.util.LinkedList;
import java.util.List;

/**
 * Misc operations for HTTP operations, mainly related to caching
 */
public class HTTPOperations {
    
    /**
     * Returns the HTTP headers with cache information for the corresponding
     * last modified date
     * @param lastmodified the last modified date.
     * @return the HTTP headers.
     */
    public static HttpHeaders getHTTPHeaderWithCache(DateTime lastmodified) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Cache-Control", "public, must-revalidate, no-transform");
        headers.add("Vary", "Accept-Encoding");
        headers.add("Last-modified", DateOperations.getHTTPDateSafely(lastmodified));
        return headers;
    }

    /**
     * Parses an Accept header into a list of acceptable content types.
     * @param acceptHeader
     * @return The list of the acceptable content types.
     */
    public static List<String> parseAcceptHeader(String acceptHeader) {

        String[] parts = acceptHeader.split(",");

        List<String> headers = new LinkedList<>();
        for(String contentType : parts) {
            headers.add(contentType.trim());
        }

        return headers;
    }
}
