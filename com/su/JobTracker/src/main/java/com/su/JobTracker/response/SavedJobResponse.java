package com.su.JobTracker.response;

import java.util.Date;

public class SavedJobResponse {
	private int saved_job_id;
	private int user_id;
	private int job_posting_id;
	private String company_name;
	private String job_title;
	private Date saved_date;
	private Date closing_date;
	public Date getSaved_date() {
		return saved_date;
	}
	public void setSaved_date(Date saved_date) {
		this.saved_date = saved_date;
	}
	public Date getClosing_date() {
		return closing_date;
	}
	public void setClosing_date(Date closing_date) {
		this.closing_date = closing_date;
	}
	public int getSaved_job_id() {
		return saved_job_id;
	}
	public void setSaved_job_id(int saved_job_id) {
		this.saved_job_id = saved_job_id;
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
	public String getCompany_name() {
		return company_name;
	}
	public void setCompany_name(String company_name) {
		this.company_name = company_name;
	}
	public String getJob_title() {
		return job_title;
	}
	public void setJob_title(String job_title) {
		this.job_title = job_title;
	}
}
