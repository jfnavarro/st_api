/*
 * Copyright (C) 2012 Spatial Transcriptomics AB
 * Read LICENSE for more information about licensing terms
 * Contact: Jose Fernandez Navarro <jose.fernandez.navarro@scilifelab.se>
 */
package com.spatialtranscriptomics.model;

import org.joda.time.DateTime;

/**
 * This interface defines the PipelineExperiment model. Applications that use
 * the API must implement the same model.
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

    public String getEmr_state();

    public void setEmr_state(String state);

    public DateTime getEmr_creation_date_time();

    public void setEmr_creation_date_time(DateTime creationDateTime);

    public DateTime getEmr_end_date_time();

    public void setEmr_end_date_time(DateTime endDateTime);

    public String getEmr_last_state_change_reason();

    public void setEmr_last_state_change_reason(String lastStateChangeReason);
    
    public String getInput_files();

    public void setInput_files(String input_files);
    
    public String getMapper_tool();

    public void setMapper_tool(String mapper_tool);

    public String getMapper_genome();

    public void setMapper_genome(String mapper_genome);

    public String getAnnotation_tool();

    public void setAnnotation_tool(String annotation_tool);

    public String getAnnotation_genome();

    public void setAnnotation_genome(String annotation_genome);
    
    public String getChip_id();
    
    public void setChip_id(String chip_id);

    public DateTime getCreated_at();

    public void setCreated_at(DateTime created);

    public DateTime getLast_modified();
}
