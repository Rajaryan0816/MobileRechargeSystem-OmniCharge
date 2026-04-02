package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentCompletedEvent {
    private Long paymentId;
    private Long userId;
    private String userEmail;
    private Long rechargeId;
    private Double amount;
    private String transactionId;
    private String status;
    private String message;
}
