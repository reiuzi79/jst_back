package com.su.JobTracker.service;
import com.su.JobTracker.mapper.*;
import com.su.JobTracker.model.*;
import com.su.JobTracker.param.JobSearchParams;
import com.su.JobTracker.response.*;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class JobPostingService {
	@Autowired
	private jobPostingMapper jobPostingMapper;
	
	public int insertJobPosting(Date closing_date, int company_id, String description, String employment_type, String location, String requirements, String responsibilities, String salary_range, String title) {
		if(title.length() > 255 || location.length() > 255 || salary_range.length() > 100) {
			return 0;
		}
		jobPostingWithBLOBs jobPosting = new jobPostingWithBLOBs();
		jobPosting.setPostedDate(new Date());
		jobPosting.setUpdatedAt(new Date());
		jobPosting.setCreatedAt(new Date());
		jobPosting.setClosingDate(closing_date);
		jobPosting.setCompanyId(company_id);
		jobPosting.setDescription(description);
		jobPosting.setEmploymentType(employment_type);
		jobPosting.setLocation(location);
		jobPosting.setRequirements(requirements);
		jobPosting.setResponsibilities(responsibilities);
		jobPosting.setSalaryRange(salary_range);
		jobPosting.setTitle(title);
		return jobPostingMapper.insert(jobPosting);
	}
	
	public int modifyJobPosting(int posting_id, Date closing_date, String description, String employment_type, String location, String requirements, String responsibilities, String salary_range, String title ) {
		if(title.length() > 255 || location.length() > 255 || salary_range.length() > 100) {
			return 0;
		}
		jobPostingWithBLOBs jobPosting = findJobPostingById(posting_id);
		jobPosting.setUpdatedAt(new Date());
		jobPosting.setClosingDate(closing_date);
		jobPosting.setDescription(description);
		jobPosting.setEmploymentType(employment_type);
		jobPosting.setLocation(location);
		jobPosting.setRequirements(requirements);
		jobPosting.setResponsibilities(responsibilities);
		jobPosting.setSalaryRange(salary_range);
		jobPosting.setTitle(title);
		return jobPostingMapper.updateByPrimaryKeyWithBLOBs(jobPosting);
	}
	
	public int deleteJobPostingById(int posting_id) {
		return jobPostingMapper.deleteByPrimaryKey(posting_id);
	}
	
	public jobPostingWithBLOBs findJobPostingById(int posting_id) {
		return jobPostingMapper.selectByPrimaryKey(posting_id);
	}
	
	public JobSearchResponse findDetailedJobPostingById(int job_posting_id) {
		return jobPostingMapper.findJobPostingByPostingId(job_posting_id);
	}
	
	public Page<JobSearchResponse> findJobPostingByCompanyId(int company_id, int page, int size){
		int offset = (page - 1) * size;
        int limit = size;
        var example = new jobPostingExample();
		var criteria = example.createCriteria();
		criteria.andCompanyIdEqualTo(company_id);
        int totalCount = (int)jobPostingMapper.countByExample(example);
        int totalPages = (int) Math.ceil((double) totalCount / size);
        var list = jobPostingMapper.findJobPostingByCompanyId(company_id, limit, offset);
        var result = new Page<>(list, page, size, totalCount, totalPages);
		return result;
	}
	
	public Page<JobSearchResponse> findUnexpiredJobPostingByCompanyId(int company_id, Date now, int page, int size){
		int offset = (page - 1) * size;
        int limit = size;
        var example = new jobPostingExample();
		var criteria = example.createCriteria();
		criteria.andCompanyIdEqualTo(company_id);
		criteria.andClosingDateGreaterThan(now);
		int totalCount = (int)jobPostingMapper.countByExample(example);
	    int totalPages = (int) Math.ceil((double) totalCount / size);
	    var list = jobPostingMapper.findUnexpiredJobPostingByCompanyId(company_id, now, limit, offset);
	    var result = new Page<>(list, page, size, totalCount, totalPages);
		return result;
	}
	
	public Page<JobSearchResponse> findExpiredJobPostingByCompanyId(int company_id, Date now, int page, int size){
		int offset = (page - 1) * size;
        int limit = size;
        var example = new jobPostingExample();
		var criteria = example.createCriteria();
		criteria.andCompanyIdEqualTo(company_id);
		criteria.andClosingDateLessThanOrEqualTo(now);
		int totalCount = (int)jobPostingMapper.countByExample(example);
	    int totalPages = (int) Math.ceil((double) totalCount / size);
	    var list = jobPostingMapper.findExpiredJobPostingByCompanyId(company_id, now, limit, offset);
	    var result = new Page<>(list, page, size, totalCount, totalPages);
		return result;
	}
	
	public Page<JobSearchResponse> findUnexpiredJobPosting(String company_name, String title, String location, String type, String keyword, Date now, int page, int size){
		int offset = (page - 1) * size;
        int limit = size;
		var params = new JobSearchParams();
		params.setCompanyName(company_name);
        params.setTitle(title);
        params.setLocation(location);
        params.setNow(now);
        params.setKeyword(keyword);
        params.setType(type);
        
        int totalCount = (int)jobPostingMapper.countUnexpiredJobPosting(params);
	    int totalPages = (int) Math.ceil((double) totalCount / size);
	    var list = jobPostingMapper.findUnexpiredJobPosting(params, limit, offset);
	    var result = new Page<>(list, page, size, totalCount, totalPages);
        return result;
	}
}
