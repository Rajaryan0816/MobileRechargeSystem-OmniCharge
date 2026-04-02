package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RechargeHistoryDto {
    private Long id;
    private String mobileNumber;
    private Long operatorId;
    private Long planId;
    private BigDecimal amount;
    private String userType;
    private String status;
    private String transactionId;
    private LocalDateTime createdAt;
}
