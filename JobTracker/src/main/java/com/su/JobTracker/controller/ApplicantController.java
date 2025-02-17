package com.su.JobTracker.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.su.JobTracker.model.message;
import com.su.JobTracker.request.JobStatusRequest;
import com.su.JobTracker.request.MessageRequest;
import com.su.JobTracker.request.ReviewRequest;
import com.su.JobTracker.request.SearchJobRequest;
import com.su.JobTracker.response.*;
import com.su.JobTracker.service.*;
import com.su.JobTracker.utils.StatusCode;
import java.util.List;

@RestController
@RequestMapping("/api/applicant")
@PreAuthorize("hasRole('APPLICANT')")
public class ApplicantController {
	private final CompanyService companyService;
	private final JobPostingService postingService;
	private final JobApplicationService applicationService;
	private final MessageService messageService;
	private final ReviewService reviewService;
	private final UserService userService;
	private final SavedJobService savedJobService;
	@Autowired
	public ApplicantController(SavedJobService savedJobService, UserService userService, CompanyService companyService, JobPostingService postingService, JobApplicationService applicationService, MessageService messageService, ReviewService reviewService){
		this.companyService = companyService;
		this.savedJobService = savedJobService;
		this.applicationService = applicationService;
		this.postingService = postingService;
		this.messageService = messageService;
		this.reviewService = reviewService;
		this.userService = userService;
	}
	
	@GetMapping("/searchCompaniesByName")
	public ResponseEntity<?> searchCompany(@RequestParam String name, @RequestParam(defaultValue = "1") Integer page, @RequestParam(defaultValue = "10") Integer size){
		var result = companyService.findCompanyByName(name, page, size);
		return ResponseEntity.ok(result);
	}
	
