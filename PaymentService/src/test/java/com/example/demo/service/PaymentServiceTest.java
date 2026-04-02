package com.example.demo.service;

import com.example.demo.client.RechargeClient;
import com.example.demo.config.RabbitMQConfig;
import com.example.demo.dto.PaymentRequest;
import com.example.demo.dto.PaymentResponse;
import com.example.demo.dto.RechargeDto;
import com.example.demo.entity.Payment;
import com.example.demo.entity.Payment.PaymentStatus;
import com.example.demo.repository.PaymentRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @Mock
    private RechargeClient rechargeClient;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    private PaymentRequest request;
    private RechargeDto rechargeDto;

    @BeforeEach
    void setUp() {
        request = PaymentRequest.builder()
                .rechargeId(1L)
                .userId(10L)
                .userEmail("user@example.com")
                .amount(500.0)
                .paymentMethod("UPI")
                .build();

        rechargeDto = new RechargeDto(1L, "9876543210", 2L, 101L, 500.0, "REGISTERED", 10L);
    }

    // ──────────────────────────────────────────────
    // processPayment — SUCCESS
    // ──────────────────────────────────────────────

    @Test
    void testProcessPayment_Success() {
        when(rechargeClient.getRechargeById(1L)).thenReturn(rechargeDto);

        Payment pendingPayment = Payment.builder()
                .id(1L)
                .userId(10L)
                .rechargeId(1L)
                .amount(500.0)
                .transactionId("TXN00000001")
                .status(PaymentStatus.PENDING)
                .build();

        Payment successPayment = Payment.builder()
                .id(1L)
                .userId(10L)
                .rechargeId(1L)
                .amount(500.0)
                .transactionId("TXN00000001")
                .status(PaymentStatus.SUCCESS)
                .build();

        when(paymentRepository.save(any(Payment.class)))
                .thenReturn(pendingPayment)
                .thenReturn(successPayment);

        PaymentResponse response = paymentService.processPayment(request);

        assertNotNull(response);
        assertEquals(PaymentStatus.SUCCESS, response.getStatus());
        assertNotNull(response.getTransactionId());
        assertEquals("Payment processed successfully", response.getMessage());

        // PENDING save + SUCCESS save = 2 saves
        verify(paymentRepository, times(2)).save(any(Payment.class));
        // RabbitMQ event published on success
        verify(rabbitTemplate, times(1))
                .convertAndSend(eq(RabbitMQConfig.EXCHANGE_PAYMENT),
                        eq(RabbitMQConfig.ROUTING_KEY_PAYMENT_COMPLETED), any(Object.class));
    }

    // ──────────────────────────────────────────────
    // processPayment — FAILED (zero / negative amount)
    // ──────────────────────────────────────────────

    @Test
    void testProcessPayment_ZeroAmount_Fails() {
        request.setAmount(0.0);
        when(rechargeClient.getRechargeById(1L)).thenReturn(rechargeDto);

        Payment pendingPayment = Payment.builder()
                .id(2L)
                .userId(10L)
                .rechargeId(1L)
                .amount(0.0)
                .transactionId("TXN00000002")
                .status(PaymentStatus.PENDING)
                .build();

        Payment failedPayment = Payment.builder()
                .id(2L)
                .userId(10L)
                .rechargeId(1L)
                .amount(0.0)
                .transactionId("TXN00000002")
                .status(PaymentStatus.FAILED)
                .build();

        when(paymentRepository.save(any(Payment.class)))
                .thenReturn(pendingPayment)
                .thenReturn(failedPayment);

        PaymentResponse response = paymentService.processPayment(request);

        assertNotNull(response);
        assertEquals(PaymentStatus.FAILED, response.getStatus());
        // FAILED event is also published so user gets failure notification
        verify(rabbitTemplate, times(1))
                .convertAndSend(eq(RabbitMQConfig.EXCHANGE_PAYMENT),
                        eq(RabbitMQConfig.ROUTING_KEY_PAYMENT_COMPLETED), any(Object.class));
    }

    // ──────────────────────────────────────────────
    // processPayment — Recharge not found
    // ──────────────────────────────────────────────

    @Test
    void testProcessPayment_RechargeNotFound_ThrowsException() {
        when(rechargeClient.getRechargeById(1L)).thenReturn(null);

        assertThrows(RuntimeException.class, () -> paymentService.processPayment(request));
        verify(paymentRepository, never()).save(any());
    }

    // ──────────────────────────────────────────────
    // processPayment — RechargeService unreachable
    // ──────────────────────────────────────────────

    @Test
    void testProcessPayment_RechargeClientThrows_ThrowsException() {
        when(rechargeClient.getRechargeById(1L))
                .thenThrow(new RuntimeException("Connection refused"));

        assertThrows(RuntimeException.class, () -> paymentService.processPayment(request));
        verify(paymentRepository, never()).save(any());
    }

    // ──────────────────────────────────────────────
    // getPaymentsByUserId — Success
    // ──────────────────────────────────────────────

    @Test
    void testGetPaymentsByUserId_ReturnsList() {
        Payment p1 = Payment.builder().id(1L).userId(10L).status(PaymentStatus.SUCCESS).build();
        Payment p2 = Payment.builder().id(2L).userId(10L).status(PaymentStatus.FAILED).build();
        when(paymentRepository.findByUserId(10L)).thenReturn(List.of(p1, p2));

        List<Payment> payments = paymentService.getPaymentsByUserId(10L);

        assertEquals(2, payments.size());
        verify(paymentRepository, times(1)).findByUserId(10L);
    }

    // ──────────────────────────────────────────────
    // getPaymentsByUserId — Empty list
    // ──────────────────────────────────────────────

    @Test
    void testGetPaymentsByUserId_EmptyList() {
        when(paymentRepository.findByUserId(99L)).thenReturn(List.of());

        List<Payment> payments = paymentService.getPaymentsByUserId(99L);

        assertNotNull(payments);
        assertTrue(payments.isEmpty());
    }
}
