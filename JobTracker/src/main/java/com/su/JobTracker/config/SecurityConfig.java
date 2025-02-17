package com.su.JobTracker.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfiguration;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.authentication.AuthenticationManager;
import static org.springframework.security.config.Customizer.withDefaults;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfiguration;
@Configuration
@EnableWebSecurity
public class SecurityConfig{
	
    @Bean
    protected SecurityFilterChain configure(HttpSecurity http) throws Exception {
        http
    		.addFilterBefore(new JwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
        	.authorizeHttpRequests((authz) -> authz
        			.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
        			.requestMatchers("/api/auth/**").permitAll()
        			.anyRequest().authenticated()  // Other endpoints require authentication
        			)
        	.logout((logout) -> logout
                    .invalidateHttpSession(true)  // Invalidate the session
                    .clearAuthentication(true)  // Clear authentication data
                    .deleteCookies("JSESSIONID")  // Delete session cookies
        			)
        	.sessionManagement((management) -> management
					.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        	.httpBasic(withDefaults())
        	.csrf((csrf) -> csrf
        			.disable());
        return http.build();
    }
    
}