/*
 * Copyright (C) 2012 Spatial Transcriptomics AB
 * Read LICENSE for more information about licensing terms
 * Contact: Jose Fernandez Navarro <jose.fernandez.navarro@scilifelab.se>
 */

package com.spatialtranscriptomics.model;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;
import org.joda.time.DateTime;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * This interface defines the Selection model. Applications that use the API must implement the same model.
 */
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
	
	@NotEmpty(message = "Gene nomenclatures with stats must not be empty.")
	List<String[]> gene_hits = new ArrayList<String[]>();
	
        boolean enabled;
        
	String type;
	
	String status;
	
	String comment;
	
	String[] obo_foundry_terms;

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

         public boolean getEnabled() {
            return this.enabled;
        }
        
        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
        
	public List<String[]> getGene_hits() {
		return gene_hits;
	}

	public void setGene_hits(List<String[]> gene_hits) {
		this.gene_hits = gene_hits;
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
        
        public String getGene(int i) {
            return (String) (gene_hits.get(i)[0]);
	}
        
	public int getHit_count(int i) {
            return Integer.parseInt(gene_hits.get(i)[1]);
	}

	public double getNormalized_hit_count(int i) {
            return Double.parseDouble(gene_hits.get(i)[2]);
	}

	public double getNormalized_pixel_intensity(int i) {
            return Double.parseDouble(gene_hits.get(i)[3]);
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
