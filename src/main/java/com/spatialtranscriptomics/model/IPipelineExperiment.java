/*
 * Copyright (C) 2012 Spatial Transcriptomics AB
 * Read LICENSE for more information about licensing terms
 * Contact: Jose Fernandez Navarro <jose.fernandez.navarro@scilifelab.se>
 */

package com.spatialtranscriptomics.model;

import java.util.Date;

/**
 * This interface defines the PipelineExperiment model. Applications that use the API must implement the same model.
 */

public interface IPipelineExperiment {

	public String getId();

	public void setId(String id);

	public String getEmr_jobflow_id();

	public void setEmr_jobflow_id(String emrJobflowId);

	public String getName();

	public void setName(String name);
	
	public String getAccount_id();

	public void setAccount_id(String id);

	public Date getLast_modified();

	public void setLast_modified(Date date);
}
