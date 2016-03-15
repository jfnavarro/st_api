package com.spatialtranscriptomics.controller;

import com.spatialtranscriptomics.exceptions.BadRequestResponse;
import com.spatialtranscriptomics.exceptions.CustomBadRequestException;
import com.spatialtranscriptomics.exceptions.CustomInternalServerErrorException;
import com.spatialtranscriptomics.exceptions.CustomInternalServerErrorResponse;
import com.spatialtranscriptomics.exceptions.CustomNotFoundException;
import com.spatialtranscriptomics.exceptions.CustomNotModifiedException;
import com.spatialtranscriptomics.exceptions.NotFoundResponse;
import com.spatialtranscriptomics.exceptions.NotModifiedResponse;
import com.spatialtranscriptomics.model.LastModifiedDate;
import com.spatialtranscriptomics.model.PipelineExperiment;
import com.spatialtranscriptomics.serviceImpl.PipelineExperimentServiceImpl;
import com.spatialtranscriptomics.serviceImpl.PipelineStatsServiceImpl;
import com.spatialtranscriptomics.serviceImpl.S3ServiceImpl;
import com.spatialtranscriptomics.util.DateOperations;
import java.util.List;
import javax.validation.Valid;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * This class is Spring MVC controller class for the API endpoint
 * "rest/pipelineexperiment". It implements the methods available at this
 * endpoint.
 */
@Repository
@Controller
@RequestMapping("/rest/pipelineexperiment")
public class PipelineExperimentController {

    @SuppressWarnings("unused")
    private static final Logger logger = Logger.getLogger(PipelineExperimentController.class);

    @Autowired
    PipelineExperimentServiceImpl pipelineexperimentService;

    @Autowired
    PipelineStatsServiceImpl pipelinestatsService;

    @Autowired
    S3ServiceImpl s3Service;

    /**
     * GET|HEAD /pipelineexperiment/
     * GET|HEAD /pipelineexperiment/?account={accountId}
     * 
     * Returns a list of experiments.
     * 
     * @param accountId the account.
     * @return the list.
     */
    @Secured({"ROLE_CM", "ROLE_ADMIN"})
    @RequestMapping(method = {RequestMethod.GET, RequestMethod.HEAD})
    public @ResponseBody
    List<PipelineExperiment> list(@RequestParam(value = "account", required = false) String accountId) {
        List<PipelineExperiment> pipelineexperiments;
        if (accountId != null) {
            pipelineexperiments = pipelineexperimentService.findByAccount(accountId);
            logger.info("Returning list of pipeline experiments for account " + accountId);
        } else {
            pipelineexperiments = pipelineexperimentService.list();
            logger.info("Returning list of pipeline experiments");
        }
        if (pipelineexperiments == null) {
            logger.info("Returning empty list of pipeline experiments");
            throw new CustomNotFoundException(
                    "No PipelineExperiment found or you don't have permissions to access them.");
        }
        return pipelineexperiments;
    }

