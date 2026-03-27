package com.example.demo.dto;

import com.example.demo.enums.UserType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import io.swagger.v3.oas.annotations.media.Schema;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RechargeRequestDto {

    @NotBlank(message = "Mobile number is required")
    @Pattern(
	        regexp = "^[6-9]\\d{9}$",
	        message = "Phone number must be 10 digits and start with 6-9"
	    )
    private String mobileNumber;

    @NotNull(message = "Operator ID is required")
    private Long operatorId;

    @NotNull(message = "Plan ID is required")
    private Long planId;

    @Schema(hidden = true)
    private UserType userType;

    @Schema(hidden = true)
    private Long userId; // Required if REGISTERED user

    private String userEmail;
}
