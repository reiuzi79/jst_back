package com.su.JobTracker.response;

import java.util.List;

public class Page<T> {
	private List<T> content;
	private int page;
	private int size;
	private int totalCount;
	private int totalPages;
	public Page(List<T> content, int page, int size, int totalCount, int totalPages) {
		super();
		this.content = content;
		this.page = page;
		this.size = size;
		this.totalCount = totalCount;
		this.totalPages = totalPages;
	}
	public List<T> getContent() {
		return content;
	}
	public void setContent(List<T> content) {
		this.content = content;
	}
	public int getPage() {
		return page;
	}
	public void setPage(int page) {
		this.page = page;
	}
	public int getSize() {
		return size;
	}
	public void setSize(int size) {
		this.size = size;
	}
	public int getTotalCount() {
		return totalCount;
	}
	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}
	public int getTotalPages() {
		return totalPages;
	}
	public void setTotalPages(int totalPages) {
		this.totalPages = totalPages;
	}
	
}
