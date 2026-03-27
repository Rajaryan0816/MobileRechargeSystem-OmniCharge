package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;



@Data
@NoArgsConstructor
@AllArgsConstructor
public class RechargeDto {
    private Long id;
    private String mobileNumber;
    private Long operatorId;
    private Long planId;
    private Double amount;
    private String userType;
    private Long userId;
}
