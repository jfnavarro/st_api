package com.spatialtranscriptomics.model;

import java.util.Date;

import org.hibernate.validator.constraints.NotBlank;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class PipelineStats implements IPipelineStats {

	@Id
	String id;
	
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
	
	Date last_modified;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getExperiment_id() {
		return experiment_id;
	}

	public void setExperiment_id(String experiment_id) {
		this.experiment_id = experiment_id;
	}

	public String getDoc_id() {
		return doc_id;
	}

	public void setDoc_id(String doc_id) {
		this.doc_id = doc_id;
	}

	public String[] getInput_files() {
		return input_files;
	}

	public void setInput_files(String[] input_files) {
		this.input_files = input_files;
	}

	public String[] getOutput_files() {
		return output_files;
	}

	public void setOutput_files(String[] output_files) {
		this.output_files = output_files;
	}

	public String getParameters() {
		return parameters;
	}

	public void setParameters(String parameters) {
		this.parameters = parameters;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public int getNo_of_reads_mapped() {
		return no_of_reads_mapped;
	}

	public void setNo_of_reads_mapped(int no_of_reads_mapped) {
		this.no_of_reads_mapped = no_of_reads_mapped;
	}

	public int getNo_of_reads_annotated() {
		return no_of_reads_annotated;
	}

	public void setNo_of_reads_annotated(int no_of_reads_annotated) {
		this.no_of_reads_annotated = no_of_reads_annotated;
	}

	public int getNo_of_reads_mapped_with_find_indexes() {
		return no_of_reads_mapped_with_find_indexes;
	}

	public void setNo_of_reads_mapped_with_find_indexes(
			int no_of_reads_mapped_with_find_indexes) {
		this.no_of_reads_mapped_with_find_indexes = no_of_reads_mapped_with_find_indexes;
	}

	public int getNo_of_reads_contaminated() {
		return no_of_reads_contaminated;
	}

	public void setNo_of_reads_contaminated(int no_of_reads_contaminated) {
		this.no_of_reads_contaminated = no_of_reads_contaminated;
	}

	public int getNo_of_barcodes_found() {
		return no_of_barcodes_found;
	}

	public void setNo_of_barcodes_found(int no_of_barcodes_found) {
		this.no_of_barcodes_found = no_of_barcodes_found;
	}

	public int getNo_of_genes_found() {
		return no_of_genes_found;
	}

	public void setNo_of_genes_found(int no_of_genes_found) {
		this.no_of_genes_found = no_of_genes_found;
	}

	public int getNo_of_transcripts_found() {
		return no_of_transcripts_found;
	}

	public void setNo_of_transcripts_found(int no_of_transcripts_found) {
		this.no_of_transcripts_found = no_of_transcripts_found;
	}

	public int getNo_of_reads_found() {
		return no_of_reads_found;
	}

	public void setNo_of_reads_found(int no_of_reads_found) {
		this.no_of_reads_found = no_of_reads_found;
	}

	public String getMapper_tool() {
		return mapper_tool;
	}

	public void setMapper_tool(String mapper_tool) {
		this.mapper_tool = mapper_tool;
	}

	public String getMapper_genome() {
		return mapper_genome;
	}

	public void setMapper_genome(String mapper_genome) {
		this.mapper_genome = mapper_genome;
	}

	public String getAnnotation_tool() {
		return annotation_tool;
	}

	public void setAnnotation_tool(String annotation_tool) {
		this.annotation_tool = annotation_tool;
	}

	public String getAnnotation_genome() {
		return annotation_genome;
	}

	public void setAnnotation_genome(String annotation_genome) {
		this.annotation_genome = annotation_genome;
	}

	public String getQuality_plots_file() {
		return quality_plots_file;
	}

	public void setQuality_plots_file(String quality_plots_file) {
		this.quality_plots_file = quality_plots_file;
	}

	public String getLog_file() {
		return log_file;
	}

	public void setLog_file(String log_file) {
		this.log_file = log_file;
	}

	public Date getLast_modified() {
		return last_modified;
	}

	public void setLast_modified(Date last_modified) {
		this.last_modified = last_modified;
	}
	
}
