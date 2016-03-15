package com.spatialtranscriptomics.controller;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.spatialtranscriptomics.exceptions.BadRequestResponse;
import com.spatialtranscriptomics.exceptions.CustomBadRequestException;
import com.spatialtranscriptomics.exceptions.CustomInternalServerErrorException;
import com.spatialtranscriptomics.exceptions.CustomInternalServerErrorResponse;
import com.spatialtranscriptomics.exceptions.CustomNotFoundException;
import com.spatialtranscriptomics.exceptions.CustomNotModifiedException;
import com.spatialtranscriptomics.exceptions.NotFoundResponse;
import com.spatialtranscriptomics.exceptions.NotModifiedResponse;
import com.spatialtranscriptomics.model.PipelineStats;
import com.spatialtranscriptomics.serviceImpl.PipelineStatsServiceImpl;

/**
 * This class is Spring MVC controller class for the API endpoint
 * "rest/pipelinestats". It implements the methods available at this endpoint.
 */
@Repository
@Controller
@RequestMapping("/rest/pipelinestats")
public class PipelineStatsController {

    @SuppressWarnings("unused")
    private static final Logger logger = Logger.getLogger(PipelineStatsController.class);

    @Autowired
    PipelineStatsServiceImpl pipelinestatsService;

    /**
     * GET|HEAD /pipelinestats/
     * GET|HEAD /pipelinestats/?pipelineexperiment={experimentId}
     * 
     * Returns a list of stats.
     * 
     * @param experimentId experiment ID.
     * @return the list.
     */
    @Secured({"ROLE_CM", "ROLE_ADMIN"})
    @RequestMapping(method = {RequestMethod.GET, RequestMethod.HEAD})
    public @ResponseBody
    List<PipelineStats> list(@RequestParam(value = "pipelineexperiment", required = false) String experimentId) {
        List<PipelineStats> pipelinestats = null;
        if (experimentId != null) {
            pipelinestats = new ArrayList<PipelineStats>(1);
            PipelineStats stats = pipelinestatsService.findByExperiment(experimentId);
            if (stats != null) {
                logger.info("Returning pipeline stats for experiment " + experimentId);
                pipelinestats.add(stats);
            }
        } else {
            logger.info("Returning list of pipeline stats");
            pipelinestats = pipelinestatsService.list();
        }
        if (pipelinestats == null) {
            logger.info("Returning empty list of pipeline stats");
            throw new CustomNotFoundException(
                    "No PipelineStats found or you dont have permissions to access them.");
        }
        return pipelinestats;
    }

    /**
     * GET|HEAD /pipelinestats/{id}
     * 
     * Returns a stats.
     * 
     * @param id the stats ID.
     * @return the stats.
     */
    @Secured({"ROLE_CM", "ROLE_ADMIN"})
    @RequestMapping(value = "{id}", method = {RequestMethod.GET, RequestMethod.HEAD})
    public @ResponseBody
    PipelineStats get(@PathVariable String id) {
        PipelineStats pipelinestats = pipelinestatsService.find(id);
        if (pipelinestats == null) {
            logger.info("Failed to return pipeline stats " + id + ". Permission denied or missing.");
            throw new CustomNotFoundException(
                    "A PipelineStats with this ID does not exist or you dont have permissions to access it.");
        }
        logger.info("Returning pipeline stats " + id);
        return pipelinestats;
    }

    /**
     * POST /pipelinestats/
     * 
     * Adds a pipeline stats
     * @param pipelinestats the pipeline stats.
     * @param result binding.
     * @return the pipeline stats with ID assigned.
     */
    @Secured({"ROLE_CM", "ROLE_ADMIN"})
    @RequestMapping(method = RequestMethod.POST)
    public @ResponseBody
    PipelineStats add(@RequestBody @Valid PipelineStats pipelinestats, BindingResult result) {
        // PipelineStats validation
        if (result.hasErrors()) {
            logger.info("Failed to add pipeline stats. Missing fields?");
            throw new CustomBadRequestException(
                    "PipelineStats is invalid. Missing required fields?");
        }
        if (pipelinestats.getId() != null) {
            logger.info("Failed to add pipeline stats. ID set by user.");
            throw new CustomBadRequestException(
                    "The PipelineStats you want to add must not have an ID. The ID will be autogenerated.");
        }
        if (pipelinestatsService.findByExperiment(pipelinestats.getExperiment_id()) != null) {
            logger.info("Failed to add pipeline stats. Duplicate name.");
            throw new CustomBadRequestException(
                    "An PipelineStats for this PipelineExperiment already exists.");
        }
        return pipelinestatsService.add(pipelinestats);
    }

    /**
     * PUT /pipelinestats/{id}
     * 
     * Updates a pipeline experiment.
     * @param id the pipeline experiment ID.
     * @param pipelinestats the pipeline stats.
     * @param result binding.
     */
    @Secured({"ROLE_CM", "ROLE_ADMIN"})
    @RequestMapping(value = "{id}", method = RequestMethod.PUT)
    public @ResponseBody
    void update(@PathVariable String id, @RequestBody @Valid PipelineStats pipelinestats, BindingResult result) {

        // PipelineStats validation
        if (result.hasErrors()) {
            logger.info("Failed to update pipeline stats" + id + " . Missing fields?");
            throw new CustomBadRequestException(
                    "PipelineStats is invalid. Missing required fields?");
        }

        if (!id.equals(pipelinestats.getId())) {
            logger.info("Failed to update pipeline stats " + id + ". ID mismatch.");
            throw new CustomBadRequestException(
                    "PipelineStats ID in request URL does not match ID in content body.");
        } else if (pipelinestatsService.find(id) == null) {
            logger.info("Failed to update pipeline stats " + id + ". Duplicate username.");
            throw new CustomBadRequestException(
                    "A PipelineStats with this ID does not exist or you don't have permissions to access it.");
        } else {
            logger.info("Successfully updated pipeline stats " + id);
            pipelinestatsService.update(pipelinestats);
        }
    }

    /**
     * DELETE /pipelinestats/{id}
     * 
     * Deletes a pipeline stats.
     * @param id the chip ID.
     */
    @Secured({"ROLE_CM", "ROLE_ADMIN"})
    @RequestMapping(value = "{id}", method = RequestMethod.DELETE)
    public @ResponseBody
    void delete(@PathVariable String id) {
        logger.info("Successfully deleted pipeline stats " + id);
        pipelinestatsService.delete(id);
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
        logger.error("Unknown error in pipeline stats controller: " + ex.getMessage());
        return new CustomInternalServerErrorResponse(ex.getMessage());
    }

}
