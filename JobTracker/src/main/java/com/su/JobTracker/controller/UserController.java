package com.su.JobTracker.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import com.su.JobTracker.service.*;
import com.su.JobTracker.utils.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.su.JobTracker.model.*;
import com.su.JobTracker.request.PasswordRequest;

@RestController
@RequestMapping("/api/users")
public class UserController {
	@Autowired
	private UserService userService;
	
    private final JwtUtil jwtUtil = new JwtUtil();
	
	@GetMapping("/getAllUsers")
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public List<user> getAllUsers(){
		System.out.println("getAllUsers");
		return userService.getAllUsers();
	}
	
	@GetMapping("/getCurrentUser")
	public ResponseEntity<?> getCurrentUser(){
		user currentUser = null;
		try {
			currentUser = userService.getCurrentUser();
		} catch (Exception e) {
			return ResponseEntity.status(StatusCode.USER_NOT_FOUND.getCode()).body("ユーザーが存在しない／User not found");
		}
		if(currentUser == null){
			return ResponseEntity.status(StatusCode.NOT_LOGIN.getCode()).body("100 ログインされていない／No user logged in.");  //ログインされていない／No user logged in.
		}
		return ResponseEntity.ok(currentUser);
	}
	
	@PostMapping("/changePassword")
	@PreAuthorize("permitAll()")
	public ResponseEntity<?> changePassword(@RequestBody PasswordRequest r){
		System.out.println(r.getNewPassword());
		System.out.println(r.getOldPassword());
		var email = r.getEmail();
		var oldPassword = r.getOldPassword();
		var newPassword = r.getNewPassword();
		var role = SecurityContextHolder.getContext().getAuthentication().getAuthorities().iterator().next().getAuthority();
		if(!email.equals("Default") && !role.equals("ROLE_ADMIN")) { //Non-admin passes an email to change other user's password
			throw new AccessDeniedException("Access denied!"); 
		}
		else if(!email.equals("Default") && role.equals("ROLE_ADMIN")) { //admin passes an email to change other user's password
			try {
				var user = userService.findUserByEmail(email);
				if(user == null) {
					return ResponseEntity.status(StatusCode.USER_NOT_FOUND.getCode()).body("ユーザーが存在しない／User not found");
				}
				if(userService.changePassword(user.getUserId(), newPassword) == 1) {
					return ResponseEntity.ok(null);
				}else {
					return ResponseEntity.badRequest().body("Failed");
				}
			} catch (Exception e) {
				return ResponseEntity.status(Integer.parseInt(e.getMessage())).body(e.getMessage());
			}
		}
		else if (email.equals("Default")){ //change self password
			try {
				var user = userService.getCurrentUser();
				if(user == null) {
					return ResponseEntity.status(StatusCode.USER_NOT_FOUND.getCode()).body("ユーザーが存在しない／User not found");
				}
				if(userService.checkOldPassword(user.getUserId(), oldPassword) == 0) {
					return ResponseEntity.status(StatusCode.INVALID_CREDENTIAL.getCode()).body(null);
				}
				if(userService.changePassword(user.getUserId(), newPassword) == 1) {
					return ResponseEntity.ok(null);
				}else {
					return ResponseEntity.badRequest().body("Failed");
				}
			} catch (Exception e) {
				return ResponseEntity.badRequest().body("Failed");
			}
		}
		return null;
	}
	
	@PostMapping("/disableUser")
	public ResponseEntity<?> disableUser(HttpServletRequest request, HttpServletResponse response) throws Exception{
		var currentUser = userService.getCurrentUser();
		if (currentUser != null) {
			var authentication = SecurityContextHolder.getContext().getAuthentication();
	        if (authentication != null) {
	            new SecurityContextLogoutHandler().logout(request, response, authentication);
	        }
	        try {
				userService.disableUser(currentUser.getEmail());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return ResponseEntity.badRequest().body(null);
	}
}
