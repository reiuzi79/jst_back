package com.su.JobTracker.controller;

import java.util.Date;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.su.JobTracker.service.*;
import com.su.JobTracker.model.jobApplication;
import com.su.JobTracker.model.message;
import com.su.JobTracker.request.AddCompanyRequest;
import com.su.JobTracker.request.ApplicationStatusRequest;
import com.su.JobTracker.request.LocationRequest;
import com.su.JobTracker.request.MessageRequest;
import com.su.JobTracker.request.PostJobRequest;
import com.su.JobTracker.response.*;
import com.su.JobTracker.utils.*;
import java.text.SimpleDateFormat;
@RestController
@RequestMapping("/api/company")
@PreAuthorize("hasRole('COMPANY_REPRESENTATIVE')")
public class CompanyController {
	private final CompanyService companyService;
	private final JobPostingService postingService;
	private final JobApplicationService applicationService;
	private final MessageService messageService;
	private final ReviewService reviewService;
	private final UserService userService;
	@Autowired
	public CompanyController(UserService userService, CompanyService companyService, JobPostingService postingService, JobApplicationService applicationService, MessageService messageService, ReviewService reviewService){
		this.companyService = companyService;
		this.applicationService = applicationService;
		this.postingService = postingService;
		this.messageService = messageService;
		this.reviewService = reviewService;
		this.userService = userService;
	}
	
