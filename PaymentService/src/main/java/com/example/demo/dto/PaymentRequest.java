package com.example.demo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentRequest {

    @Schema(hidden = true)
    private Long userId;

    @Schema(hidden = true)
    private String userEmail; // For guest users

    @NotNull(message = "Recharge ID is required")
    private Long rechargeId;

    @Schema(hidden = true)
    private Double amount;

    @NotNull(message = "Payment Method is required")
    private String paymentMethod;
}
