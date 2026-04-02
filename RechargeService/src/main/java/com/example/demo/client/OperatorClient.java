package com.example.demo.client;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.OperatorResponseDto;
import com.example.demo.dto.PlanResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "OPERATORSERVICE")
public interface OperatorClient {

    @GetMapping("/operator/active")
    ApiResponse<List<OperatorResponseDto>> getAllOperators();  // ✅ wrapped response

    @GetMapping("/operator/plans/operator/{operatorId}")
    ApiResponse<List<PlanResponseDto>> getPlansForOperator(@PathVariable("operatorId") Long operatorId);

    @GetMapping("/operator/plans/{id}")
    ApiResponse<PlanResponseDto> getPlanById(@PathVariable("id") Long id);
}