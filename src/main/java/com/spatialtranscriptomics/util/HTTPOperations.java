/*
 * Copyright (C) 2012 Spatial Transcriptomics AB
 * Read LICENSE for more information about licensing terms
 * Contact: Jose Fernandez Navarro <jose.fernandez.navarro@scilifelab.se>
 */

package com.spatialtranscriptomics.util;

import org.springframework.http.HttpHeaders;
import org.joda.time.DateTime;
import org.springframework.http.MediaType;

/**
 * Mist operations for HTTP operations, mainly related to caching
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
}
