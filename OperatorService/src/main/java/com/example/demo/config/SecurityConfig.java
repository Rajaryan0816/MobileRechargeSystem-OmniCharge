package com.example.demo.config;

import com.example.demo.security.JwtFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtFilter jwtFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .authorizeHttpRequests(auth -> auth
                // Swagger UI
                .requestMatchers("/swagger-ui/**").permitAll()
                .requestMatchers("/swagger-ui.html").permitAll()
                .requestMatchers("/v3/api-docs/**").permitAll()
                .requestMatchers("/swagger-resources/**").permitAll()
                .requestMatchers("/webjars/**").permitAll()

                // Public Operator endpoints
                .requestMatchers("/operator/active").permitAll()
                .requestMatchers("/operator/{id}").permitAll()
                .requestMatchers("/operator/plans/active").permitAll()
                .requestMatchers("/operator/plans/{id}").permitAll()
                .requestMatchers("/operator/plans/operator/**").permitAll()
                .requestMatchers("/operator/plans/categories").permitAll()
                .requestMatchers("/operator/plans/tags").permitAll()
                .requestMatchers("/operator/plans/category/**").permitAll()
                .requestMatchers("/operator/plans/tag/**").permitAll()
                .requestMatchers("/api/operators").permitAll()
                .requestMatchers("/api/operators/**").permitAll()

                // 🔒 Protected
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}