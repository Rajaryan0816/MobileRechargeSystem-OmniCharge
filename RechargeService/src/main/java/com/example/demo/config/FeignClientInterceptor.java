package com.example.demo.config;


import feign.RequestInterceptor;
import feign.RequestTemplate;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
public class FeignClientInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate template) {
        ServletRequestAttributes attributes =
            (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            String authHeader = request.getHeader("Authorization");

            // If registered user → forward their JWT token
            // If guest user → no Authorization header, skip
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                template.header("Authorization", authHeader);
            }
        }
        // Guest user: no header added — operator service must allow this endpoint publicly
    }
}



//This class is used in Spring Cloud OpenFeign to automatically forward the Authorization (JWT token) when one microservice calls another.
//It copies the JWT token from the incoming request and sends it to the next service.
