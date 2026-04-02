package com.example.demo.client;

import com.example.demo.dto.PaymentRequestDto;
import com.example.demo.dto.PaymentResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "PAYMENTSERVICE") 
public interface PaymentClient {

    @PostMapping("/api/payments/process")
    PaymentResponseDto processPayment(@RequestBody PaymentRequestDto requestDto);
    
    @PostMapping("/api/payments/guest/process")
    PaymentResponseDto processGuestPayment(@RequestBody PaymentRequestDto requestDto);
}
