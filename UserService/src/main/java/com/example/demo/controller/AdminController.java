package com.example.demo.controller;


import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.AuthService;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private AuthService service;

    // 🔥 1. Get all users
    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userRepo.findAll();
    }

    @DeleteMapping("/delete_user")
    public String deleteUser(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
        
        userRepo.delete(user);
        return "User with email " + email + " deleted successfully";
    }

    @PostMapping("/assign_role")
    public String assignRole(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String role = request.get("role");
        
        System.out.println("Assign role endpoint called!");
        System.out.println("Email: " + email);
        System.out.println("Role: " + role);
        
        service.assignRole(email, role);
        return "Role updated";
    }
}
