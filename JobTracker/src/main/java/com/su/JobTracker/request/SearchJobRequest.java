package com.su.JobTracker.request;

import org.springframework.web.bind.annotation.RequestParam;

public class SearchJobRequest {
	private String company_name = "";
	private String title = ""; 
	private String[] location = new String[] {""};
	private String type = ""; 
	private String keyword = "";
	public String getCompany_name() {
		return company_name;
	}
	public void setCompany_name(String company_name) {
		this.company_name = company_name;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String[] getLocation() {
		return location;
	}
	public void setLocation(String[] location) {
		this.location = location;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getKeyword() {
		return keyword;
	}
	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}
}
