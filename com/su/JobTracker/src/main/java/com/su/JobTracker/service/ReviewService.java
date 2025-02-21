package com.su.JobTracker.service;
import com.su.JobTracker.mapper.*;
import com.su.JobTracker.model.*;
import com.su.JobTracker.response.Page;
import com.su.JobTracker.utils.StatusCode;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReviewService {
	@Autowired
	private reviewMapper reviewMapper;
	
	public int insertReview(int company_id, int user_id, int rating, String title, String content) {
		var example = new reviewExample();
		var cri = example.createCriteria();
		cri.andCompanyIdEqualTo(company_id);
		cri.andUserIdEqualTo(user_id);
		var result = reviewMapper.selectByExample(example);
		if(!result.isEmpty()) {
			return StatusCode.ALREADY_REVIEWED.getCode(); //701 既にレビューが存在している/Already reviewed
		}
		review review = new review();
		if(rating > 5 || rating < 0) {
			return 0;
		}
		review.setContent(content);
		review.setReviewDate(new Date());
		review.setRating(rating);
		review.setCompanyId(company_id);
		review.setUserId(user_id);
		review.setTitle(title);
		return reviewMapper.insert(review);
	}
	
	public int modifyReview(int review_id, int rating, String title, String content) {
		var review = reviewMapper.selectByPrimaryKey(review_id);
		if(rating > 5 || rating < 0) {
			return 0;
		}
		review.setRating(rating);
		review.setTitle(title);
		review.setContent(content);
		review.setReviewDate(new Date());
		return reviewMapper.updateByPrimaryKeyWithBLOBs(review);
	}
	
	public int deleteReviewById(int review_id) {
		return reviewMapper.deleteByPrimaryKey(review_id);
	}
	
	public review findReviewById(int review_id) {
		return reviewMapper.selectByPrimaryKey(review_id);
	}
	
	public Page<com.su.JobTracker.response.ReviewResponse> findReviewByUserId(int user_id, int page, int size, int order){
		int offset = (page - 1) * size;
        int limit = size;
		var example = new reviewExample();
		var cri = example.createCriteria();
		cri.andUserIdEqualTo(user_id);
		int totalCount = (int)reviewMapper.countByExample(example);
	    int totalPages = (int) Math.ceil((double) totalCount / size);
	    var list = reviewMapper.findReviewByUserId(user_id, offset, limit, order);
	    var result = new Page<>(list, page, size, totalCount, totalPages);
		return result;
	}
	
	public Page<com.su.JobTracker.response.ReviewResponse> findReviewByCompanyId(int company_id, int page, int size, int order){
		int offset = (page - 1) * size;
        int limit = size;
		var example = new reviewExample();
		var cri = example.createCriteria();
		cri.andCompanyIdEqualTo(company_id);
		int totalCount = (int)reviewMapper.countByExample(example);
	    int totalPages = (int) Math.ceil((double) totalCount / size);
	    var list = reviewMapper.findReviewByCompanyId(company_id, offset, limit, order);
	    var result = new Page<>(list, page, size, totalCount, totalPages);
		return result;
	}
}
