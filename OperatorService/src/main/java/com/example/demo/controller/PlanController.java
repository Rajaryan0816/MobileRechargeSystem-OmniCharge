package com.example.demo.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.dto.PlanRequestDTO;
import com.example.demo.dto.PlanResponseDTO;
import com.example.demo.exception.ApiResponse;
import com.example.demo.service.PlanService;

import java.util.List;

@RestController
@RequestMapping("/operator/plans")
public class PlanController {

    @Autowired
    private PlanService planService;
    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<PlanResponseDTO>>> getActivePlans() {
        List<PlanResponseDTO> plans = planService.getFilteredPlans(null, null);
        return ResponseEntity.ok(ApiResponse.success("Active plans fetched successfully", plans));
    }
    @GetMapping("/categories")
    public ResponseEntity<ApiResponse<List<String>>> getAllCategories() {
        List<String> categories = planService.getAllCategories();
        return ResponseEntity.ok(ApiResponse.success("Categories fetched successfully", categories));
    }

    @GetMapping("/tags")
    public ResponseEntity<ApiResponse<List<String>>> getAllTags() {
        List<String> tags = planService.getAllTags();
        return ResponseEntity.ok(ApiResponse.success("Tags fetched successfully", tags));
    }
    
    // 🔓 PUBLIC: Global plans explicitly by Category AND Tag
    @GetMapping("/category/{category}/tag/{tag}")
    public ResponseEntity<ApiResponse<List<PlanResponseDTO>>> getPlansByCategoryAndTag(@PathVariable String category, @PathVariable String tag) {
        List<PlanResponseDTO> plans = planService.getFilteredPlans(category, tag);
        return ResponseEntity.ok(ApiResponse.success("Plans fetched for category: " + category + " & tag: " + tag, plans));
    }

    // 🔓 PUBLIC: Get ALL plans by operator (No filters)
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PlanResponseDTO>> getPlanById(@PathVariable Long id) {
        PlanResponseDTO plan = planService.getPlanById(id);
        return ResponseEntity.ok(ApiResponse.success("Plan fetched successfully", plan));
    }

    @GetMapping("/operator/{operatorId}")
    public ResponseEntity<ApiResponse<List<PlanResponseDTO>>> getPlansByOperator(
            @PathVariable Long operatorId) {
        List<PlanResponseDTO> plans = planService.getPlansByOperator(operatorId, null, null);
        return ResponseEntity.ok(ApiResponse.success("Plans fetched for operator " + operatorId, plans));
    }

    // 🔓 PUBLIC: Get Operator plans explicitly by Category
    @GetMapping("/operator/{operatorId}/category/{category}")
    public ResponseEntity<ApiResponse<List<PlanResponseDTO>>> getOperatorPlansByCategory(
            @PathVariable Long operatorId, @PathVariable String category) {
        List<PlanResponseDTO> plans = planService.getPlansByOperator(operatorId, category, null);
        return ResponseEntity.ok(ApiResponse.success("Plans fetched for operator " + operatorId + " and category: " + category, plans));
    }

    // 🔓 PUBLIC: Get Operator plans explicitly by Tag
    @GetMapping("/operator/{operatorId}/tag/{tag}")
    public ResponseEntity<ApiResponse<List<PlanResponseDTO>>> getOperatorPlansByTag(
            @PathVariable Long operatorId, @PathVariable String tag) {
        List<PlanResponseDTO> plans = planService.getPlansByOperator(operatorId, null, tag);
        return ResponseEntity.ok(ApiResponse.success("Plans fetched for operator " + operatorId + " and tag: " + tag, plans));
    }

    // 🔓 PUBLIC: Get Operator plans explicitly by Category AND Tag
    @GetMapping("/operator/{operatorId}/category/{category}/tag/{tag}")
    public ResponseEntity<ApiResponse<List<PlanResponseDTO>>> getOperatorPlansByCategoryAndTag(
            @PathVariable Long operatorId, @PathVariable String category, @PathVariable String tag) {
        List<PlanResponseDTO> plans = planService.getPlansByOperator(operatorId, category, tag);
        return ResponseEntity.ok(ApiResponse.success("Plans fetched for operator " + operatorId + " bounded by category and tag", plans));
    }

    // 🔒 OPERATOR/ADMIN: Create plan
    @PostMapping("/create")
    public ResponseEntity<ApiResponse<PlanResponseDTO>> createPlan(
            @Valid @RequestBody PlanRequestDTO requestDTO) {
        PlanResponseDTO created = planService.createPlan(requestDTO);
        return ResponseEntity.ok(ApiResponse.success("Plan created successfully", created));
    }

    // 🔒 OPERATOR/ADMIN: Update plan
    @PutMapping("/update/{id}")
    public ResponseEntity<ApiResponse<PlanResponseDTO>> updatePlan(
            @PathVariable Long id,
            @Valid @RequestBody PlanRequestDTO requestDTO) {
        PlanResponseDTO updated = planService.updatePlan(id, requestDTO);
        return ResponseEntity.ok(ApiResponse.success("Plan updated successfully", updated));
    }

    // 🔒 ADMIN ONLY: Delete plan
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ApiResponse<Void>> deletePlan(@PathVariable Long id) {
        planService.deletePlan(id);
        return ResponseEntity.ok(ApiResponse.success("Plan deleted successfully"));
    }
}
