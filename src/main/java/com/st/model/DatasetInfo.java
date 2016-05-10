package com.st.model;

import org.hibernate.validator.constraints.NotBlank;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * This class maps the DatasetInfo data model object into a MongoDB Document We
 * use the @Document annotation of Spring Data for the mapping. We also do data
 * validation using Hibernate validator constraints.
 *
 * A dataset info is a join-table-like entity allowing an account to access
 * a dataset.
 */
@Document(collection = "datasetinfo")
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

    /**
     * Default constructor is needed by Jackson, in
     * case other constructors are added.
     */
    public DatasetInfo() {}

    // HACK. Only for admin editing.
    public DatasetInfo(String accountId, String datasetId, String comment) {
        this.account_id = accountId;
        this.dataset_id = datasetId;
        this.comment = comment;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
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
    public String getDataset_id() {
        return dataset_id;
    }

    @Override
    public void setDataset_id(String dataset_id) {
        this.dataset_id = dataset_id;
    }

    @Override
    public String getComment() {
        return comment;
    }

    @Override
    public void setComment(String comment) {
        this.comment = comment;
    }
}
