package com.example.demo.controller;

import com.example.demo.client.PaymentClient;
import com.example.demo.client.RechargeClient;
import com.example.demo.dto.PaymentHistoryDto;
import com.example.demo.dto.RechargeHistoryDto;
import com.example.demo.dto.UserProfileDto;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserProfileController {

    private final UserRepository userRepository;
    private final RechargeClient rechargeClient;
    private final PaymentClient paymentClient;
    private final JwtUtil jwtUtil;

    // 👤 GET /user/profile — returns the logged-in user's profile
    @GetMapping("/profile")
    public ResponseEntity<UserProfileDto> getProfile(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7).trim();
        String email = jwtUtil.extractEmail(token);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserProfileDto profile = new UserProfileDto(
                user.getId(),
                user.getUserName(),
                user.getEmail(),
                user.getPhoneNumber(),
                user.getCountryCode(),
                user.getRole()
        );

        return ResponseEntity.ok(profile);
    }

    // 📋 GET /user/history/recharges — returns recharge transaction history
    @GetMapping("/history/recharges")
    public ResponseEntity<List<RechargeHistoryDto>> getRechargeHistory(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7).trim();
        Long userId = jwtUtil.extractUserId(token);

        List<RechargeHistoryDto> history = rechargeClient.getRechargesByUserId(userId);
        return ResponseEntity.ok(history);
    }
}
