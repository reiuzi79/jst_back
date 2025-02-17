package com.su.JobTracker.request;

import org.springframework.web.bind.annotation.RequestParam;

public class ApplicationStatusRequest {
	private Integer application_id;
	private Integer status;
	private String new_note = "";
	public Integer getApplication_id() {
		return application_id;
	}
	public void setApplication_id(Integer application_id) {
		this.application_id = application_id;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public String getNew_note() {
		return new_note;
	}
	public void setNew_note(String new_note) {
		this.new_note = new_note;
	}
}
