package com.su.JobTracker.request;

import org.springframework.web.bind.annotation.RequestParam;

public class MessageRequest {
	private Integer application_id;
	private String message;
	public Integer getApplication_id() {
		return application_id;
	}
	public void setApplication_id(Integer application_id) {
		this.application_id = application_id;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
}
