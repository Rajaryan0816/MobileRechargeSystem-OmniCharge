package com.example.demo.service;

import com.example.demo.client.OperatorClient;
import com.example.demo.client.PaymentClient;
import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.PaymentRequestDto;
import com.example.demo.dto.PlanResponseDto;
import com.example.demo.dto.PaymentResponseDto;
import com.example.demo.dto.RechargeRequestDto;
import com.example.demo.enums.RechargeStatus;
import com.example.demo.model.Recharge;
import com.example.demo.repository.RechargeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RechargeServiceImpl implements RechargeService {

    private final RechargeRepository rechargeRepository;
    private final PaymentClient paymentClient;
    private final OperatorClient operatorClient;

    @Override
    public Recharge processRecharge(RechargeRequestDto requestDto) {
        
        // Fetch Plan Details from OperatorService
        ApiResponse<PlanResponseDto> planResponse = operatorClient.getPlanById(requestDto.getPlanId());
        if (planResponse == null || planResponse.getData() == null) {
            throw new RuntimeException("Plan not found with ID: " + requestDto.getPlanId());
        }
        PlanResponseDto planDto = planResponse.getData();
        
        // Optional validation: check operator match
        if (!planDto.getOperatorId().equals(requestDto.getOperatorId())) {
            throw new RuntimeException("Plan does not belong to the correct operator");
        }

        java.math.BigDecimal fetchedAmount = java.math.BigDecimal.valueOf(planDto.getAmount());

        // 1. Create a PENDING Recharge Record
        Recharge recharge = Recharge.builder()
                .mobileNumber(requestDto.getMobileNumber())
                .operatorId(requestDto.getOperatorId())
                .planId(requestDto.getPlanId())
                .amount(fetchedAmount)
                .userType(requestDto.getUserType())
                .userId(requestDto.getUserId())
                .userEmail(requestDto.getUserEmail())
                .status(RechargeStatus.PENDING)
                .build();
                
        recharge = rechargeRepository.save(recharge);
        log.info("Recharge initiated with ID: {}, Status: PENDING", recharge.getId());

        // 2. Prepare Payment Request
        PaymentRequestDto paymentRequest = PaymentRequestDto.builder()
                .rechargeId(recharge.getId())
                .amount(recharge.getAmount())
                .userId(recharge.getUserId())
                .userEmail(recharge.getUserEmail())
                .paymentMethod("CREDIT_CARD") // Default or handled dynamically
                .build();

        // 3. Call Payment Service
        try {
            PaymentResponseDto paymentResponse;
            if (com.example.demo.enums.UserType.GUEST.equals(requestDto.getUserType())) {
                paymentResponse = paymentClient.processGuestPayment(paymentRequest);
            } else {
                paymentResponse = paymentClient.processPayment(paymentRequest);
            }
            
            if ("SUCCESS".equalsIgnoreCase(paymentResponse.getStatus())) {
                recharge.setStatus(RechargeStatus.SUCCESS);
                recharge.setTransactionId(paymentResponse.getTransactionId());
                log.info("Payment successful for Recharge ID: {}, Transaction ID: {}", recharge.getId(), paymentResponse.getTransactionId());
            } else {
                recharge.setStatus(RechargeStatus.FAILED);
                log.warn("Payment failed for Recharge ID: {}. Reason: {}", recharge.getId(), paymentResponse.getMessage());
            }
        } catch (Exception e) {
            log.error("Error communicating with Payment Service for Recharge ID: {}", recharge.getId(), e);
            recharge.setStatus(RechargeStatus.PENDING);
        }

        // 4. Update and return the final state
        return rechargeRepository.save(recharge);
    }

    @Override
    public List<Recharge> getRechargesByUserId(Long userId) {
        log.info("Fetching recharges for userId: {}", userId);
        return rechargeRepository.findByUserId(userId);
    }

    @Override
    public Recharge getRechargeById(Long id) {
        return rechargeRepository.findById(id).orElseThrow(() -> new RuntimeException("Recharge not found for ID: " + id));
    }
}
