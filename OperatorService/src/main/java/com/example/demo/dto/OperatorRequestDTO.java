package com.example.demo.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OperatorRequestDTO {

    @NotBlank(message = "Operator code is required")
    private String operatorCode;

    @NotBlank(message = "Operator name is required")
    private String operatorName;

    private String logoUrl;

    private Boolean isActive = true;
}