	@GetMapping("/searchUnexpiredJobDefault")
	@PreAuthorize("permitAll()")
	public ResponseEntity<?> searchUnexpiredJob2(@RequestParam(defaultValue = "1") Integer page, @RequestParam(defaultValue = "10") Integer size) {
		try {
			var result = postingService.findUnexpiredJobPosting("", "", "", "", "", new Date(), page, size);
			return ResponseEntity.ok(result);
		}catch(Exception e) {
			e.printStackTrace();
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
	
	@PostMapping("/searchUnexpiredJob")
	public ResponseEntity<?> searchUnexpiredJob(@RequestBody SearchJobRequest r, @RequestParam(defaultValue = "1") Integer page, @RequestParam(defaultValue = "10") Integer size) {
		try {
			var company_name = r.getCompany_name();
			var title = r.getTitle();
			var locations = r.getLocation();
			var type = r.getType();
			var keyword = r.getKeyword();
			String location = "";
			if(locations != null) {
				for(int i = 0; i < locations.length; i++) {
					if(locations[i]!=null) {
						location = location.concat(locations[i]).concat("-");
					}
				}
				if (location.length() > 0) {
			    	location = location.substring(0, location.length() - 1);
				}
			}
			if(title == null) {
				title = "";
			}
			if(company_name == null) {
				company_name = "";
			}
			if(type == null) {
				type = "";
			}
			if(keyword == null) {
				keyword = "";
			}
			var result = postingService.findUnexpiredJobPosting(company_name, title, location, type, keyword, new Date(), page, size);
			var user_id = userService.getCurrentUser().getUserId();
			var savedJobs = savedJobService.findUnexpiredSavedJobByUserId(user_id, new Date());
			var savedPostingId = savedJobs.stream().map(SavedJobResponse::getJob_posting_id).collect(Collectors.toList());
			var new_result = result.getContent().stream().collect(Collectors.toList()); 
			new_result.forEach(temp -> { //if the job is saved, set isSaved to 1
			if (savedPostingId.contains(temp.getJob_posting_id())) {
		        temp.setIsSaved(1);
		        savedPostingId.remove(Integer.valueOf(temp.getJob_posting_id()));
		    }});
			result.setContent(new_result);
			return ResponseEntity.ok(result);
		}catch(Exception e) {
			e.printStackTrace();
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
	
	@PostMapping("/applyJob")
	public ResponseEntity<?> applyJob(@RequestParam Integer job_posting_id, @RequestBody JobStatusRequest r){
		try {
			var notes = r.getNotes();
			if(notes == null) {
				notes = "";
			}
			var user = userService.getCurrentUser();
			var name = user.getName();
			var user_id = user.getUserId();
			int count = applicationService.insertJobApplication(user_id, notes, job_posting_id, name);
			if(count == 1) {
				return ResponseEntity.ok(null);
			}
			else if(count == StatusCode.CANNOT_REAPPLY.getCode()) {
				return ResponseEntity.status(StatusCode.CANNOT_REAPPLY.getCode()).body("只今再応募不可/Cannot reapply now"); //702
			}
			else {
				return ResponseEntity.badRequest().body(null);
			}
		}catch(Exception e) {
			e.printStackTrace();
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
	
	@PutMapping("/acceptOffer")
	public ResponseEntity<?> acceptOffer(@RequestParam Integer job_posting_id, @RequestBody JobStatusRequest r){
		try {
			var notes = r.getNotes();
			if(notes == null) {
				notes = "";
			}
			var user = userService.getCurrentUser();
			var name = user.getName();
			var user_id = user.getUserId();
			int count = applicationService.modifyJobApplication(job_posting_id, "Hired", notes, user_id, name);
			if(count == 1) {
				return ResponseEntity.ok(null);
			}else {
				return ResponseEntity.badRequest().body(null);
			}
		}catch(Exception e) {
			e.printStackTrace();
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
	
	@PostMapping("/rejectJob")
	public ResponseEntity<?> reject(@RequestParam Integer job_posting_id, @RequestBody JobStatusRequest r){
		try {
			var notes = r.getNotes();
			if(notes == null) {
				notes = "";
			}
			var user = userService.getCurrentUser();
			var name = user.getName();
			var user_id = user.getUserId();
			int count = applicationService.modifyJobApplication(job_posting_id, "Applicant_Rejected", notes, user_id, name);
			if(count == 1) {
				return ResponseEntity.ok(null);
			}else {
				return ResponseEntity.badRequest().body(null);
			}
		}catch(Exception e) {
			e.printStackTrace();
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
	
	@GetMapping("getMyApplication")
	public ResponseEntity<?> getMyApplication(@RequestParam(defaultValue = "0") Integer status, @RequestParam(defaultValue = "1") Integer page, @RequestParam(defaultValue = "10") Integer size){
		try {
			var user_id = userService.getCurrentUser().getUserId();
			var result = applicationService.findDetailedJobApplicationByUserId(user_id, page, size, status);

			return ResponseEntity.ok(result);
		}catch(Exception e) {
			e.printStackTrace();
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
	
	@GetMapping("getJobDetail")
	public ResponseEntity<?> getJobDetail(@RequestParam Integer job_posting_id){
		try {
			var user_id = userService.getCurrentUser().getUserId();
			var result = postingService.findDetailedJobPostingById(job_posting_id);
			return ResponseEntity.ok(result);
		}catch(Exception e) {
			e.printStackTrace();
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
	
	@PostMapping("/saveJob")
	public ResponseEntity<?> save(@RequestParam Integer job_posting_id){
		try {
			var user_id = userService.getCurrentUser().getUserId();
			int count = savedJobService.insertSavedJob(user_id, job_posting_id);
			if(count == 1) {
				return ResponseEntity.ok(null);
			}
			else if(count == StatusCode.ALREADY_SAVED.getCode()) {
				return ResponseEntity.status(StatusCode.ALREADY_SAVED.getCode()).body("既に気に入っている/Already saved"); //703
			}
			else {
				return ResponseEntity.badRequest().body(null);
			}
		}catch(Exception e) {
			e.printStackTrace();
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
	
	@DeleteMapping("/delectSaved")
	public ResponseEntity<?> delete(@RequestParam Integer saved_job_id){
		try {
			int count = savedJobService.deleteSavedJob(saved_job_id);
			if(count == 1) {
				return ResponseEntity.ok(null);
			}else {
				return ResponseEntity.badRequest().body(null);
			}
		}catch(Exception e) {
			e.printStackTrace();
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
	
	@GetMapping("/getSaved")
	public ResponseEntity<?> getSaved(){
		try {
			var user_id = userService.getCurrentUser().getUserId();
			var result = savedJobService.findUnexpiredSavedJobByUserId(user_id, new Date());
			return ResponseEntity.ok(result);
		}catch(Exception e) {
			e.printStackTrace();
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
	
	@GetMapping("/getMyReview")
	public ResponseEntity<?> getMyReview(@RequestParam(defaultValue = "1") Integer page, @RequestParam(defaultValue = "10") Integer size, @RequestParam(defaultValue = "0")Integer order){
		try {
			var user_id = userService.getCurrentUser().getUserId();
			var result = reviewService.findReviewByUserId(user_id, page, size, order);
			return ResponseEntity.ok(result);
		}catch(Exception e) {
			e.printStackTrace();
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
	
	@GetMapping("/getCompanyReview")
	public ResponseEntity<?> getCompanyReview(@RequestParam Integer company_id, @RequestParam(defaultValue = "1") Integer page, @RequestParam(defaultValue = "10") Integer size, @RequestParam(defaultValue = "0")Integer order){
		var result = reviewService.findReviewByCompanyId(company_id, page, size, order);//0/1 order by date desc/asc; 2/3 order by rating desc/asc
		return ResponseEntity.ok(result);
	}
	
	@PostMapping("/writeReview")
	public ResponseEntity<?> writeReview(@RequestParam Integer company_id, @RequestBody ReviewRequest r){
		try {
			var rating = r.getRating();
			var title = r.getTitle();
			var content = r.getContent();
			var user_id = userService.getCurrentUser().getUserId();
			int count = reviewService.insertReview(company_id, user_id, rating, title, content);
			if(count == 1) {
				return ResponseEntity.ok(null);
			}
			else if(count == StatusCode.ALREADY_REVIEWED.getCode()) {
				return ResponseEntity.status(StatusCode.ALREADY_REVIEWED.getCode()).body("既にレビューが存在している/Already reviewed"); //701
			}
			else {
				return ResponseEntity.badRequest().body(null);
			}
		}catch(Exception e) {
			e.printStackTrace();
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
	
	@DeleteMapping("/deleteReview")
	public ResponseEntity<?> deleteReview(@RequestParam Integer review_id){
		try {
			int user_id = userService.getCurrentUser().getUserId();
			var review = reviewService.findReviewById(review_id);
			if(review.getUserId() != user_id) {
				return ResponseEntity.status(StatusCode.UNAUTHORIZED.getCode()).body(null);
			}
			int count = reviewService.deleteReviewById(review_id);
			if(count == 1) {
				return ResponseEntity.ok(null);
			}else {
				return ResponseEntity.badRequest().body(null);
			}
		}catch(Exception e) {
			e.printStackTrace();
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
	
	@PutMapping("/modifyReview")
	public ResponseEntity<?> modifyReview(@RequestParam Integer review_id, @RequestBody ReviewRequest r){
		try {
			int user_id = userService.getCurrentUser().getUserId();
			var rating = r.getRating();
			var title = r.getTitle();
			var content = r.getContent();
			var review = reviewService.findReviewById(review_id);
			if(review.getUserId() != user_id) {
				return ResponseEntity.status(StatusCode.UNAUTHORIZED.getCode()).body(null);
			}
			int count = reviewService.modifyReview(review_id, rating, title, content);
			if(count == 1) {
				return ResponseEntity.ok(null);
			}else {
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
}
