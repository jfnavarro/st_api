/*
 * Copyright (C) 2012 Spatial Transcriptomics AB
 * Read LICENSE for more information about licensing terms
 * Contact: Jose Fernandez Navarro <jose.fernandez.navarro@scilifelab.se>
 */

package com.spatialtranscriptomics.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.spatialtranscriptomics.model.Task;

/**
 * Interface for the task service.
 */
@Service
public interface TaskService {

	public Task find(String id);
	
	public Task findByName(String name);
	
	public List<Task> findByAccount(String accountId);

	public List<Task> list();

	public Task add(Task task);

	public void update(Task task);

	public void delete(String id);
        
        public void deleteForAccount(String accountId);

}
