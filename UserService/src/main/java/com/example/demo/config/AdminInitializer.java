package com.example.demo.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class AdminInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Override
    public void run(String... args) throws Exception {
        log.info("Checking for admin user initialization...");
        
        if (userRepository.findByUserName("admin").isEmpty()) {
            User adminUser = new User();
            adminUser.setUserName("admin");
            adminUser.setPassword("admin"); // Using plain "admin" per current project setup
            adminUser.setEmail("admin@omnicharge.com");
            adminUser.setPhoneNumber("0000000000");
            adminUser.setCountryCode("+91"); 
            adminUser.setRole("ADMIN");
            
            userRepository.save(adminUser);
            log.info("✅ Default admin user created successfully (username: 'admin', password: 'admin')");
        } else {
            log.info("✅ Admin user already exists. Skipping initialization.");
        }
    }
}
