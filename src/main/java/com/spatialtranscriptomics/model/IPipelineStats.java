/*
 * Copyright (C) 2012 Spatial Transcriptomics AB
 * Read LICENSE for more information about licensing terms
 * Contact: Jose Fernandez Navarro <jose.fernandez.navarro@scilifelab.se>
 */
package com.spatialtranscriptomics.model;

/**
 * This interface defines the PipelineStats model. Applications that use the API
 * must implement the same model.
 */
public interface IPipelineStats {

    public String getId();

    public void setId(String id);

    public String getExperiment_id();

    public void setExperiment_id(String experiment_id);

    public String getDoc_id();

    public void setDoc_id(String doc_id);

    public String[] getInput_files();

    public void setInput_files(String[] input_files);

    public String[] getOutput_files();

    public void setOutput_files(String[] output_files);

    public String getParameters();

    public void setParameters(String parameters);

    public String getStatus();

    public void setStatus(String status);

    public int getNo_of_reads_mapped();

    public void setNo_of_reads_mapped(int no_of_reads_mapped);

    public int getNo_of_reads_annotated();

    public void setNo_of_reads_annotated(int no_of_reads_annotated);

    public int getNo_of_reads_mapped_with_find_indexes();

    public void setNo_of_reads_mapped_with_find_indexes(
            int no_of_reads_mapped_with_find_indexes);

    public int getNo_of_reads_contaminated();

    public void setNo_of_reads_contaminated(int no_of_reads_contaminated);

    public int getNo_of_barcodes_found();

    public void setNo_of_barcodes_found(int no_of_barcodes_found);

    public int getNo_of_genes_found();

    public void setNo_of_genes_found(int no_of_genes_found);

    public int getNo_of_transcripts_found();

    public void setNo_of_transcripts_found(int no_of_transcripts_found);

    public int getNo_of_reads_found();

    public void setNo_of_reads_found(int no_of_reads_found);

    public String getMapper_tool();

    public void setMapper_tool(String mapper_tool);

    public String getMapper_genome();

    public void setMapper_genome(String mapper_genome);

    public String getAnnotation_tool();

    public void setAnnotation_tool(String annotation_tool);

    public String getAnnotation_genome();

    public void setAnnotation_genome(String annotation_genome);

    public String getQuality_plots_file();

    public void setQuality_plots_file(String quality_plots_file);

    public String getLog_file();

    public void setLog_file(String log_file);

}
