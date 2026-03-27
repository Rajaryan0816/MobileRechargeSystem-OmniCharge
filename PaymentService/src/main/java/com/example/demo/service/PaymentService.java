package com.example.demo.service;

import com.example.demo.dto.PaymentRequest;
import com.example.demo.dto.PaymentResponse;
import com.example.demo.entity.Payment;

import java.util.List;

public interface PaymentService {
    PaymentResponse processPayment(PaymentRequest request);
    List<Payment> getPaymentsByUserId(Long userId);
    List<Payment> getPayments();
}
