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

    double[] alignmentMatrix;
    
    @NotBlank(message = "HE image must not be blank.")
    String imageHE;

    String imageCy3;
        
    @NotBlank(message = "Data file must not be blank.")
    String dataFile;
    
    List<String> files;
    
    String comment;

    @NotBlank(message = "Created by must not be blank.")
    String created_by_account_id;

    @CreatedDate
    private DateTime created_at;

    @LastModifiedDate
    private DateTime last_modified;

    @NotBlank(message = "Granted account must contain at least one.")
    List<String> grantedAccounts;

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
    public String getImageHE() {
        return this.imageHE;
    }

    @Override
    public void setImageHE(String filename) {
        this.imageHE = filename;
    }

    @Override
    public String getImageCy3() {
        return this.imageCy3;
    }

    @Override
    public void setImageCy3(String filename) {
        this.imageCy3 = filename;
    }
    
    @Override
    public double[] getAlignmentMatrix() {
        return this.alignmentMatrix;
    }
    
    @Override
    public void setAlignmentMatrix(double[] arr) {
        this.alignmentMatrix = arr;
    }
    
    @Override
    public String getDataFile() {
        return this.dataFile;
    }

    @Override
    public void setDataFile(String file) {
        this.dataFile = file;
    }
    
    @Override
    public List<String> getFiles() {
        return this.files;
    }

    @Override
    public void setFiles(List<String> files) {
        this.files = files;
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
    public List<String> getGrantedAccounts() {
        // Controller must assure to synch these in the DB with DatasetInfo objects
        return grantedAccounts;
    }

    @Override
    public void setGrantedAccounts(List<String> grantedAccounts) {
        // Controller must assure to synch these in the DB with DatasetInfo objects
        this.grantedAccounts = grantedAccounts;
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
