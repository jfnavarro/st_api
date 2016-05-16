package com.st.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.validator.constraints.NotBlank;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.st.controller.AccountController;
import com.st.controller.DatasetInfoController;
import com.st.serviceImpl.AccountServiceImpl;
import com.st.serviceImpl.DatasetInfoServiceImpl;
import org.joda.time.DateTime;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

/**
 * This class maps the Dataset data model object into a MongoDB Document We use
 * the @Document annotation of Spring Data for the mapping. We also do data
 * validation using Hibernate validator constraints.
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

    int overall_feature_count;
    int overall_hit_count;
    int unique_gene_count;
    int unique_barcode_count;
    double[] overall_hit_quartiles;
    double[] gene_pooled_hit_quartiles;
    
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
    public int getOverall_feature_count() {
        return overall_feature_count;
    }

    @Override
    public void setOverall_feature_count(int count) {
        this.overall_feature_count = count;
    }

    @Override
    public int getUnique_gene_count() {
        return this.unique_gene_count;
    }

    @Override
    public void setUnique_gene_count(int count) {
        this.unique_gene_count = count;
    }

    @Override
    public int getUnique_barcode_count() {
        return this.unique_barcode_count;
    }

    @Override
    public void setUnique_barcode_count(int count) {
        this.unique_barcode_count = count;
    }

    @Override
    public int getOverall_hit_count() {
        return this.overall_hit_count;
    }

    @Override
    public void setOverall_hit_count(int count) {
        this.overall_hit_count = count;
    }

    @Override
    public double[] getOverall_hit_quartiles() {
        return this.overall_hit_quartiles;
    }

    @Override
    public void setOverall_hit_quartiles(double[] quartiles) {
        this.overall_hit_quartiles = quartiles;
    }

    @Override
    public double[] getGene_pooled_hit_quartiles() {
        return this.gene_pooled_hit_quartiles;
    }

    @Override
    public void setGene_pooled_hit_quartiles(double[] quartiles) {
        this.gene_pooled_hit_quartiles = quartiles;
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
        granted_accounts = new ArrayList<>(alreadyInThere.size());
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

    // NOTE: This method is currently invoked explicitly in the controller AFTER adding a dataset, since the autogenerated ID
    // produced when persisting is needed. At editing, it is invoked in setGranted_accounts().
    public void updateGranted_accounts() {
        //System.out.println("Updating granted for Id: " + this.id);
        DatasetInfoServiceImpl datasetinfoService = DatasetInfoController.getStaticDatasetInfoService();
        // Remove existing accounts.
        List<DatasetInfo> dsis = datasetinfoService.findByDataset(this.id);
        for (DatasetInfo dsi : dsis) {
            //System.out.println("Deleting granted: " + dsi.getId());
            datasetinfoService.delete(dsi.getId());
        }
        if (granted_accounts == null) {
            return;
        }
        for (String did : granted_accounts) {
            try {
                Date d = new Date();
                datasetinfoService.add(new DatasetInfo(did, this.id, "Created " + d.toString()));
            } catch (Exception e) {
            }
        }
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
