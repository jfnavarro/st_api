/*
 * Copyright (C) 2012 Spatial Transcriptomics AB
 * Read LICENSE for more information about licensing terms
 * Contact: Jose Fernandez Navarro <jose.fernandez.navarro@scilifelab.se>
 */

package com.spatialtranscriptomics.model;

import org.hibernate.validator.constraints.NotBlank;
import org.joda.time.DateTime;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * This interface defines the ImageAlignment model. Applications that use the API must implement the same model.
 */
@Document(collection="imagealignment")
public class ImageAlignment implements IImageAlignment {

	@Id
	String id;
	
	@Indexed(unique = true)
	@NotBlank(message = "Name must not be blank.")
	String name;
	
	@NotBlank(message = "Chip must not be blank.")
	@Indexed(unique = false)
	String chip_id;
	
	@NotBlank(message = "Figure red must not be blank.")
	String figure_red;
	
	@NotBlank(message = "Figure blue must not be blank.")
	String figure_blue;
	
	//@NotEmpty(message = "Alignment matrix must not be empty.")
	double[] alignment_matrix;
	
        @CreatedDate
	private DateTime created_at;
	
        @LastModifiedDate
        private DateTime last_modified;
        
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getChip_id() {
		return chip_id;
	}

	public void setChip_id(String id) {
		this.chip_id = id;
	}

	public String getFigure_red() {
		return figure_red;
	}

	public void setFigure_red(String fig) {
		this.figure_red = fig;
	}

	public String getFigure_blue() {
		return figure_blue;
	}

	public void setFigure_blue(String fig) {
		this.figure_blue = fig;
	}

	public double[] getAlignment_matrix() {
		return alignment_matrix;
	}

	public void setAlignment_matrix(double[] arr) {
		this.alignment_matrix = arr;
	}

        public DateTime getCreated_at() {
		return created_at;
	}
	
	public void setCreated_at(DateTime created) {
		this.created_at = created;
	}

	public DateTime getLast_modified() {
		return last_modified;
	}
}
