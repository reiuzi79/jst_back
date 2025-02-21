package com.su.JobTracker.service;
import com.su.JobTracker.mapper.*;
import com.su.JobTracker.model.*;
import com.su.JobTracker.response.CompanyResponse;
import com.su.JobTracker.response.Page;
import com.su.JobTracker.utils.StatusCode;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CompanyService {
	@Autowired
	private companyMapper companyMapper;
	@Autowired
	private userCompanyMapper userCompanyMapper;
	
	public int insertCompany(String company_name, String[] locations, String industry, String website, int user_id) {
		try {
			String location = "";
			for(int i = 0; i < locations.length; i++) {
				if(locations[i]!=null) {
					location = location.concat(locations[i]).concat("-");
				}
			}
			if (location.length() > 0) {
			    location = location.substring(0, location.length() - 1);
			}
			if(company_name.length() > 255 || location.length() > 255 || industry.length() > 255 || website.length() > 255) {
				return 0;
			}
			company company = new company();
			company.setCompanyName(company_name);
			company.setCreatedAt(new Date());
			company.setUpdatedAt(new Date());
			company.setIndustry(industry);
			company.setLocation(location);
			company.setWebsite(website);
			companyMapper.insertCompany(company);
			
			userCompany userCompany = new userCompany();
			userCompany.setCompanyId(company.getCompanyId());
			userCompany.setUserId(user_id);
			userCompanyMapper.insert(userCompany);
			return StatusCode.OK.getCode();
		}catch(Exception e) {
			e.printStackTrace();
			return StatusCode.UNKNOWN_ERROR.getCode();
		}
	}
	
	@Transactional
	public int deleteCompanyById(int company_id) {
		return companyMapper.deleteCompany(company_id);
	}
	
	public int modifyCompany(int company_id, String company_name, String[] locations, String industry, String website) {
		String location = "";
		for(int i = 0; i < locations.length; i++) {
			if(locations[i]!=null) {
				location = location.concat(locations[i]).concat("-");
			}
		}
		if (location.length() > 0) {
		    location = location.substring(0, location.length() - 1);
		}
		if(company_name.length() > 255 || location.length() > 255 || industry.length() > 255 || website.length() > 255) {
			return 0;
		}
		company company = companyMapper.selectByPrimaryKey(company_id);
		company.setCompanyName(company_name);
		company.setLocation(location);
		company.setIndustry(industry);
		company.setWebsite(website);
		company.setUpdatedAt(new Date());
		return companyMapper.updateByPrimaryKey(company);
	}

	
	public Integer findRepresentativeIdByCompanyId(int company_id) {
		var example = new userCompanyExample();
		var criteria = example.createCriteria();
		criteria.andCompanyIdEqualTo(company_id);
		
		return userCompanyMapper.selectByExample(example).getFirst().getUserId();
	}
	
	public company findCompanyById(int company_id) throws Exception{
		return companyMapper.selectByPrimaryKey(company_id);
	}
	
	public company findCompanyByUserId(int user_id) {
		var example = new userCompanyExample();
		var criteria = example.createCriteria();
		criteria.andUserIdEqualTo(user_id);
		var company_user = userCompanyMapper.selectByExample(example);
		if(company_user.size() == 0) {
			return null;
		}
		var company_id = company_user.getFirst().getCompanyId();
		return companyMapper.selectByPrimaryKey(company_id);
	}
	
	public company findCompanyByNameAndUserId(String company_name, int user_id) {
		return companyMapper.findCompanyByNameAndUserId(company_name, user_id);
	}
	
	public Page<CompanyResponse> findCompanyByName(String company_name, Integer page, Integer size) {
		int offset = (page - 1) * size;
        int limit = size;
		var example = new companyExample();
		var criteria = example.createCriteria();
		criteria.andCompanyNameLike(company_name);
		int totalCount = (int) companyMapper.countByExample(example);
		int totalPages = (int) Math.ceil((double) totalCount / size);
		List<CompanyResponse> list = companyMapper.findCompanyByName(company_name, offset, limit);
		var result = new Page<>(list, page, size, totalCount, totalPages);
		return result;
	}
}
