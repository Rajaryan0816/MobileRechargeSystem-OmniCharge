package com.example.demo.client;

import com.example.demo.dto.PaymentHistoryDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "PaymentService")
public interface PaymentClient {

    @GetMapping("/api/payments/user/{userId}")
    List<PaymentHistoryDto> getPaymentsByUserId(@PathVariable("userId") Long userId);
}
