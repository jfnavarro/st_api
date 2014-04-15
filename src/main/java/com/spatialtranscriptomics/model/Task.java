package com.spatialtranscriptomics.model;

import java.util.Date;

import org.hibernate.validator.constraints.NotBlank;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection="task")
public class Task implements ITask {

	@Id
	String id;
	
	@NotBlank(message = "name must not be blank.")
	String name;
	
	String status;
	
	Date start;
	
	Date end;
	
	String[] selection_ids;
	
	String account_id;
	
	String parameters;
	
	Date last_modified;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Date getStart() {
		return start;
	}

	public void setStart(Date start) {
		this.start = start;
	}

	public Date getEnd() {
		return end;
	}

	public void setEnd(Date end) {
		this.end = end;
	}

	public String[] getSelection_ids() {
		return selection_ids;
	}

	public void setSelection_ids(String[] selection_ids) {
		this.selection_ids = selection_ids;
	}

	public String getAccount_id() {
		return account_id;
	}

	public void setAccount_id(String account_id) {
		this.account_id = account_id;
	}

	public String getParameters() {
		return parameters;
	}

	public void setParameters(String parameters) {
		this.parameters = parameters;
	}

	public Date getLast_modified() {
		return last_modified;
	}

	public void setLast_modified(Date last_modified) {
		this.last_modified = last_modified;
	}
	
	
	
}
