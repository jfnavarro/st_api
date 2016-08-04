package com.st.model;

import java.util.List;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.joda.time.DateTime;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

/**
 * This class maps the Account data model object into a MongoDB Document We use
 * the @Document annotation of Spring Data for the mapping. We also do data
 * validation using Hibernate validator constraints.
 *
 * Accounts represent the user entities, and also store the credentials used
 * for login. The username is enforced to be an email.
 */
@Document(collection = "account")
public class Account implements IAccount {

    @Id
    public String id;

    @Indexed(unique = true)
    @NotBlank
    @Email(message = "Username must be a valid email address.")
    public String username;

    @Length(min = 4, message = "Password must have at least 4 characters.")
    public String password;

    @NotBlank
    public String role;

    //@NotBlank // not possible...
    public boolean enabled;

    public String institution;

    public String first_name;

    public String last_name;

    public String street_address;

    public String city;

    public String postcode;

    public String country;

    @CreatedDate
    private DateTime created_at;

    @LastModifiedDate
    private DateTime last_modified;

    public List<String> granted_datasets;

    /**
     * Default constructor is needed by Jackson, in
     * case other constructors are added.
     */
    public Account() {}
    
    // id is set automatically by MongoDB
    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String getRole() {
        return this.role;
    }

    @Override
    public void setRole(String role) {
        this.role = role;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String getInstitution() {
        return institution;
    }

    @Override
    public void setInstitution(String institution) {
        this.institution = institution;
    }

    @Override
    public String getFirst_name() {
        return first_name;
    }

    @Override
    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    @Override
    public String getLast_name() {
        return last_name;
    }

    @Override
    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    @Override
    public String getStreet_address() {
        return street_address;
    }

    @Override
    public void setStreet_address(String street_address) {
        this.street_address = street_address;
    }

    @Override
    public String getCity() {
        return city;
    }

    @Override
    public void setCity(String city) {
        this.city = city;
    }

    @Override
    public String getPostcode() {
        return postcode;
    }

    @Override
    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    @Override
    public String getCountry() {
        return country;
    }

    @Override
    public void setCountry(String country) {
        this.country = country;
    }

    @Override
    public List<String> getGranted_datasets() {
        // Controller must assure to sinch these in the DB with DatasetInfo objects
        return granted_datasets;
    }

    @Override
    public void setGranted_datasets(List<String> grantedDatasets) {
        // Controller must assure to sinch these in the DB with DatasetInfo objects
        this.granted_datasets = grantedDatasets;
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
