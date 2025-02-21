package com.su.JobTracker.service;
import com.su.JobTracker.mapper.*;
import com.su.JobTracker.model.*;
import com.su.JobTracker.response.JobApplicationResponse;
import com.su.JobTracker.response.Page;
import com.su.JobTracker.utils.StatusCode;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class JobApplicationService {
	@Autowired
	private jobApplicationMapper jobApplicationMapper;
	@Autowired
	private applicationStatusHistoryMapper applicationStatusMapper;
	
	@Transactional
	public int insertJobApplication(int user_id, String notes, int job_posting_id, String name) throws Exception{
		var example = new jobApplicationExample();
		Date now = new Date();
		Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);
        calendar.add(Calendar.MONTH, -1);
        Date newDate = calendar.getTime();
		var criteria = example.createCriteria();
		criteria.andJobPostingIdEqualTo(job_posting_id);
		criteria.andUserIdEqualTo(user_id);
		boolean found = jobApplicationMapper.selectByExample(example).stream().anyMatch(r -> {
			if(newDate.before(r.getApplicationDate())) 
			{
				return true;
			}
			return false;
		});
		if(found) {
			return StatusCode.CANNOT_REAPPLY.getCode(); //702 只今再応募不可/Cannot reapply now
		}
		var application = new jobApplication();
		application.setApplicationStatus("Applied");
		application.setApplicationDate(now);
		application.setUserId(user_id);
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss"); 
		var new_notes = new String("").concat(name + "  ").concat(formatter.format(now).concat(" 応募").concat("\r\n").concat(notes).concat("\r\n------------------------"));
		application.setNotes(new_notes);
		application.setJobPostingId(job_posting_id);
		return jobApplicationMapper.insert(application);
		/*var history = new applicationStatusHistory();
		history.setApplicationId(application.getApplicationId());
		history.setStatus(application.getApplicationStatus());
		history.setChangeDate(new Date());
		applicationStatusMapper.insert(history);*/
	}
	
	@Transactional
	public int modifyJobApplication(int application_id, String application_status, String new_note, int user_id, String name)  throws Exception{
		var application = findJobApplicationById(application_id);
		application.setApplicationStatus(application_status);
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
		String status_jp;
		switch(application_status) {
		case("Interview"):
			status_jp = "面接に進む";
			break;
		case("Offer"):
			status_jp = "内定";
			break;
		case("Rejected"):
			status_jp = "お見送り";
			break;
		case("Applicant_Rejected"):
			status_jp = "辞退";
			application_status = "Rejected";
			break;
		case("Hired"):
			status_jp = "入社決定";
			break;
		default:
			status_jp = "";
		}
		var notes = application.getNotes().concat("\r\n\r\n").concat(name + "  ").concat(formatter.format(new Date())
				.concat(" "+status_jp).concat("\r\n").concat(new_note).concat("\r\n------------------------"));
		application.setNotes(notes);
		return jobApplicationMapper.updateByPrimaryKeyWithBLOBs(application);
	}
	
	public int deleteJobApplication(int application_id) {
		return jobApplicationMapper.deleteByPrimaryKey(application_id);
	}
	
	public jobApplication findJobApplicationById(int application_id){
		return jobApplicationMapper.selectByPrimaryKey(application_id);
	}
	
	public JobApplicationResponse findDetailedJobApplicationById(int application_id){
		return jobApplicationMapper.findDetailedJobApplicationById(application_id);
	}
	
	public Page<JobApplicationResponse> findDetailedJobApplicationByUserId(int user_id, int page, int size, int status){
		int offset = (page - 1) * size;
        int limit = size;
        int totalCount = (int)jobApplicationMapper.countInProgressByUserId(user_id, status);
        int totalPages = (int) Math.ceil((double) totalCount / size);
        var list = jobApplicationMapper.findDetailedJobApplicationByUserId(user_id, limit, offset, status);
        var result = new Page<>(list, page, size, totalCount, totalPages);
		return result;
	}
	
	public Page<JobApplicationResponse> findDetailedJobApplicationByCompanyId(int company_id, int page, int size, int status){
		int offset = (page - 1) * size;
        int limit = size;
        int totalCount = jobApplicationMapper.countInProgressByCompanyId(company_id, status);
        int totalPages = (int) Math.ceil((double) totalCount / size);
        var list = jobApplicationMapper.findDetailedJobApplicationByCompanyId(company_id, limit, offset, status);
        var result = new Page<>(list, page, size, totalCount, totalPages);
		return result;
	}
	
	public Page<JobApplicationResponse> findDetailedJobApplicationByJobPostingId(int job_posting_id, int page, int size, int status){
		int offset = (page - 1) * size;
        int limit = size;
        int totalCount = jobApplicationMapper.countInProgressByJobPostingId(job_posting_id, status);
        int totalPages = (int) Math.ceil((double) totalCount / size);
        var list = jobApplicationMapper.findDetailedJobApplicationByJobPostingId(job_posting_id, limit, offset, status);
        var result = new Page<>(list, page, size, totalCount, totalPages);
		return result;
	}
}
