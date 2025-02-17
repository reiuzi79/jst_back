package com.su.JobTracker.config;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.*;
import com.su.JobTracker.utils.*;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.su.JobTracker.model.user;
import com.su.JobTracker.service.*;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
	
	JwtUtil jwt = new JwtUtil();
	
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws java.io.IOException, jakarta.servlet.ServletException {
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring(7); // exclude "Bearer "
            //System.out.println(token);
            if(!jwt.isTokenExpired(token)) {
            	String role = jwt.extractRole(token);
            	String email = jwt.extractEmail(token);
                GrantedAuthority authority = (GrantedAuthority) () -> "ROLE_" + role.toUpperCase();
                SecurityContextHolder.getContext().setAuthentication(
                        new UsernamePasswordAuthenticationToken(email, null, List.of(authority))
                );
                //System.out.println(email);
                //System.out.println(authority.getAuthority());
            }
            
        }
        filterChain.doFilter(request, response);
    }
    
}

