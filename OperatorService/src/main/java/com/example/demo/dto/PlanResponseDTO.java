package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlanResponseDTO {

    private Long id;
    private Long operatorId;
    private String planName;
    private Double amount;
    private String validity;
    private String data;
    private String voice;
    private String sms;
    private String description;
    private String category;
    private java.util.List<String> tags;
}
