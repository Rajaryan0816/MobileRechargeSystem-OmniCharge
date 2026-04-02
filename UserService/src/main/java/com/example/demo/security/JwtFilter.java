package com.example.demo.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();
        System.out.println("🔍 Incoming Request Path: " + path);

        // ✅ Allow Swagger UI paths
        if (path.contains("/swagger-ui") ||
            path.contains("/v3/api-docs") ||
            path.contains("/swagger-resources") ||
            path.contains("/webjars")) {
            System.out.println("✅ Skipping JwtFilter for Swagger path: " + path);
            filterChain.doFilter(request, response);
            return;
        }

        // ✅ Allow public endpoints
        if (path.contains("/auth/")) {
            System.out.println("✅ Skipping JwtFilter for Auth path: " + path);
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader("Authorization");
        System.out.println("👉 HEADER: " + authHeader);

        // ❌ Missing or invalid header
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            System.out.println("❌ Missing or invalid Authorization header");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        try {
            // 🔑 Extract token
            String token = authHeader.substring(7).trim();
            System.out.println("👉 TOKEN: " + token);

            // 🔍 Extract data from token
            String email = jwtUtil.extractEmail(token);
            String role = jwtUtil.extractRole(token);

            System.out.println("✅ EMAIL: " + email);
            System.out.println("✅ ROLE: " + role);
            System.out.println("👉 PATH: " + path);

            // 🔒 ADMIN endpoints → ADMIN only
            if (path.startsWith("/admin") && !"ADMIN".equals(role)) {
                System.out.println("❌ Access denied: Not ADMIN");
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                return;
            }

            // 🔒 OPERATOR endpoints → ADMIN + OPERATOR allowed
            if (path.startsWith("/operator") &&
                !("ADMIN".equals(role) || "OPERATOR".equals(role))) {
                System.out.println("❌ Access denied: Not OPERATOR or ADMIN");
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                return;
            }

            // ✅ Set authentication in Spring Security
            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(
                            email,
                            null,
                            Collections.singletonList(
                                    new SimpleGrantedAuthority("ROLE_" + role)
                            )
                    );

            authToken.setDetails(
                    new WebAuthenticationDetailsSource().buildDetails(request)
            );

            SecurityContextHolder.getContext().setAuthentication(authToken);

            System.out.println("✅ Authentication set for: " + email + " with role: " + role);

        } catch (Exception e) {
            System.out.println("❌ TOKEN ERROR: " + e.getMessage());
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        // ✅ Continue filter chain
        filterChain.doFilter(request, response);
    }
}