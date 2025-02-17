package com.su.JobTracker.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import com.su.JobTracker.service.*;
import com.su.JobTracker.utils.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.su.JobTracker.model.*;
import com.su.JobTracker.request.LoginRequest;
import com.su.JobTracker.request.RegisterRequest;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
	@Autowired
	private UserService userService;
	private final JwtUtil jwtUtil = new JwtUtil();
	
	public class userInfo{
		public String email;
		public String username;
		public String role;
		public String token;
		public int userId;
	}

	/**
	 * 
	 * @param email 
	 * @param password
	 * @return user information with a 15mins token
	 */
	@PostMapping("/login")
	@PreAuthorize("permitAll()")
	public ResponseEntity<?> loginUser(@RequestBody(required = true)LoginRequest loginRequest) {
		String password = loginRequest.getPassword();
		String email = loginRequest.getEmail();
		if(email.isEmpty()) {
			return ResponseEntity.status(StatusCode.USER_NOT_FOUND.getCode()).body("Email cannot be empty");
		}
		try {
			var user = userService.login(email, password);
			var token = jwtUtil.generateToken(user.getEmail(), user.getRole());
			var userWithToken = new userInfo();
			userWithToken.email = user.getEmail();
			userWithToken.role = user.getRole();
			userWithToken.username = user.getName();
			userWithToken.token = token;
			userWithToken.userId = user.getUserId();
			GrantedAuthority authority = (GrantedAuthority) () -> "ROLE_" + userWithToken.role.toUpperCase();
            SecurityContextHolder.getContext().setAuthentication(
                    new UsernamePasswordAuthenticationToken(userWithToken.email, null, List.of(authority))
            );
            System.out.println("OK");
			return ResponseEntity.ok(userWithToken);
		}
		catch(Exception e){
			return ResponseEntity.status(Integer.parseInt(e.getMessage())).body("Failed");
		}
	}
	
	@GetMapping("/login1")
	@PreAuthorize("permitAll()")
	public ResponseEntity<?> loginUser1(String email, String password) {
		return ResponseEntity.ok("Good");
	}
	
	@PostMapping("/logout")
	@PreAuthorize("permitAll()")
	public ResponseEntity<String> logout(HttpServletRequest request, HttpServletResponse response) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            new SecurityContextLogoutHandler().logout(request, response, authentication);
        }
        return ResponseEntity.ok(StatusCode.LOGGED_OUT.getStringCode());  //ログアウトしました／Logged out successfully.
	}
	

	@PostMapping("/register")
	@PreAuthorize("permitAll()")
	public ResponseEntity<?> registerUser(@RequestBody(required = true)RegisterRequest registerRequest){
		var newUser = new user();
		newUser.setName(registerRequest.getUsername());
		newUser.setEmail(registerRequest.getEmail());
		newUser.setRole(registerRequest.getRole());
		newUser.setPassword(registerRequest.getPassword());
		try {
			int statusCode = userService.insertUser(newUser);
			if(statusCode == StatusCode.SUCCESS.getCode()) {
				return ResponseEntity.status(HttpStatus.CREATED).body(null);
			}else if(statusCode == StatusCode.USER_EXIST.getCode()){
				return ResponseEntity.status(statusCode).body("904 ユーザー存在している／User already exists");
			}else {
				return ResponseEntity.status(statusCode).body("900 登録失敗／Register failed");
			}
		}
		catch(Exception e) {
			return ResponseEntity.status(Integer.parseInt(e.getMessage())).body("Failed");
		}
	}
}
