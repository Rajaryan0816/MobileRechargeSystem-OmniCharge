package com.example.demo.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.dto.OperatorRequestDTO;
import com.example.demo.dto.OperatorResponseDTO;
import com.example.demo.exception.ApiResponse;
import com.example.demo.service.OperatorService;

import java.util.List;

@RestController
@RequestMapping("/operator")
@Tag(name = "Operator Management", description = "APIs for managing mobile operators (Jio, Airtel, Vi, BSNL, etc.)")
public class OperatorController {

    @Autowired
    private OperatorService operatorService;

    @Operation(summary = "Get all active operators", description = "Returns all operators where isActive = true. No authentication required.")
    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<OperatorResponseDTO>>> getActiveOperators() {
        List<OperatorResponseDTO> operators = operatorService.getAllActiveOperators();
        return ResponseEntity.ok(ApiResponse.success("Active operators fetched successfully", operators));
    }

    @Operation(summary = "Get all operators (including inactive)", description = "Returns all operators. Requires ADMIN or OPERATOR role.",
               security = @SecurityRequirement(name = "Bearer JWT"))
    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<OperatorResponseDTO>>> getAllOperators() {
        List<OperatorResponseDTO> operators = operatorService.getAllOperators();
        return ResponseEntity.ok(ApiResponse.success("All operators fetched successfully", operators));
    }

    @Operation(summary = "Get operator by ID", description = "Returns a specific operator by its ID. No authentication required.")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<OperatorResponseDTO>> getOperatorById(
            @Parameter(description = "Operator ID") @PathVariable Long id) {
        OperatorResponseDTO operator = operatorService.getOperatorById(id);
        return ResponseEntity.ok(ApiResponse.success("Operator fetched successfully", operator));
    }

    @Operation(summary = "Create a new operator", description = "Creates a new mobile operator. Requires ADMIN or OPERATOR role.",
               security = @SecurityRequirement(name = "Bearer JWT"))
    @PostMapping("/create")
    public ResponseEntity<ApiResponse<OperatorResponseDTO>> createOperator(
            @Valid @RequestBody OperatorRequestDTO requestDTO) {
        OperatorResponseDTO created = operatorService.createOperator(requestDTO);
        return ResponseEntity.ok(ApiResponse.success("Operator created successfully", created));
    }

    @Operation(summary = "Update an existing operator", description = "Updates an operator by ID. Requires ADMIN or OPERATOR role.",
               security = @SecurityRequirement(name = "Bearer JWT"))
    @PutMapping("/update/{id}")
    public ResponseEntity<ApiResponse<OperatorResponseDTO>> updateOperator(
            @Parameter(description = "Operator ID to update") @PathVariable Long id,
            @Valid @RequestBody OperatorRequestDTO requestDTO) {
        OperatorResponseDTO updated = operatorService.updateOperator(id, requestDTO);
        return ResponseEntity.ok(ApiResponse.success("Operator updated successfully", updated));
    }

    @Operation(summary = "Delete an operator", description = "Deletes an operator by ID. Requires ADMIN role.",
               security = @SecurityRequirement(name = "Bearer JWT"))
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteOperator(
            @Parameter(description = "Operator ID to delete") @PathVariable Long id) {
        operatorService.deleteOperator(id);
        return ResponseEntity.ok(ApiResponse.success("Operator deleted successfully"));
    }
}
