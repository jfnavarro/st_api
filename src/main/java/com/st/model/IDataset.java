package com.st.model;

import java.util.List;
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

    public String getImage_alignment_file();

    public void setImage_alignment_file(String file);

    public String getSpot_coordinates_file();

    public void setSpot_coordinates_file(String file);
    
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
