package com.spatialtranscriptomics.model;

import java.util.Date;

public interface ISelection {

	public String getId();

	public void setId(String id);

	public String[] getFeature_ids();

	public void setFeature_ids(String[] feature_ids);

	public String getDataset_id();

	public void setDataset_id(String dataset_id);

	public String getAccount_id();

	public void setAccount_id(String account_id);

	public String getName();

	public void setName(String name);

	public String getType();

	public void setType(String type);

	public String getStatus();

	public void setStatus(String status);

	public String getComment();

	public void setComment(String comment);

	public Date getLast_modified();

	public void setLast_modified(Date last_modified);
}
