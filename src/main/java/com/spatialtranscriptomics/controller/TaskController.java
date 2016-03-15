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
import com.spatialtranscriptomics.model.Selection;
import com.spatialtranscriptomics.model.Task;
import com.spatialtranscriptomics.serviceImpl.TaskServiceImpl;
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
 * This class is Spring MVC controller class for the API endpoint "rest/task".
 * It implements the methods available at this endpoint.
 */
@Repository
@Controller
@RequestMapping("/rest/task")
public class TaskController {

    @SuppressWarnings("unused")
    private static final Logger logger = Logger.getLogger(TaskController.class);

    @Autowired
    TaskServiceImpl taskService;

    /**
     * GET|HEAD /task/
     * GET|HEAD /task/?account={accountId}
     * 
     * List / list for account.
     * @param accountId account ID.
     * @return list.
     */
    @Secured({"ROLE_USER", "ROLE_CM", "ROLE_ADMIN"})
    @RequestMapping(method = {RequestMethod.GET, RequestMethod.HEAD})
    public @ResponseBody
    List<Task> list(@RequestParam(value = "account", required = false) String accountId) {
        List<Task> tasks = null;
        if (accountId != null) {
            tasks = taskService.findByAccount(accountId);
            logger.info("Returning list of tasks for account " + accountId);
        } else {
            tasks = taskService.list();
            logger.info("Returning list of tasks");
        }
        if (tasks == null) {
            logger.info("Returning empty list of tasks");
            throw new CustomNotFoundException("No tasks found or you dont have permissions to access them.");
        }
        return tasks;
    }

    /**
     * GET|HEAD /task/{id}
     * 
     * Returns a task.
     * 
     * @param id the task ID.
     * @param ifModifiedSince last mod tag.
     * @return the task.
     */
    @Secured({"ROLE_CM", "ROLE_ADMIN"})
    @RequestMapping(value = "{id}", method = {RequestMethod.GET, RequestMethod.HEAD})
    public @ResponseBody
    HttpEntity<Task> get(@PathVariable String id, @RequestHeader(value="If-Modified-Since", defaultValue="") String ifModifiedSince) {
        Task task = taskService.find(id);
        if (task == null) {
            throw new CustomNotFoundException("A task with this ID does not exist or you dont have permissions to access it.");
        }
        // Check if already newest.
        DateTime reqTime = DateOperations.parseHTTPDate(ifModifiedSince);
        if (reqTime != null) {
            DateTime resTime = task.getLast_modified() == null ? new DateTime(2012,1,1,0,0) : task.getLast_modified();
            // NOTE: Only precision within day.
            resTime = new DateTime(resTime.getYear(), resTime.getMonthOfYear(), resTime.getDayOfMonth(), resTime.getHourOfDay(), resTime.getMinuteOfHour(), resTime.getSecondOfMinute());
            if (!resTime.isAfter(reqTime)) {
                logger.info("Not returning selection " + id + " since not modified");
                throw new CustomNotModifiedException("This task has not been modified");
            }
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Cache-Control", "public, must-revalidate, no-transform");
        headers.add("Vary", "Accept-Encoding");
        headers.add("Last-modified", DateOperations.getHTTPDateSafely(task.getLast_modified()));
        HttpEntity<Task> entity = new HttpEntity<Task>(task, headers);
        logger.info("Returning task " + id);
        return entity;
    }

    /**
     * GET|HEAD /task/lastmodified/{id}
     *
     * Finds a task's last modified timestamp.
     * @param id the task ID.
     * @return the timestamp.
     */
    @Secured({"ROLE_CM", "ROLE_ADMIN"})
    @RequestMapping(value = "/lastmodified/{id}", method = {RequestMethod.GET, RequestMethod.HEAD})
    public @ResponseBody
    LastModifiedDate getLastModified(@PathVariable String id) {
        Task task = taskService.find(id);
        if (task == null) {
            logger.info("Failed to return last modified time of task " + id);
            throw new CustomNotFoundException("A task with this ID does not exist or you dont have permissions to access it.");
        }
        logger.info("Returning last modified time of task " + id);
        return new LastModifiedDate(task.getLast_modified());
    }

    /**
     * POST /task/
     * 
     * Adds a task
     * @param task the task.
     * @param result binding.
     * @return the task with ID assigned.
     */
    @Secured({"ROLE_CM", "ROLE_ADMIN"})
    @RequestMapping(method = RequestMethod.POST)
    public @ResponseBody
    Task add(@RequestBody @Valid Task task, BindingResult result) {
        // Task validation
        if (result.hasErrors()) {
            logger.info("Failed to add task. Missing fields?");
            throw new CustomBadRequestException("Task is invalid. Missing required fields?");
        }
        if (task.getId() != null) {
            logger.info("Failed to add task. ID set by user.");
            throw new CustomBadRequestException("The task you want to add must not have an ID. The ID will be autogenerated.");
        }
        if (taskService.findByName(task.getName()) != null) {
            logger.info("Failed to add task. Duplicate name.");
            throw new CustomBadRequestException("A task with this name already exists. Task names are unique.");
        }
        logger.info("Successfully added task " + task.getId());
        return taskService.add(task);
    }

    /**
     * PUT /task/{id}
     * 
     * Updates a task.
     * @param id the task ID.
     * @param task the task.
     * @param result binding.
     */
    @Secured({"ROLE_CM", "ROLE_ADMIN"})
    @RequestMapping(value = "{id}", method = RequestMethod.PUT)
    public @ResponseBody
    void update(@PathVariable String id, @RequestBody @Valid Task task,
            BindingResult result) {
        // Task validation
        if (result.hasErrors()) {
            logger.info("Failed to update task " + id + " . Missing fields?");
            throw new CustomBadRequestException("Task is invalid. Missing required fields?");
        }
        if (!id.equals(task.getId())) {
            logger.info("Failed to update task " + id + ". ID mismatch.");
            throw new CustomBadRequestException("Task ID in request URL does not match ID in content body.");
        } else if (taskService.find(id) == null) {
            logger.info("Failed to update task " + id + ". Duplicate username.");
            throw new CustomBadRequestException("A task with this ID does not exist or you don't have permissions to access it.");
        } else {
            taskService.update(task);
            logger.info("Successfully updated task " + id);
        }
    }

    /**
     * DELETE /task/{id}
     * 
     * Deletes a task.
     * @param id the task ID.
     */
    @Secured({"ROLE_CM", "ROLE_ADMIN"})
    @RequestMapping(value = "{id}", method = RequestMethod.DELETE)
    public @ResponseBody
    void delete(@PathVariable String id) {
        taskService.delete(id);
        logger.info("Successfully deleted task " + id);
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
        logger.error("Unknown error in task controller: " + ex.getMessage());
        return new CustomInternalServerErrorResponse(ex.getMessage());
    }

}
