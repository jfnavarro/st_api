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
 * This interface defines the ImageAlignment model. Applications that use the
 * API must implement the same model.
 * 
 * An image alignment holds the transform between the coordinate space of the
 * image of the tissue of an experiment, and the coordinates of the chip
 * (and the obtained features).
 */
@Document(collection = "imagealignment")
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

    double[] alignment_matrix;

    @CreatedDate
    private DateTime created_at;

    @LastModifiedDate
    private DateTime last_modified;

    /**
     * Default constructor is needed by Jackson, in
     * case other constructors are added.
     */
    public ImageAlignment() {}
    
    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getChip_id() {
        return chip_id;
    }

    @Override
    public void setChip_id(String id) {
        this.chip_id = id;
    }

    @Override
    public String getFigure_red() {
        return figure_red;
    }

    @Override
    public void setFigure_red(String fig) {
        this.figure_red = fig;
    }

    @Override
    public String getFigure_blue() {
        return figure_blue;
    }

    @Override
    public void setFigure_blue(String fig) {
        this.figure_blue = fig;
    }

    @Override
    public double[] getAlignment_matrix() {
        return alignment_matrix;
    }

    @Override
    public void setAlignment_matrix(double[] arr) {
        this.alignment_matrix = arr;
    }

    @Override
    public DateTime getCreated_at() {
        return created_at;
    }

    @Override
    public void setCreated_at(DateTime created) {
        this.created_at = created;
    }

    @Override
    public DateTime getLast_modified() {
        return last_modified;
    }
}
