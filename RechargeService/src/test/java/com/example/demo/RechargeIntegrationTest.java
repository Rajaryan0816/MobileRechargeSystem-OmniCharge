package com.example.demo;

import com.example.demo.model.Recharge;
import com.example.demo.repository.RechargeRepository;
import com.example.demo.service.RechargeService;
import com.example.demo.dto.RechargeRequestDto;
import com.example.demo.enums.RechargeStatus;
import com.example.demo.enums.UserType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import com.example.demo.client.OperatorClient;
import com.example.demo.client.PaymentClient;
import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.PlanResponseDto;
import com.example.demo.dto.PaymentResponseDto;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;

@SpringBootTest
public class RechargeIntegrationTest {

    @Autowired
    private RechargeService rechargeService;

    @Autowired
    private RechargeRepository rechargeRepository;

    @MockitoBean
    private OperatorClient operatorClient;

    @MockitoBean
    private PaymentClient paymentClient;

    @Test
    void testRechargeFlow_Integration() {
        // Prepare Mock Data
        PlanResponseDto planDto = PlanResponseDto.builder()
                .id(101L)
                .operatorId(1L)
                .amount(200.0)
                .build();
        ApiResponse<PlanResponseDto> apiResponse = new ApiResponse<>();
        apiResponse.setData(planDto);

        when(operatorClient.getPlanById(101L)).thenReturn(apiResponse);

        PaymentResponseDto paymentResponse = PaymentResponseDto.builder()
                .status("SUCCESS")
                .transactionId("INT-TXN-001")
                .build();
        when(paymentClient.processPayment(any())).thenReturn(paymentResponse);

        // Execute Request
        RechargeRequestDto request = RechargeRequestDto.builder()
                .mobileNumber("1122334455")
                .operatorId(1L)
                .planId(101L)
                .userId(10L)
                .userType(UserType.REGISTERED)
                .build();

        Recharge result = rechargeService.processRecharge(request);

        // Assertions
        assertNotNull(result);
        assertEquals(RechargeStatus.SUCCESS, result.getStatus());
        assertEquals("INT-TXN-001", result.getTransactionId());

        // Verify Data in DB
        Recharge dbRecharge = rechargeRepository.findById(result.getId()).orElse(null);
        assertNotNull(dbRecharge);
        assertEquals(BigDecimal.valueOf(200.0), dbRecharge.getAmount());
    }
}
