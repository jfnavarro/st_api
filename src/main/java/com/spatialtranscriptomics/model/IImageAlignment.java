/**
 * This class maps the Dataset data model object into a MongoDB Document We use
 * the @Document annotation of Spring Data for the mapping. We also do data
 * validation using Hibernate validator constraints.
 *
 */

package com.spatialtranscriptomics.model;

import org.joda.time.DateTime;

/**
 * This interface defines the ImageAlignment model. Applications that use the
 * API must implement the same model.
 */

public interface IImageAlignment {

    public String getId();

    public void setId(String id);

    public String getName();

    public void setName(String name);

    public String getChip_id();

    public void setChip_id(String id);

    public String getFigure_red();

    public void setFigure_red(String fig);

    public String getFigure_blue();

    public void setFigure_blue(String fig);

    public double[] getAlignment_matrix();

    public void setAlignment_matrix(double[] arr);

    public DateTime getCreated_at();

    public void setCreated_at(DateTime created);

    public DateTime getLast_modified();
}
