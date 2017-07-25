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

    public String getImageHE();

    public void setImageHE(String filename);

    public String getImageCy3();

    public void setImageCy3(String filename);
    
    public double[] getAlignmentMatrix();
    
    public void setAlignmentMatrix(double[] arr);

    public List<String> getFiles();

    public void setFiles(List<String> files);
    
    public String getTissue();

    public void setTissue(String tissue);

    public String getSpecies();

    public void setSpecies(String species);

    public String getComment();

    public void setComment(String comm);

    public boolean getEnabled();

    public void setEnabled(boolean b);

    public List<String> getGrantedAccounts();

    public void setGrantedAccounts(List<String> grantedAccounts);

    public DateTime getCreated_at();

    public void setCreated_at(DateTime created);

    public DateTime getLast_modified();

    public String getCreated_by_account_id();

    public void setCreated_by_account_id(String id);
}
