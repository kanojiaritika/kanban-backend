package com.kanban.kanbanProject.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

// The below annotations configure Spring Security (Tells which endpoints are protected, which filters should run etc.)
@Configuration
@EnableWebSecurity // Tells Spring Security to activate spring security for this app
public class SecurityConfig {

    @Autowired
    JwtAuthFilter jwtAuthFilter;

    // This method builds the filter chain configuration.
    // SecurityFilterChain = ordered list of filters that process requests.
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/login", "/register").permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class); // This means, add jwtAuthFilter BEFORE username password auth.

        return http.build(); // This converts your configuration into the actual SecurityFilterChain object.
    }
}
