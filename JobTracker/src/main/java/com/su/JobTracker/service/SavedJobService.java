package com.su.JobTracker.service;
import com.su.JobTracker.mapper.*;
import com.su.JobTracker.model.savedJob;
import com.su.JobTracker.model.savedJobExample;
import com.su.JobTracker.response.*;
import com.su.JobTracker.utils.StatusCode;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SavedJobService {
	@Autowired
	private savedJobMapper savedJobMapper;
	
	public int insertSavedJob(int user_id, int job_posting_id) {
		var example = new savedJobExample();
		var cri = example.createCriteria();
		cri.andJobPostingIdEqualTo(job_posting_id);
		cri.andUserIdEqualTo(user_id);
		var result = savedJobMapper.selectByExample(example);
		if(!result.isEmpty()) {
			return StatusCode.ALREADY_SAVED.getCode(); //703 既に気に入っている/Already saved
		}
		savedJob job = new savedJob();
		job.setSavedDate(new Date());
		job.setUserId(user_id);
		job.setJobPostingId(job_posting_id);
		return savedJobMapper.insert(job);
	}
	
	public int deleteSavedJob(int saved_job_id) {
		return savedJobMapper.deleteByPrimaryKey(saved_job_id);
	}
	
	public List<SavedJobResponse> findUnexpiredSavedJobByUserId(int user_id, Date now){
		return savedJobMapper.findUnexpiredSavedJobByUserId(user_id, now);
	}
}
