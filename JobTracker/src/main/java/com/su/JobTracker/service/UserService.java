package com.su.JobTracker.service;
import org.springframework.beans.factory.annotation.Autowired;
import com.su.JobTracker.config.*;
import org.springframework.security.core.context.SecurityContextHolder;
import com.su.JobTracker.utils.*;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.List;
import com.su.JobTracker.model.user;
import com.su.JobTracker.model.userExample;
import com.su.JobTracker.mapper.userMapper;

@Service
public class UserService {
	@Autowired
	private userMapper userMapper;
	
	public List<user> getAllUsers(){
		var example = new userExample();
		//select * from registered_users
		return userMapper.selectByExample(example);
	}
	
	public int changePassword(int userId, String newPassword) throws Exception{
		newPassword = MD5Util.getMD5((newPassword + "_JTSalt"));
		
		return userMapper.updatePassword(userId, newPassword);
	}
	
	public int insertUser(user user) throws Exception{
		user.setPassword(MD5Util.getMD5((user.getPassword() + "_JTSalt")));
		try {
			if (findByEmail(user.getEmail()) != null){
				return StatusCode.USER_EXIST.getCode(); //904 ユーザー存在している／User already exists
			}
		}catch(RuntimeException e) //If user not exists
		{
			//insert into registered_users values(...)
			user.setCreatedAt(new Date());
			user.setUpdatedAt(new Date());
			if(userMapper.insertSelective(user)== 1) {
				return StatusCode.OK.getCode();
			}
		}
		return StatusCode.REGISTER_FAILED.getCode(); //900 登録失敗／Register failed
	}
	
	public user findById(Integer id) throws Exception{
		var example = new userExample();
		var cri = example.createCriteria();
		//select * from registered_users where email = email
		cri.andUserIdEqualTo(id);
		List<user> l = userMapper.selectByExample(example);
		if(l.isEmpty()) {
			throw new RuntimeException(StatusCode.USER_NOT_FOUND.getStringCode()); //ユーザーが存在しない／User not found
		}
		return l.get(0);
	}
	
	public UserInfo findByEmail(String email) throws Exception{
		var example = new userExample();
		var cri = example.createCriteria();
		//select * from registered_users where email = email
		cri.andEmailEqualTo(email);
		List<user> l = userMapper.selectByExample(example);
		if(l.isEmpty()) {
			throw new RuntimeException(StatusCode.USER_NOT_FOUND.getStringCode()); //ユーザーが存在しない／User not found
		}
		user u = l.get(0);
		UserInfo userInfo = new UserInfo();
		userInfo.setEmail(u.getEmail());
		userInfo.setPassword(u.getPassword());
		userInfo.setRole(u.getRole());
		userInfo.setUsername(u.getName());
		return userInfo;
	}
	
	public user findUserByEmail(String email) throws Exception{
		var example = new userExample();
		var cri = example.createCriteria();
		//select * from registered_users where email = email
		cri.andEmailEqualTo(email);
		List<user> l = userMapper.selectByExample(example);
		if(l.isEmpty()) {
			throw new RuntimeException(StatusCode.USER_NOT_FOUND.getStringCode()); //ユーザーが存在しない／User not found
		}
		return l.get(0);
	}
	
	public int checkOldPassword(int id, String password) {
		password = MD5Util.getMD5((password + "_JTSalt"));
		var u = userMapper.selectByPrimaryKey(id);
		if(password.equals(u.getPassword())) {
			return 1;
		}else {
			return 0;
		}
	}
	
	public user login(String email, String password) throws Exception{
		password = MD5Util.getMD5((password + "_JTSalt"));
		var u = findUserByEmail(email);
		if(u.getPassword().equals(password)){
			return u;
		}
		throw new RuntimeException(StatusCode.INVALID_CREDENTIAL.getStringCode()); //102 パスワードが正しくない／Password does not match
	}
	
	public int disableUser(String email) throws Exception{
		var u = findUserByEmail(email);
		if(userMapper.disableUser(u.getUserId()) == 1) {
			return StatusCode.SUCCESS.getCode();
		}
		return StatusCode.DISABLE_FAILED.getCode();  //109 退会失敗／Failed to disable
	}
	
	public user getCurrentUser() throws Exception {
		var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            String email = (String) authentication.getPrincipal();
            var user = findUserByEmail(email);
            return user;
        }
        return null;
	}
}
