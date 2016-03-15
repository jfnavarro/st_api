package com.spatialtranscriptomics.controller;

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

import com.spatialtranscriptomics.component.StaticContextAccessor;
import com.spatialtranscriptomics.exceptions.BadRequestResponse;
import com.spatialtranscriptomics.exceptions.CustomBadRequestException;
import com.spatialtranscriptomics.exceptions.CustomInternalServerErrorException;
import com.spatialtranscriptomics.exceptions.CustomInternalServerErrorResponse;
import com.spatialtranscriptomics.exceptions.CustomNotFoundException;
import com.spatialtranscriptomics.exceptions.CustomNotModifiedException;
import com.spatialtranscriptomics.exceptions.NotFoundResponse;
import com.spatialtranscriptomics.exceptions.NotModifiedResponse;
import com.spatialtranscriptomics.model.DatasetInfo;
import com.spatialtranscriptomics.serviceImpl.DatasetInfoServiceImpl;

/**
 * This class is Spring MVC controller class for the API endpoint "rest/datasetinfo". It implements the methods available at this endpoint.
 */

@Repository
@Controller
@RequestMapping("/rest/datasetinfo")
public class DatasetInfoController {

    @SuppressWarnings("unused")
    private static final Logger logger = Logger.getLogger(DatasetInfoController.class);

    @Autowired
    DatasetInfoServiceImpl datasetinfoService;

    /**
     * GET|HEAD /datasetinfo/ GET|HEAD /datasetinfo/?account={accountId}
     * GET|HEAD /datasetinfo/?dataset={datasetId}
     *
     * Lists enabled dataset infos.
     *
     * @param accountId the account ID.
     * @param datasetId the dataset ID.
     * @return the list.
     */
    @Secured({"ROLE_USER", "ROLE_CM", "ROLE_ADMIN"})
    @RequestMapping(method = {RequestMethod.GET, RequestMethod.HEAD})
    public @ResponseBody
    List<DatasetInfo> list(
            @RequestParam(value = "account", required = false) String accountId,
            @RequestParam(value = "dataset", required = false) String datasetId
    ) {
        List<DatasetInfo> datasetinfos;
        if (accountId != null) {
            datasetinfos = datasetinfoService.findByAccount(accountId);
            logger.info("Returning list of dataset infos for account " + accountId);
        } else if (datasetId != null) {
            datasetinfos = datasetinfoService.findByDataset(datasetId);
            logger.info("Returning list of dataset infos for dataset " + datasetId);
        } else {
            datasetinfos = datasetinfoService.list();
            logger.info("Returning list of dataset infos");
        }
        if (datasetinfos == null) {
            logger.info("Returning empty list of datasetinfos");
            throw new CustomNotFoundException("No DatasetInfos found or you dont have permissions to access them.");
        }
        return datasetinfos;
    }

    /**
     * GET|HEAD /datasetinfo/{id}
     *
     * Returns a dataset info.
     *
     * @param id the dataset info ID.
     * @return the dataset info.
     */
    @Secured({"ROLE_USER", "ROLE_CM", "ROLE_ADMIN"})
    @RequestMapping(value = "{id}", method = {RequestMethod.GET, RequestMethod.HEAD})
    public @ResponseBody
    DatasetInfo get(@PathVariable String id) {
        DatasetInfo datasetinfo = datasetinfoService.find(id);
        if (datasetinfo == null) {
            logger.info("Failed to reeturn dataset info " + id);
            throw new CustomNotFoundException("A DatasetInfo with this ID does not exist or you dont have permissions to access it.");
        }
        logger.info("Returning dataset info " + id);
        return datasetinfo;
    }

    /**
     * POST /datasetinfo/
     * 
     * Adds a dataset info.
     * @param datasetinfo the dataset info.
     * @param result the binding.
     * @return the dataset info with ID assigned.
     */
    @Secured({"ROLE_CM", "ROLE_ADMIN"})
    @RequestMapping(method = RequestMethod.POST)
    public @ResponseBody
    DatasetInfo add(@RequestBody @Valid DatasetInfo datasetinfo, BindingResult result) {
        // Data model validation
        if (result.hasErrors()) {
            logger.info("Failed to add dataset info. Missing fields?");
            throw new CustomBadRequestException("DatasetInfo is invalid. Missing required fields?");
        }
        if (datasetinfo.getId() != null) {
            logger.info("Failed to add dataset info. ID set by user.");
            throw new CustomBadRequestException("The DatasetInfo you want to add must not have an ID. The ID will be autogenerated.");
        }
        return datasetinfoService.add(datasetinfo);
    }

    /**
     * PUT /datasetinfo/{id}
     * 
     * Updates a dataset info.
     * 
     * @param id the dataset info ID.
     * @param datasetinfo the dataset info.
     * @param result the binding.
     */
    @Secured({"ROLE_CM", "ROLE_ADMIN"})
    @RequestMapping(value = "{id}", method = RequestMethod.PUT)
    public @ResponseBody
    void update(@PathVariable String id, @RequestBody @Valid DatasetInfo datasetinfo,
            BindingResult result) {
        // Data model validation
        if (result.hasErrors()) {
            logger.info("Failed to update dataset info. Missing fields?");
            throw new CustomBadRequestException(
                    "DatasetInfo is invalid. Missing required fields?");
        }
        if (!id.equals(datasetinfo.getId())) {
            logger.info("Failed to update dataset info. ID mismatch.");
            throw new CustomBadRequestException(
                    "DatasetInfo ID in request URL does not match ID in content body.");
        } else if (datasetinfoService.find(id) == null) {
            logger.info("Failed to update dataset info. Missing or failed permissions.");
            throw new CustomBadRequestException(
                    "A DatasetInfo with this ID does not exist or you don't have permissions to access it.");
        } else {
            logger.info("Successfully updated datasetinfo " + datasetinfo.getId());
            datasetinfoService.update(datasetinfo);
        }
    }

    /**
     * DELETE /datasetinfo/{id}
     * 
     * Deletes a dataset info.
     * @param id the dataset info ID.
     */
    @Secured({"ROLE_CM", "ROLE_ADMIN"})
    @RequestMapping(value = "{id}", method = RequestMethod.DELETE)
    public @ResponseBody
    void delete(@PathVariable String id) {
        datasetinfoService.delete(id);
        logger.info("Successfully deleted dataset info " + id);
    }

    /**
     * Static access to datasetinfo service.
     * @return the service.
     */
    public static DatasetInfoServiceImpl getStaticDatasetInfoService() {
        return StaticContextAccessor.getBean(DatasetInfoController.class).getDatasetInfoService();
    }

    /**
     * Access to datasetinfo service.
     * @return the service.
     */
    public DatasetInfoServiceImpl getDatasetInfoService() {
        return this.datasetinfoService;
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
        logger.error("Unknown error in dataset info controller: " + ex.getMessage());
        return new CustomInternalServerErrorResponse(ex.getMessage());
    }

}
