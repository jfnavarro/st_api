/*
 * Copyright (C) 2012 Spatial Transcriptomics AB
 * Read LICENSE for more information about licensing terms
 * Contact: Jose Fernandez Navarro <jose.fernandez.navarro@scilifelab.se>
 */
package com.spatialtranscriptomics.model;

import java.util.List;
import java.util.Map;
import org.joda.time.DateTime;

/**
 * This interface defines the Dataset model. Applications that use the API must
 * implement the same model.
 */

public interface IDataset {

    public String getId();

    public void setId(String id);

    public String getName();

    public void setName(String name);

    public String getImage_alignment_id();

    public void setImage_alignment_id(String imal);
    
    public Map<String,String> getQa_parameters();
    
    public void setQa_parameters(Map<String,String> qa_parameters);

    public String getTissue();

    public void setTissue(String tissue);

    public String getSpecies();

    public void setSpecies(String species);

    public String getComment();

    public void setComment(String comm);

    public boolean getEnabled();

    public void setEnabled(boolean b);

    public List<String> getGranted_accounts();

    public void setGranted_accounts(List<String> grantedAccounts);

    public DateTime getCreated_at();

    public void setCreated_at(DateTime created);

    public DateTime getLast_modified();

    public String getCreated_by_account_id();

    public void setCreated_by_account_id(String id);
}
