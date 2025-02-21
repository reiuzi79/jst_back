package com.su.JobTracker.response;

import java.util.Date;

public class JobSearchResponse {
	private int job_posting_id;
	private String title;
	private String description;
	private String location;
	private String employment_type;
	private String salary_range;
	private String requirements;
	private String responsibilities;
	private Date posted_date;
	private Date closing_date;
	private String company_name;
	private String company_location;
	private String industry;
	private int isSaved = 0;
	public int getJob_posting_id() {
		return job_posting_id;
	}
	public void setJob_posting_id(int job_posting_id) {
		this.job_posting_id = job_posting_id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public String getEmployment_type() {
		return employment_type;
	}
	public void setEmployment_type(String employment_type) {
		this.employment_type = employment_type;
	}
	public String getSalary_range() {
		return salary_range;
	}
	public void setSalary_range(String salary_range) {
		this.salary_range = salary_range;
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
	public Date getPosted_date() {
		return posted_date;
	}
	public void setPosted_date(Date posted_date) {
		this.posted_date = posted_date;
	}
	public Date getClosing_date() {
		return closing_date;
	}
	public void setClosing_date(Date closing_date) {
		this.closing_date = closing_date;
	}
	public String getCompany_name() {
		return company_name;
	}
	public void setCompany_name(String company_name) {
		this.company_name = company_name;
	}
	public String getCompany_location() {
		return company_location;
	}
	public void setCompany_location(String company_location) {
		this.company_location = company_location;
	}
	public String getIndustry() {
		return industry;
	}
	public void setIndustry(String industry) {
		this.industry = industry;
	}
	public int getIsSaved() {
		return isSaved;
	}
	public void setIsSaved(int isSaved) {
		this.isSaved = isSaved;
	}
}
