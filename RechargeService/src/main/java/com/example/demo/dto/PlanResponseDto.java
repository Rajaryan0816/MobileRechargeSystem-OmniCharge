package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlanResponseDto {
    private Long id;
    private Long operatorId;
    private String planName;
    private Double amount;
    private String validity;
    private String data;
    private String voice;
    private String sms;
    private String description;
}
