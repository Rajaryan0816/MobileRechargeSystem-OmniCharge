package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentRequestDto {
    private Long rechargeId; // E.g., Recharge ID
    private BigDecimal amount;
    private Long userId;
    private String userEmail;
    private String paymentMethod;
}
