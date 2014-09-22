/**
 * This class maps the Dataset data model object into a MongoDB Document We use
 * the @Document annotation of Spring Data for the mapping. We also do data
 * validation using Hibernate validator constraints.
 *
 */
package com.spatialtranscriptomics.model;

/**
 * This interface defines the DatasetInfo model. Applications that use the API
 * must implement the same model.
 */
public interface IDatasetInfo {

    public String getId();

    public void setId(String id);

    public String getAccount_id();

    public void setAccount_id(String id);

    public String getDataset_id();

    public void setDataset_id(String id);

    public String getComment();

    public void setComment(String id);

}
