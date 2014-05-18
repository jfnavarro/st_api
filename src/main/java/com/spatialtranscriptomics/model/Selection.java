package com.spatialtranscriptomics.model;

import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection="selection")
public class Selection implements ISelection {

	@Id
	String id;
	
	@Indexed(unique = true)
	@NotBlank(message = "Name must not be blank.")
	String name;
	
	@Indexed(unique = false)
	@NotBlank(message = "Dataset must not be blank.")
	String dataset_id;
	
	@Indexed(unique = false)
	@NotBlank(message = "Account must not be blank.")
	String account_id;
	
	@NotEmpty(message = "Features must not be empty.")
	String[] feature_ids;
	
	String type;
	
	String status;
	
	String comment;
	
	String[] obo_foundry_terms;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String[] getFeature_ids() {
		return feature_ids;
	}

	public void setFeature_ids(String[] feature_ids) {
		this.feature_ids = feature_ids;
	}

	public String getDataset_id() {
		return dataset_id;
	}

	public void setDataset_id(String dataset_id) {
		this.dataset_id = dataset_id;
	}

	public String getAccount_id() {
		return account_id;
	}

	public void setAccount_id(String account_id) {
		this.account_id = account_id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}
	
	public String[] getObo_foundry_terms() {
		return obo_foundry_terms;
	}

	public void setObo_foundry_terms(String[] obo_foundry_terms) {
		this.obo_foundry_terms = obo_foundry_terms;
	}
	
}
