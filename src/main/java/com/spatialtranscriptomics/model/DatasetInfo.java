/*
 * Copyright (C) 2012 Spatial Transcriptomics AB
 * Read LICENSE for more information about licensing terms
 * Contact: Jose Fernandez Navarro <jose.fernandez.navarro@scilifelab.se>
 */

package com.spatialtranscriptomics.model;

import org.hibernate.validator.constraints.NotBlank;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * This class maps the DatasetInfo data model object into a MongoDB Document
 * We use the @Document annotation of Spring Data for the mapping.
 * We also do data validation using Hibernate validator constraints.
 * 
 */
@Document(collection="datasetinfo")
@CompoundIndexes({
    @CompoundIndex(name = "account_id_1_dataset_id_1", def = "{'account_id': 1, 'dataset_id': 1}")
})
public class DatasetInfo implements IDatasetInfo {

	@Id
	String id;
	
	@Indexed(unique = false)
	@NotBlank(message = "account_id must not be blank.")
	String account_id;
	
	@Indexed(unique = false)
	@NotBlank(message = "dataset_id must not be blank.")
	String dataset_id;
	
	String comment;
	
	public DatasetInfo() {}
	
	// HACK. Only for admin editing.
	public DatasetInfo(String accountId, String datasetId, String comment) {
		this.account_id = accountId;
		this.dataset_id = datasetId;
		this.comment = comment;
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getAccount_id() {
		return account_id;
	}

	public void setAccount_id(String account_id) {
		this.account_id = account_id;
	}

	public String getDataset_id() {
		return dataset_id;
	}

	public void setDataset_id(String dataset_id) {
		this.dataset_id = dataset_id;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}
}
