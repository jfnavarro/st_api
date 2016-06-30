package com.st.controller;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * Created by henriktreadup on 6/29/16.
 */

@Controller
@RequestMapping("/rest/file/**")
public class FileController {

    private static String FILE_REST_PREFIX = "/rest/file/";

    Logger log = Logger.getLogger(FileController.class);

    private String getPath(HttpServletRequest request) {
        String path = (String) request.getAttribute( HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
        return path.substring(FILE_REST_PREFIX.length());
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<InputStreamResource> get(HttpServletRequest request) {
        String path = getPath(request);

        String content = "The contents of the file.";
        byte[] contentBytes = content.getBytes(StandardCharsets.UTF_8);
        final int contentLengthOfStream = contentBytes.length;

        InputStream inputStream = new ByteArrayInputStream(contentBytes);

        InputStreamResource inputStreamResource = new InputStreamResource(inputStream);
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setContentLength(contentLengthOfStream);
        return new ResponseEntity(inputStreamResource, responseHeaders, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    public void post(HttpServletRequest request, @RequestBody byte[] content) throws IOException {
        String contentType = request.getHeader("Content-Length");

        String contentString = new String(content, StandardCharsets.UTF_8);

        log.info("Recieved file content: " + contentString);
    }
}

