package com.spatialtranscriptomics.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.spatialtranscriptomics.model.Task;

/**
 * Interface for the task service.
 */
@Service
public interface TaskService {

    /**
     * Finds a task.
     * @param id the task ID.
     * @return the task.
     */
    public Task find(String id);

    /**
     * Finds a task by name.
     * @param name the task name.
     * @return the task.
     */
    public Task findByName(String name);

    /**
     * Lists all tasks for an account.
     * @param accountId the account ID.
     * @return the list.
     */
    public List<Task> findByAccount(String accountId);

    /**
     * Lists all tasks.
     * @return the tasks.
     */
    public List<Task> list();

    /**
     * Adds a task.
     * @param task the task.
     * @return the task with ID assigned.
     */
    public Task add(Task task);

    /**
     * Updates a task.
     * @param task the task.
     */
    public void update(Task task);

    /**
     * Deletes a task.
     * @param id the task ID.
     */
    public void delete(String id);

    /**
     * Deletes all tasks for an account.
     * @param accountId the account ID.
     */
    public void deleteForAccount(String accountId);

}
