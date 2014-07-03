/*
 * Copyright (C) 2012 Spatial Transcriptomics AB
 * Read LICENSE for more information about licensing terms
 * Contact: Jose Fernandez Navarro <jose.fernandez.navarro@scilifelab.se>
 */

package com.spatialtranscriptomics.model;

import java.util.Date;
import org.joda.time.DateTime;

/**
 * This interface defines the Task model. Applications that use the API must implement the same model.
 */
public interface ITask {

	public String getId();

	public void setId(String id);

	public String getName();

	public void setName(String name);

	public String getStatus();

	public void setStatus(String status);

	public Date getStart();

	public void setStart(Date start);

	public Date getEnd();

	public void setEnd(Date end);

	public String[] getSelection_ids();

	public void setSelection_ids(String[] selection_ids);

	public String getAccount_id();

	public void setAccount_id(String account_id);

	public String getParameters();

	public void setParameters(String parameters);
	
	public String getResult_file();

	public void setResult_file(String file);
        
        public DateTime getCreated_at();

	public void setCreated_at(DateTime created);
	
	public DateTime getLast_modified();

}
