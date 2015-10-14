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
 * This interface defines the Selection model. Applications that use the API
 * must implement the same model.
 * 
 * A selection represents a subset of features, stemming from a user-selected
 * region in the client.
 */
@Document(collection = "selection")
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

    //right now we store genes
    //as array[(gene,reads,norm_reads,pixel_intensity),...]
    @NotEmpty(message = "Gene nomenclatures with stats must not be empty.")
    List<String[]> gene_hits = new ArrayList<String[]>();

    @NotEmpty(message = "Features uniques Ids must not be empty.")
    String[] features_ids;
        
    boolean enabled;
    String type;
    String status;
    String comment;

    @CreatedDate
    private DateTime created_at;

    @LastModifiedDate
    private DateTime last_modified;

    private Color color;

     /**
     * Default constructor is needed by Jackson, in
     * case other constructors are added.
     */
    public Selection() {}
    
    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public boolean getEnabled() {
        return this.enabled;
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public List<String[]> getGene_hits() {
        return gene_hits;
    }

    @Override
    public void setGene_hits(List<String[]> gene_hits) {
        this.gene_hits = gene_hits;
    }

    @Override
    public String[] getFeature_ids() {
        return features_ids;
    }

    @Override
    public void setFeature_ids(String[] features_ids) {
        this.features_ids = features_ids;
    }
    
    @Override
    public String getDataset_id() {
        return dataset_id;
    }

    @Override
    public void setDataset_id(String dataset_id) {
        this.dataset_id = dataset_id;
    }

    @Override
    public String getAccount_id() {
        return account_id;
    }

    @Override
    public void setAccount_id(String account_id) {
        this.account_id = account_id;
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
    public String getType() {
        return type;
    }

    @Override
    public void setType(String type) {
        this.type = type;
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
    public String getComment() {
        return comment;
    }

    @Override
    public void setComment(String comment) {
        this.comment = comment;
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

    @Override
    public Color getColor() { return color; }

    @Override
    public void setColor(Color color) { this.color = color; }
}
