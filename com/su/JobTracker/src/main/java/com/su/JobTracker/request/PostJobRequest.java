package com.su.JobTracker.request;

import java.util.Date;

import org.springframework.web.bind.annotation.RequestParam;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PostJobRequest {
	private Date closing_date;
	private String description;
	private String employment_type;
	@JsonProperty("location")
	private String[] location;
	private String requirements;
	private String responsibilities;
	private String salary_range; 
	private String title;
	public Date getClosing_date() {
		return closing_date;
	}
	public void setClosing_date(Date closing_date) {
		this.closing_date = closing_date;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getEmployment_type() {
		return employment_type;
	}
	public void setEmployment_type(String employment_type) {
		this.employment_type = employment_type;
	}
	public String[] getLocationRequest() {
		return location;
	}
	public void setLocationRequest(String[] location) {
		this.location = location;
	}
	public String getRequirements() {
		return requirements;
	}
	public void setRequirements(String requirements) {
		this.requirements = requirements;
	}
	public String getResponsibilities() {
		return responsibilities;
	}
	public void setResponsibilities(String responsibilities) {
		this.responsibilities = responsibilities;
	}
	public String getSalary_range() {
		return salary_range;
	}
	public void setSalary_range(String salary_range) {
		this.salary_range = salary_range;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
}