	@PostMapping("/addCompany")
	public ResponseEntity<?> addCompany(@RequestBody AddCompanyRequest addCompanyRequest){
		try {
			int user_id = userService.getCurrentUser().getUserId();
			var company_name = addCompanyRequest.getCompany_name();
			var location = addCompanyRequest.getLocationRequest();
			var industry = addCompanyRequest.getIndustry();
			var website = addCompanyRequest.getWebsite();
			int count = companyService.insertCompany(company_name, location, industry, website, user_id);
			if(count == 201) {
				return ResponseEntity.ok(null);
			}
			else {
				return ResponseEntity.status(StatusCode.TOO_LONG.getCode()).body(null); //555 文字数制限を超えた／Input is too long
			}
		}
		catch(Exception e) {
			e.printStackTrace();
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
	
	@PutMapping("/modifyCompany")
	public ResponseEntity<?> modifyCompany(@RequestBody AddCompanyRequest addCompanyRequest){
		try {
			int user_id = userService.getCurrentUser().getUserId();
			int company_id = companyService.findCompanyByUserId(user_id).getCompanyId();
			var company_name = addCompanyRequest.getCompany_name();
			var location = addCompanyRequest.getLocationRequest();
			var industry = addCompanyRequest.getIndustry();
			var website = addCompanyRequest.getWebsite();
			int count = companyService.modifyCompany(company_id, company_name, location, industry, website);
			if(count == 1) {
				return ResponseEntity.ok(null);
			}
			else {
				return ResponseEntity.status(StatusCode.TOO_LONG.getCode()).body(null); //555 文字数制限を超えた／Input is too long
			}
		}
		catch(Exception e) {
			e.printStackTrace();
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
	
	@GetMapping("/getCompany")
	public ResponseEntity<?> getMyCompany(){
		try {
			System.out.println("getCompany");
			int user_id = userService.getCurrentUser().getUserId();
			var company = companyService.findCompanyByUserId(user_id);
			if (company != null) {
				return ResponseEntity.ok(company);
			}else {
				return ResponseEntity.status(StatusCode.NOT_FOUND.getCode()).body(null);
			}
		}
		catch(Exception e) {
			e.printStackTrace();
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
	
	@PostMapping("/postJob")
	public ResponseEntity<?> postJob(@RequestBody PostJobRequest r){
		try {
			int user_id = userService.getCurrentUser().getUserId();
			var company_id = companyService.findCompanyByUserId(user_id).getCompanyId();
			var closing_date = r.getClosing_date();
			var description = r.getDescription();
			var employment_type = r.getEmployment_type();
			var locations = r.getLocationRequest();
			var requirements = r.getRequirements();
			var responsibilities = r.getResponsibilities();
			var salary_range = r.getSalary_range();
			var title = r.getTitle();
			String location = "";
			for(int i = 0; i < locations.length; i++) {
				location = location.concat(locations[i]).concat("-");
			}
			if (location.length() > 0) {
			    location = location.substring(0, location.length() - 1);
			}
			int count = postingService.insertJobPosting(closing_date, company_id, description, employment_type, location, requirements, responsibilities, salary_range, title);
			if(count == 1) {
				return ResponseEntity.ok(null);
			}
			else {
				return ResponseEntity.status(StatusCode.TOO_LONG.getCode()).body(null); //555 文字数制限を超えた／Input is too long
			}
		}
		catch(Exception e) {
			e.printStackTrace();
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
	
	@PutMapping("/modifyJob")
	public ResponseEntity<?> modifyJob(@RequestParam Integer posting_id, @RequestBody PostJobRequest r){
		try {
			int user_id = userService.getCurrentUser().getUserId();
			var company_id = companyService.findCompanyByUserId(user_id).getCompanyId();
			var jobs = postingService.findJobPostingByCompanyId(company_id, 1, 100);
			var closing_date = r.getClosing_date();
			var description = r.getDescription();
			var employment_type = r.getEmployment_type();
			var locations = r.getLocationRequest();
			var requirements = r.getRequirements();
			var responsibilities = r.getResponsibilities();
			var salary_range = r.getSalary_range();
			var title = r.getTitle();
			String location = "";
			for(int i = 0; i < locations.length; i++) {
				if(locations[i]!=null) {
					location = location.concat(locations[i]).concat("-");
				}
			}
			if (location.length() > 0) {
			    location = location.substring(0, location.length() - 1);
			}
			if(jobs.getContent().stream().anyMatch(jobSearchResponse -> (jobSearchResponse.getJob_posting_id() == posting_id))) {
				int count = postingService.modifyJobPosting(posting_id, closing_date, description, employment_type, location, requirements, responsibilities, salary_range, title);
				if(count == 1) {
					return ResponseEntity.ok(null);
				}
				else {
					return ResponseEntity.status(StatusCode.TOO_LONG.getCode()).body(null); //555 文字数制限を超えた／Input is too long
				}
			}else {
				return ResponseEntity.status(StatusCode.NOT_FOUND.getCode()).body(StatusCode.NOT_FOUND.getMessage()); //404
			}
		}
		catch(Exception e) {
			e.printStackTrace();
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
	
	@GetMapping("/getUnexpiredJob")
	public ResponseEntity<?> getMyUnexpiredCompanyJob(@RequestParam(defaultValue = "1") Integer page, @RequestParam(defaultValue = "10") Integer size){
		try {
			int user_id = userService.getCurrentUser().getUserId();
			var company_id = companyService.findCompanyByUserId(user_id).getCompanyId();
			return ResponseEntity.ok(postingService.findUnexpiredJobPostingByCompanyId(company_id, new Date(), page, size));
		}
		catch(Exception e) {
			e.printStackTrace();
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
	
	@GetMapping("/getJob")
	public ResponseEntity<?> getMyCompanyJob(@RequestParam(defaultValue = "1") Integer page, @RequestParam(defaultValue = "10") Integer size){
		try {
			int user_id = userService.getCurrentUser().getUserId();
			var company_id = companyService.findCompanyByUserId(user_id).getCompanyId();
			return ResponseEntity.ok(postingService.findJobPostingByCompanyId(company_id, page, size));
		}
		catch(Exception e) {
			e.printStackTrace();
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
	
	@GetMapping("/getExpiredJob")
	public ResponseEntity<?> getMyExpiredCompanyJob(@RequestParam(defaultValue = "1") Integer page, @RequestParam(defaultValue = "10") Integer size){
		try {
			int user_id = userService.getCurrentUser().getUserId();
			var company_id = companyService.findCompanyByUserId(user_id).getCompanyId();
			return ResponseEntity.ok(postingService.findExpiredJobPostingByCompanyId(company_id, new Date(), page, size));
		}
		catch(Exception e) {
			e.printStackTrace();
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
	
	@DeleteMapping("/deleteJob")
	public ResponseEntity<?> deleteJob(@RequestParam Integer posting_id){
		try {
			int user_id = userService.getCurrentUser().getUserId();
			var company_id = companyService.findCompanyByUserId(user_id).getCompanyId();
			var jobs = postingService.findJobPostingByCompanyId(company_id, 1, 100);
			if(jobs.getContent().stream().anyMatch(jobSearchResponse -> (jobSearchResponse.getJob_posting_id() == posting_id))) {
				postingService.deleteJobPostingById(posting_id);
				return ResponseEntity.status(StatusCode.OK.getCode()).body(null); //201
			}
			else {
				return ResponseEntity.status(StatusCode.NOT_FOUND.getCode()).body(StatusCode.NOT_FOUND.getMessage()); //404
			}
		}catch(Exception e) {
			e.printStackTrace();
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
	
	@GetMapping("/getApplicationByJobPostingId")
	public ResponseEntity<?> getApplicationByJobPostingId(@RequestParam Integer job_posting_id, @RequestParam(defaultValue = "1")Integer page, @RequestParam(defaultValue = "10")Integer size, @RequestParam(defaultValue = "0")Integer status){
		try {
			var result = applicationService.findDetailedJobApplicationByJobPostingId(job_posting_id, page, size, status);
			return ResponseEntity.ok(result);
		}catch(Exception e) {
			e.printStackTrace();
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
	
	@PutMapping("/changeApplicationStatus")
	public ResponseEntity<?> changeApplicationStatus(@RequestBody ApplicationStatusRequest r){
		String application_status;
		var application_id = r.getApplication_id();
		var status = r.getStatus();
		var new_note = r.getNew_note();
		switch(status) {
			case 1:
				application_status = "Applied";
				break;
			case 2:
				application_status = "Interview";
				break;
			case 3:
				application_status = "Offer";
				break;
			case 4:
				application_status = "Rejected";
				break;
			case 5:
				application_status = "Hired";
				break;
			default:
				return ResponseEntity.badRequest().body(null);
		}
		try {
			int user_id = userService.getCurrentUser().getUserId();
			var company_name = companyService.findCompanyByUserId(user_id).getCompanyName();
			int count = applicationService.modifyJobApplication(application_id, application_status, new_note, user_id, company_name);
			if(count == 1) {
				return ResponseEntity.ok(null);
			}
			else {
				return ResponseEntity.badRequest().body(null);
			}
		}catch(Exception e) {
			e.printStackTrace();
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
	
	@PostMapping("/sendMessage")
	public ResponseEntity<?> sendMessage(@RequestBody MessageRequest r){
		try {
			var application_id = r.getApplication_id();
			var message = r.getMessage();
			int user_id = userService.getCurrentUser().getUserId();
			int count = messageService.insertMessage(user_id, application_id, message);
			if(count == 1) {
				return ResponseEntity.ok(null);
			}else {
				return ResponseEntity.badRequest().body(null);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.badRequest().body(e.getMessage());
		}
		
	}
	
	@GetMapping("/getMessage")
	public ResponseEntity<?> getMessage(@RequestParam Integer application_id){
		try {
			int user_id = userService.getCurrentUser().getUserId();
			var messageList = messageService.findMessageByApplicationId(application_id).stream().collect(Collectors.toList());;
			message myLatestMessage = messageList
			.stream()
			.filter(msg -> msg.getSenderId() == user_id)
			.findFirst()
			.orElse(null);
			if(myLatestMessage != null) {
				messageService.setRead(myLatestMessage.getMessageId());
			}
			return ResponseEntity.ok(messageList);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
	
	@GetMapping("/getReview")
	public ResponseEntity<?> getReview(@RequestParam(defaultValue = "1")Integer page, @RequestParam(defaultValue = "10")Integer size, @RequestParam(defaultValue = "0")Integer order){
		try {
			int user_id = userService.getCurrentUser().getUserId();
			int company_id = companyService.findCompanyByUserId(user_id).getCompanyId();
			return ResponseEntity.ok(reviewService.findReviewByCompanyId(company_id, page, size, order)); //0/1 order by date desc/asc; 2/3 order by rating desc/asc
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
}
