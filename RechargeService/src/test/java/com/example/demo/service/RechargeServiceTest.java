package com.example.demo.service;

import com.example.demo.client.OperatorClient;
import com.example.demo.client.PaymentClient;
import com.example.demo.dto.*;
import com.example.demo.enums.RechargeStatus;
import com.example.demo.enums.UserType;
import com.example.demo.model.Recharge;
import com.example.demo.repository.RechargeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RechargeServiceTest {

    @Mock
    private RechargeRepository rechargeRepository;

    @Mock
    private PaymentClient paymentClient;

    @Mock
    private OperatorClient operatorClient;

    @InjectMocks
    private RechargeServiceImpl rechargeService;

    private RechargeRequestDto requestDto;
    private PlanResponseDto planDto;
    private ApiResponse<PlanResponseDto> apiResponse;

    @BeforeEach
    void setUp() {
        requestDto = RechargeRequestDto.builder()
                .mobileNumber("9876543210")
                .operatorId(1L)
                .planId(101L)
                .userId(1L)
                .userType(UserType.REGISTERED)
                .build();

        planDto = PlanResponseDto.builder()
                .id(101L)
                .operatorId(1L)
                .amount(500.0)
                .build();

        apiResponse = new ApiResponse<>();
        apiResponse.setData(planDto);
    }

    @Test
    void testProcessRecharge_Success() {
        // Mock OperatorClient
        when(operatorClient.getPlanById(101L)).thenReturn(apiResponse);

        // Mock Repository Save (Initiation)
        Recharge pendingRecharge = Recharge.builder().id(1L).status(RechargeStatus.PENDING).amount(BigDecimal.valueOf(500.0)).build();
        when(rechargeRepository.save(any(Recharge.class))).thenReturn(pendingRecharge);

        // Mock PaymentClient
        PaymentResponseDto paymentResponse = PaymentResponseDto.builder()
                .status("SUCCESS")
                .transactionId("TXN123")
                .build();
        when(paymentClient.processPayment(any(PaymentRequestDto.class))).thenReturn(paymentResponse);

        // Execute
        Recharge result = rechargeService.processRecharge(requestDto);

        // Verify
        assertNotNull(result);
        assertEquals(RechargeStatus.SUCCESS, result.getStatus());
        assertEquals("TXN123", result.getTransactionId());
        verify(rechargeRepository, times(2)).save(any(Recharge.class));
    }

    @Test
    void testProcessRecharge_PlanNotFound() {
        // Mock OperatorClient returning null
        when(operatorClient.getPlanById(101L)).thenReturn(null);

        // Execute & Verify
        assertThrows(RuntimeException.class, () -> rechargeService.processRecharge(requestDto));
    }

    @Test
    void testGetRechargeById_Success() {
        Recharge recharge = Recharge.builder().id(1L).build();
        when(rechargeRepository.findById(1L)).thenReturn(Optional.of(recharge));

        Recharge result = rechargeService.getRechargeById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void testGetRechargeById_NotFound() {
        when(rechargeRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> rechargeService.getRechargeById(1L));
    }
}
