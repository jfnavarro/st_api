package com.spatialtranscriptomics.model;

import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

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
	
	@NotEmpty(message = "Alignment matrix must not be empty.")
	double[] alignment_matrix;
	
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

}
