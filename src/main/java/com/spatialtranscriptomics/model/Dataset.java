/*
 * Copyright (C) 2012 Spatial Transcriptomics AB
 * Read LICENSE for more information about licensing terms
 * Contact: Jose Fernandez Navarro <jose.fernandez.navarro@scilifelab.se>
 */

package com.spatialtranscriptomics.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import com.spatialtranscriptomics.controller.AccountController;
import com.spatialtranscriptomics.controller.DatasetInfoController;
import com.spatialtranscriptomics.serviceImpl.AccountServiceImpl;
import com.spatialtranscriptomics.serviceImpl.DatasetInfoServiceImpl;
import java.util.Map;
import org.joda.time.DateTime;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

/**
 * This class maps the Dataset data model object into a MongoDB Document We use
 * the @Document annotation of Spring Data for the mapping. We also do data
 * validation using Hibernate validation constraints.
 *
 * A dataset represents the characteristics of of an experiment. It is tightly
 * linked to a features collection.
 */

@Document(collection = "dataset")
public class Dataset implements IDataset {

    @Id
    String id;

    @Indexed(unique = true)
    @NotBlank(message = "Name must not be blank.")
    String name;

    boolean enabled;

    @NotBlank(message = "Tissue must not be blank.")
    String tissue;

    @NotBlank(message = "Species must not be blank.")
    String species;

    @NotBlank(message = "Image alignment must not be blank.")
    String image_alignment_id;
    
    //dynamic parameters
    Map<String,String> qa_parameters;
    
    String comment;

    public String created_by_account_id;

    @CreatedDate
    private DateTime created_at;

    @LastModifiedDate
    private DateTime last_modified;

    @Transient
    public List<String> granted_accounts;

    /**
     * Default constructor is needed by Jackson, in
     * case other constructors are added.
     */
    public Dataset() {}
    
    @Override
    public String getId() {
        return id;
    }

    // id is set automatically by MongoDB
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
    public String getImage_alignment_id() {
        return this.image_alignment_id;
    }

    @Override
    public void setImage_alignment_id(String imal) {
        this.image_alignment_id = imal;
    }
    
    @Override
    public Map<String,String> getQa_parameters() {
        return this.qa_parameters;
    }
    
    @Override
    public void setQa_parameters(Map<String,String> qa_parameters) {
        this.qa_parameters = qa_parameters;
    }
    
    @Override
    public String getTissue() {
        return this.tissue;
    }

    @Override
    public void setTissue(String tissue) {
        this.tissue = tissue;
    }

    @Override
    public String getSpecies() {
        return this.species;
    }

    @Override
    public void setSpecies(String species) {
        this.species = species;
    }

    @Override
    public String getComment() {
        return this.comment;
    }

    @Override
    public void setComment(String comm) {
        this.comment = comm;
    }

    @Override
    public boolean getEnabled() {
        return this.enabled;
    }

    @Override
    public void setEnabled(boolean b) {
        this.enabled = b;
    }

    @Override
    public String getCreated_by_account_id() {
        return this.created_by_account_id;
    }

    @Override
    public void setCreated_by_account_id(String id) {
        this.created_by_account_id = id;
    }

    @Override
    @Transient
    public List<String> getGranted_accounts() {
        
        if (this.id == null) {
            return null;
        }
        
        AccountServiceImpl service = AccountController.getStaticAccountService();
        List<Account> alreadyInThere = service.findByDataset(this.id);
        if (alreadyInThere == null) {
            return null;
        }
        
        granted_accounts = new ArrayList<String>(alreadyInThere.size());
        for (Account a : alreadyInThere) {
            granted_accounts.add(a.getId());
        }
        
        return granted_accounts;
    }

    @Override
    @Transient
    public void setGranted_accounts(List<String> grantedAccounts) {
        this.granted_accounts = grantedAccounts;
        if (this.id != null && !this.id.equals("")) {
            updateGranted_accounts();
        }
    }

    // NOTE: This method is currently invoked explicitly in the controller 
    // after adding a dataset, since the autogenerated ID
    // produced when persisting is needed. At editing, it is invoked in setGranted_accounts().
    public void updateGranted_accounts() {

        DatasetInfoServiceImpl datasetinfoService = DatasetInfoController.getStaticDatasetInfoService();
        // Remove existing accounts.
        List<DatasetInfo> dsis = datasetinfoService.findByDataset(this.id);
        for (DatasetInfo dsi : dsis) {
            datasetinfoService.delete(dsi.getId());
        }
        
        if (granted_accounts == null) {
            return;
        }
        
        for (String did : granted_accounts) {
            try {
                Date date = new Date();
                datasetinfoService.add(new DatasetInfo(did, this.id, "Created " + date.toString()));
            } catch (Exception e) {
                //TODO : we should at least log this event
            }
        }
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
