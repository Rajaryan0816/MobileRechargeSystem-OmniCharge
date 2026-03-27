package com.example.demo.controller;

import com.example.demo.client.OperatorClient;
import com.example.demo.dto.OperatorResponseDto;
import com.example.demo.dto.PlanResponseDto;
import com.example.demo.dto.RechargeRequestDto;
import com.example.demo.model.Recharge;
import com.example.demo.service.RechargeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recharges")
@RequiredArgsConstructor
public class RechargeController {

    private final RechargeService rechargeService;
    private final OperatorClient operatorClient;

    @PostMapping
    public ResponseEntity<Recharge> processRecharge(@Valid @RequestBody RechargeRequestDto requestDto, jakarta.servlet.http.HttpServletRequest request) {
        Long authenticatedUserId = (Long) request.getAttribute("userId");
        String authenticatedUserEmail = (String) request.getAttribute("email");
        
        if (authenticatedUserId != null) {
            requestDto.setUserId(authenticatedUserId);
            requestDto.setUserEmail(authenticatedUserEmail);
            requestDto.setUserType(com.example.demo.enums.UserType.REGISTERED);
        } else if (requestDto.getUserType() == null) {
            requestDto.setUserType(com.example.demo.enums.UserType.GUEST);
        }
        
        Recharge recharge = rechargeService.processRecharge(requestDto);
        return ResponseEntity.ok(recharge);
    }

    @GetMapping("/operators")
    public ResponseEntity<List<OperatorResponseDto>> getOperators() {
        return ResponseEntity.ok(operatorClient.getAllOperators().getData());
    }

    @GetMapping("/operators/{operatorId}/plans")
    public ResponseEntity<List<PlanResponseDto>> getPlansForOperator(@PathVariable Long operatorId) {
        return ResponseEntity.ok(operatorClient.getPlansForOperator(operatorId).getData());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Recharge>> getRechargesByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(rechargeService.getRechargesByUserId(userId));
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<Recharge> getRechargeById(@PathVariable Long id) {
        return ResponseEntity.ok(rechargeService.getRechargeById(id));
    }
}