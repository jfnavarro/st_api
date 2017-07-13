package com.st.model;

import java.util.List;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
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

    @NotBlank(message = "ST Data must not be blank.")
    String st_data_filename;
    
    @NotBlank(message = "Main image must not be blank.")
    String image_main;

    String image_second;
    
    String image_alignment_file;
    
    String spot_coordinates_file;
    
    String comment;

    String created_by_account_id;

    @CreatedDate
    private DateTime created_at;

    @LastModifiedDate
    private DateTime last_modified;

    List<String> granted_accounts;

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
    public String getImage_alignment_file() {
        return this.image_alignment_file;
    }

    @Override
    public void setImage_alignment_file(String file) {
        this.image_alignment_file = file;
    }
    
    @Override
    public String getSpot_coordinates_file() {
        return this.spot_coordinates_file;
    }

    @Override
    public void setSpot_coordinates_file(String file) {
        this.spot_coordinates_file = file;
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
    public List<String> getGranted_accounts() {
        // Controller must assure to synch these in the DB with DatasetInfo objects
        return granted_accounts;
    }

    @Override
    public void setGranted_accounts(List<String> grantedAccounts) {
        // Controller must assure to synch these in the DB with DatasetInfo objects
        this.granted_accounts = grantedAccounts;
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
