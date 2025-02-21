package com.su.JobTracker.response;

import java.util.Date;

public class JobApplicationResponse {
	private int application_id;
	private int user_id;
	private int job_posting_id;
	private String application_status;
	private Date application_date;
	private String notes;
	private String user_email;
	private String username;
	private String job_title;
	private int company_id;
	private String company_name;
	public int getCompany_id() {
		return company_id;
	}
	public void setCompany_id(int company_id) {
		this.company_id = company_id;
	}
	public int getApplication_id() {
		return application_id;
	}
	public void setApplication_id(int application_id) {
		this.application_id = application_id;
	}
	public int getUser_id() {
		return user_id;
	}
	public void setUser_id(int user_id) {
		this.user_id = user_id;
	}
	public int getJob_posting_id() {
		return job_posting_id;
	}
	public void setJob_posting_id(int job_posting_id) {
		this.job_posting_id = job_posting_id;
	}
	public String getApplication_status() {
		return application_status;
	}
	public void setApplication_status(String application_status) {
		this.application_status = application_status;
	}
	public Date getApplication_date() {
		return application_date;
	}
	public void setApplication_date(Date application_date) {
		this.application_date = application_date;
	}
	public String getNotes() {
		return notes;
	}
	public void setNotes(String notes) {
		this.notes = notes;
	}
	public String getUser_email() {
		return user_email;
	}
	public void setUser_email(String user_email) {
		this.user_email = user_email;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getJob_title() {
		return job_title;
	}
	public void setJob_title(String job_title) {
		this.job_title = job_title;
	}
	public String getCompany_name() {
		return company_name;
	}
	public void setCompany_name(String company_name) {
		this.company_name = company_name;
	}

}
