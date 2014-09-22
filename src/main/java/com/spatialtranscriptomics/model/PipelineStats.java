/*
 * Copyright (C) 2012 Spatial Transcriptomics AB
 * Read LICENSE for more information about licensing terms
 * Contact: Jose Fernandez Navarro <jose.fernandez.navarro@scilifelab.se>
 */
package com.spatialtranscriptomics.model;

import org.hibernate.validator.constraints.NotBlank;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * This interface defines the PipelineStats model. Applications that use the API
 * must implement the same model.
 */
@Document(collection = "pipelinestats")
public class PipelineStats implements IPipelineStats {

    @Id
    String id;

    @Indexed(unique = true)
    @NotBlank(message = "experiment_id must not be blank.")
    String experiment_id;

    String doc_id;

    String[] input_files;

    String[] output_files;

    String parameters;

    String status;

    int no_of_reads_mapped;

    int no_of_reads_annotated;

    int no_of_reads_mapped_with_find_indexes;

    int no_of_reads_contaminated;

    int no_of_barcodes_found;

    int no_of_genes_found;

    int no_of_transcripts_found;

    int no_of_reads_found;

    String mapper_tool;

    String mapper_genome;

    String annotation_tool;

    String annotation_genome;

    String quality_plots_file;

    String log_file;

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getExperiment_id() {
        return experiment_id;
    }

    @Override
    public void setExperiment_id(String experiment_id) {
        this.experiment_id = experiment_id;
    }

    @Override
    public String getDoc_id() {
        return doc_id;
    }

    @Override
    public void setDoc_id(String doc_id) {
        this.doc_id = doc_id;
    }

    @Override
    public String[] getInput_files() {
        return input_files;
    }

    @Override
    public void setInput_files(String[] input_files) {
        this.input_files = input_files;
    }

    @Override
    public String[] getOutput_files() {
        return output_files;
    }

    @Override
    public void setOutput_files(String[] output_files) {
        this.output_files = output_files;
    }

    @Override
    public String getParameters() {
        return parameters;
    }

    @Override
    public void setParameters(String parameters) {
        this.parameters = parameters;
    }

    @Override
    public String getStatus() {
        return status;
    }

    @Override
    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public int getNo_of_reads_mapped() {
        return no_of_reads_mapped;
    }

    @Override
    public void setNo_of_reads_mapped(int no_of_reads_mapped) {
        this.no_of_reads_mapped = no_of_reads_mapped;
    }

    @Override
    public int getNo_of_reads_annotated() {
        return no_of_reads_annotated;
    }

    @Override
    public void setNo_of_reads_annotated(int no_of_reads_annotated) {
        this.no_of_reads_annotated = no_of_reads_annotated;
    }

    @Override
    public int getNo_of_reads_mapped_with_find_indexes() {
        return no_of_reads_mapped_with_find_indexes;
    }

    @Override
    public void setNo_of_reads_mapped_with_find_indexes(
            int no_of_reads_mapped_with_find_indexes) {
        this.no_of_reads_mapped_with_find_indexes = no_of_reads_mapped_with_find_indexes;
    }

    @Override
    public int getNo_of_reads_contaminated() {
        return no_of_reads_contaminated;
    }

    @Override
    public void setNo_of_reads_contaminated(int no_of_reads_contaminated) {
        this.no_of_reads_contaminated = no_of_reads_contaminated;
    }

    @Override
    public int getNo_of_barcodes_found() {
        return no_of_barcodes_found;
    }

    @Override
    public void setNo_of_barcodes_found(int no_of_barcodes_found) {
        this.no_of_barcodes_found = no_of_barcodes_found;
    }

    @Override
    public int getNo_of_genes_found() {
        return no_of_genes_found;
    }

    @Override
    public void setNo_of_genes_found(int no_of_genes_found) {
        this.no_of_genes_found = no_of_genes_found;
    }

    @Override
    public int getNo_of_transcripts_found() {
        return no_of_transcripts_found;
    }

    @Override
    public void setNo_of_transcripts_found(int no_of_transcripts_found) {
        this.no_of_transcripts_found = no_of_transcripts_found;
    }

    @Override
    public int getNo_of_reads_found() {
        return no_of_reads_found;
    }

    @Override
    public void setNo_of_reads_found(int no_of_reads_found) {
        this.no_of_reads_found = no_of_reads_found;
    }

    @Override
    public String getMapper_tool() {
        return mapper_tool;
    }

    @Override
    public void setMapper_tool(String mapper_tool) {
        this.mapper_tool = mapper_tool;
    }

    @Override
    public String getMapper_genome() {
        return mapper_genome;
    }

    @Override
    public void setMapper_genome(String mapper_genome) {
        this.mapper_genome = mapper_genome;
    }

    @Override
    public String getAnnotation_tool() {
        return annotation_tool;
    }

    @Override
    public void setAnnotation_tool(String annotation_tool) {
        this.annotation_tool = annotation_tool;
    }

    @Override
    public String getAnnotation_genome() {
        return annotation_genome;
    }

    @Override
    public void setAnnotation_genome(String annotation_genome) {
        this.annotation_genome = annotation_genome;
    }

    @Override
    public String getQuality_plots_file() {
        return quality_plots_file;
    }

    @Override
    public void setQuality_plots_file(String quality_plots_file) {
        this.quality_plots_file = quality_plots_file;
    }

    @Override
    public String getLog_file() {
        return log_file;
    }

    @Override
    public void setLog_file(String log_file) {
        this.log_file = log_file;
    }

}