    /**
     * GET|HEAD /pipelineexperiment/{id}
     * 
     * Returns an experiment.
     * 
     * @param id the experiment ID.
     * @param ifModifiedSince last mod tag.
     * @return the experiment.
     */
    @Secured({"ROLE_CM", "ROLE_ADMIN"})
    @RequestMapping(value = "{id}", method = {RequestMethod.GET, RequestMethod.HEAD})
    public @ResponseBody
    HttpEntity<PipelineExperiment> get(@PathVariable String id, @RequestHeader(value="If-Modified-Since", defaultValue="") String ifModifiedSince) {
        PipelineExperiment pipelineexperiment = pipelineexperimentService.find(id);
        if (pipelineexperiment == null) {
            logger.info("Failed to return pipeline experiment " + id + ". Permission denied or missing.");
            throw new CustomNotFoundException(
                    "A PipelineExperiment with this ID does not exist or you dont have permissions to access it.");
        }
        // Check if already newest.
        DateTime reqTime = DateOperations.parseHTTPDate(ifModifiedSince);
        if (reqTime != null) {
            DateTime resTime = pipelineexperiment.getLast_modified() == null ? new DateTime(2012,1,1,0,0) : pipelineexperiment.getLast_modified();
            // NOTE: Only precision within day.
            resTime = new DateTime(resTime.getYear(), resTime.getMonthOfYear(), resTime.getDayOfMonth(), resTime.getHourOfDay(), resTime.getMinuteOfHour(), resTime.getSecondOfMinute());
            if (!resTime.isAfter(reqTime)) {
                logger.info("Not returning pipeline experiment " + id + " since not modified");
                throw new CustomNotModifiedException("This pipeline experiment has not been modified");
            }
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Cache-Control", "public, must-revalidate, no-transform");
        headers.add("Vary", "Accept-Encoding");
        headers.add("Last-modified", DateOperations.getHTTPDateSafely(pipelineexperiment.getLast_modified()));
        HttpEntity<PipelineExperiment> entity = new HttpEntity<PipelineExperiment>(pipelineexperiment, headers);
        logger.info("Returning pipeline experiment " + id);
        return entity;
    }

    /**
     * GET|HEAD /pipelineexperiment/lastmodified/{id}
     *
     * Finds a pipeline experiment's last modified timestamp.
     * @param id the pipeline experiment ID.
     * @return the timestamp.
     */
    @Secured({"ROLE_CM", "ROLE_ADMIN"})
    @RequestMapping(value = "/lastmodified/{id}", method = {RequestMethod.GET, RequestMethod.HEAD})
    public @ResponseBody
    LastModifiedDate getLastModified(@PathVariable String id) {
        PipelineExperiment pipelineexperiment = pipelineexperimentService.find(id);
        if (pipelineexperiment == null) {
            logger.info("Failed to return last modified time of pipeline experiment " + id);
            throw new CustomNotFoundException(
                    "A pipeline experiment with this ID does not exist or you dont have permissions to access it.");
        }
        logger.info("Returning last modified time of pipeline experiment " + id);
        return new LastModifiedDate(pipelineexperiment.getLast_modified());
    }

    /**
     * POST /pipelineexperiment/
     * 
     * Adds a pipeline experiment
     * @param pipelineexperiment the pipeline experiment.
     * @param result binding.
     * @return the pipeline experiment with ID assigned.
     */
    @Secured({"ROLE_CM", "ROLE_ADMIN"})
    @RequestMapping(method = RequestMethod.POST)
    public @ResponseBody
    PipelineExperiment add(@RequestBody @Valid PipelineExperiment pipelineexperiment, BindingResult result) {
        // PipelineExperiment validation
        if (result.hasErrors()) {
            logger.info("Failed to add pipeline experiment. Missing fields?");
            throw new CustomBadRequestException(
                    "PipelineExperiment is invalid. Missing required fields?");
        }
        if (pipelineexperiment.getId() != null) {
            logger.info("Failed to add pipeline experiment. ID set by user.");
            throw new CustomBadRequestException(
                    "The PipelineExperiment you want to add must not have an ID. The ID will be autogenerated.");
        }
        if (pipelineexperimentService.findByName(pipelineexperiment.getName()) != null) {
            logger.info("Failed to add pipeline experiment. Duplicate name.");
            throw new CustomBadRequestException(
                    "An PipelineExperiment with this name already exists. PipelineExperiment names are unique.");
        }
        logger.info("Successfully added pipeline experiment " + pipelineexperiment.getId());
        return pipelineexperimentService.add(pipelineexperiment);
    }

    /**
     * PUT /pipelineexperiment/{id}
     * 
     * Updates a pipeline experiment.
     * @param id the pipeline experiment ID.
     * @param pipelineexperiment the pipeline experiment.
     * @param result binding.
     */
    @Secured({"ROLE_CM", "ROLE_ADMIN"})
    @RequestMapping(value = "{id}", method = RequestMethod.PUT)
    public @ResponseBody
    void update(@PathVariable String id, @RequestBody @Valid PipelineExperiment pipelineexperiment,
            BindingResult result) {
        // PipelineExperiment validation
        if (result.hasErrors()) {
            logger.info("Failed to update pipeline experiment " + id + " . Missing fields?");
            throw new CustomBadRequestException(
                    "PipelineExperiment is invalid. Missing required fields?");
        }
        if (!id.equals(pipelineexperiment.getId())) {
            logger.info("Failed to update pipeline experiment " + id + ". ID mismatch.");
            throw new CustomBadRequestException(
                    "PipelineExperiment ID in request URL does not match ID in content body.");
        } else if (pipelineexperimentService.find(id) == null) {
            logger.info("Failed to update pipeline experiment " + id + ". Duplicate username.");
            throw new CustomBadRequestException(
                    "A PipelineExperiment with this ID does not exist or you don't have permissions to access it.");
        } else {
            logger.info("Successfully updated pipeline experiment " + id);
            pipelineexperimentService.update(pipelineexperiment);
        }
    }

    /**
     * DELETE /pipelineexperiment/{id}
     * 
     * Deletes a pipeline experiment.
     * @param id the chip ID.
     */
    @Secured({"ROLE_CM", "ROLE_ADMIN"})
    @RequestMapping(value = "{id}", method = RequestMethod.DELETE)
    public @ResponseBody
    void delete(@PathVariable String id) {
        if (!pipelineexperimentService.deleteIsOkForCurrUser(id)) {
            logger.info("Failed to delete pipeline experiment " + id + " Missing permissions.");
            throw new CustomBadRequestException("You do not have permission to delete this experiment.");
        }
        s3Service.deleteExperimentData(id);
        pipelineexperimentService.delete(id);
        pipelinestatsService.deleteForExperiment(id);
        logger.info("Successfully deleted pipeline experiment " + id);
    }

    @ExceptionHandler(CustomNotModifiedException.class)
    @ResponseStatus(value = HttpStatus.NOT_MODIFIED)
    public @ResponseBody
    NotModifiedResponse handleNotModifiedException(CustomNotModifiedException ex) {
        return new NotModifiedResponse(ex.getMessage());
    }

    @ExceptionHandler(CustomNotFoundException.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public @ResponseBody
    NotFoundResponse handleNotFoundException(CustomNotFoundException ex) {
        return new NotFoundResponse(ex.getMessage());
    }

    @ExceptionHandler(CustomBadRequestException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public @ResponseBody
    BadRequestResponse handleBadRequestException(CustomBadRequestException ex) {
        return new BadRequestResponse(ex.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public @ResponseBody
    CustomInternalServerErrorResponse handleRuntimeException(CustomInternalServerErrorException ex) {
        logger.error("Unknown error in pipeline experiment controller: " + ex.getMessage());
        return new CustomInternalServerErrorResponse(ex.getMessage());
    }

}
