package com.su.JobTracker.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AddCompanyRequest {
    private String company_name;
    private String industry;
    private String website;
    @JsonProperty("location")
    private String[] location;
	public String getCompany_name() {
		return company_name;
	}
	public void setCompany_name(String company_name) {
		this.company_name = company_name;
	}
	public String getIndustry() {
		return industry;
	}
	public void setIndustry(String industry) {
		this.industry = industry;
	}
	public String getWebsite() {
		return website;
	}
	public void setWebsite(String website) {
		this.website = website;
	}
	public String[] getLocationRequest() {
		return location;
	}
	public void setLocationRequest(String[] location) {
		this.location = location;
	}
}
