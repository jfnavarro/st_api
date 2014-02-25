package com.spatialtranscriptomics.model;

import java.util.Date;

public interface IDatasetInfo {

	public String getId();
	
	public void setId(String id);
	
	public String getAccount_id();
	
	public void setAccount_id(String id);
	
	public String getDataset_id();
	
	public void setDataset_id(String id);
	
	public String getComment();
	
	public void setComment(String id);
	
	public Date getLast_modified();
	
	public void setLast_modified(Date d);
	
	
}
