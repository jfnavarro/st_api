/*
 * Copyright (C) 2012 Spatial Transcriptomics AB
 * Read LICENSE for more information about licensing terms
 * Contact: Jose Fernandez Navarro <jose.fernandez.navarro@scilifelab.se>
 */

package com.spatialtranscriptomics.model;

import java.util.Date;

/**
 * This interface defines the Experiment model. Applications that use the API must implement the same model.
 */

public interface IExperiment {

	public String getId();

	public void setId(String id);

	public String getEmr_Jobflow_id();

	public void setEmr_Jobflow_id(String emrJobflowId);

	public String getName();

	public void setName(String name);

	public Date getCreated();

	public void setCreated(Date date);
}
