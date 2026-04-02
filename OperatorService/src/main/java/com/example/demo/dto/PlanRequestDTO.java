package com.example.demo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlanRequestDTO {

    @NotNull(message = "Operator ID is required")
    private Long operatorId;

    @NotBlank(message = "Plan name is required")
    private String planName;

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    private Double amount;

    private String validity;
    private String data;
    private String voice;
    private String sms;
    private String description;

    @NotBlank(message = "Category is required")
    private String category;

    @NotNull(message = "Tags list cannot be null")
    private java.util.List<String> tags;

    private Boolean isActive = true;
}
