package com.example.demo.service;

import com.example.demo.dto.PlanRequestDTO;
import com.example.demo.dto.PlanResponseDTO;
import com.example.demo.model.Category;
import com.example.demo.model.Plan;
import com.example.demo.repository.CategoryRepository;
import com.example.demo.repository.OperatorRepository;
import com.example.demo.repository.PlanRepository;
import com.example.demo.repository.TagRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PlanServiceTest {

    @Mock
    private PlanRepository planRepository;

    @Mock
    private OperatorRepository operatorRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private TagRepository tagRepository;

    @InjectMocks
    private PlanService planService;

    private Plan activePlan;
    private Category category;

    @BeforeEach
    void setUp() {
        category = new Category(1L, "DATA");

        activePlan = new Plan();
        activePlan.setId(1L);
        activePlan.setOperatorId(1L);
        activePlan.setPlanName("999 Plan");
        activePlan.setAmount(999.0);
        activePlan.setValidity("84 days");
        activePlan.setData("2GB/day");
        activePlan.setIsActive(true);
        activePlan.setCategory(category);
        activePlan.setTags(List.of());
    }

    // ──────────────────────────────────────────────
    // getPlanById — found
    // ──────────────────────────────────────────────

    @Test
    void testGetPlanById_Found() {
        when(planRepository.findById(1L)).thenReturn(Optional.of(activePlan));

        PlanResponseDTO result = planService.getPlanById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("999 Plan", result.getPlanName());
        assertEquals(999.0, result.getAmount());
        assertEquals("DATA", result.getCategory());
    }

    // ──────────────────────────────────────────────
    // getPlanById — not found
    // ──────────────────────────────────────────────

    @Test
    void testGetPlanById_NotFound_ThrowsException() {
        when(planRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> planService.getPlanById(99L));
    }

    // ──────────────────────────────────────────────
    // getAllActivePlans
    // ──────────────────────────────────────────────

    @Test
    void testGetAllActivePlans_ReturnsActiveOnly() {
        when(planRepository.findByIsActive(true)).thenReturn(List.of(activePlan));

        List<PlanResponseDTO> result = planService.getAllActivePlans();

        assertEquals(1, result.size());
        assertEquals("999 Plan", result.get(0).getPlanName());
    }

    // ──────────────────────────────────────────────
    // getPlansByOperator — no filters
    // ──────────────────────────────────────────────

    @Test
    void testGetPlansByOperator_NoFilter() {
        when(planRepository.findByOperatorIdAndIsActive(1L, true)).thenReturn(List.of(activePlan));

        List<PlanResponseDTO> result = planService.getPlansByOperator(1L, null, null);

        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getOperatorId());
    }

    // ──────────────────────────────────────────────
    // createPlan — success
    // ──────────────────────────────────────────────

    @Test
    void testCreatePlan_Success() {
        PlanRequestDTO dto = new PlanRequestDTO(
                1L, "799 Plan", 799.0,
                "56 days", "1.5GB/day", "100 mins", "100 SMS",
                "Popular plan", "DATA", List.of("POPULAR"), true
        );

        when(operatorRepository.existsById(1L)).thenReturn(true);
        when(categoryRepository.findByName("DATA"))
                .thenReturn(Optional.of(category));
        when(tagRepository.findByName("POPULAR")).thenReturn(Optional.empty());
        when(tagRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Plan savedPlan = new Plan();
        savedPlan.setId(2L);
        savedPlan.setOperatorId(1L);
        savedPlan.setPlanName("799 Plan");
        savedPlan.setAmount(799.0);
        savedPlan.setIsActive(true);
        savedPlan.setCategory(category);
        savedPlan.setTags(List.of());

        when(planRepository.save(any(Plan.class))).thenReturn(savedPlan);

        PlanResponseDTO result = planService.createPlan(dto);

        assertNotNull(result);
        assertEquals("799 Plan", result.getPlanName());
        assertEquals(799.0, result.getAmount());
        verify(planRepository, times(1)).save(any(Plan.class));
    }

    // ──────────────────────────────────────────────
    // createPlan — operator not found
    // ──────────────────────────────────────────────

    @Test
    void testCreatePlan_OperatorNotFound_ThrowsException() {
        PlanRequestDTO dto = new PlanRequestDTO(
                99L, "Test Plan", 299.0,
                "28 days", "1GB/day", null, null,
                null, "DATA", List.of(), true
        );
        when(operatorRepository.existsById(99L)).thenReturn(false);

        assertThrows(RuntimeException.class, () -> planService.createPlan(dto));
        verify(planRepository, never()).save(any());
    }

    // ──────────────────────────────────────────────
    // deletePlan — success
    // ──────────────────────────────────────────────

    @Test
    void testDeletePlan_Success() {
        when(planRepository.existsById(1L)).thenReturn(true);
        doNothing().when(planRepository).deleteById(1L);

        assertDoesNotThrow(() -> planService.deletePlan(1L));
        verify(planRepository, times(1)).deleteById(1L);
    }

    // ──────────────────────────────────────────────
    // deletePlan — not found
    // ──────────────────────────────────────────────

    @Test
    void testDeletePlan_NotFound_ThrowsException() {
        when(planRepository.existsById(99L)).thenReturn(false);

        assertThrows(RuntimeException.class, () -> planService.deletePlan(99L));
        verify(planRepository, never()).deleteById(any());
    }
}
