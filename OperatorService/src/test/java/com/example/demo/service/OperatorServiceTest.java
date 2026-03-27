package com.example.demo.service;

import com.example.demo.dto.OperatorRequestDTO;
import com.example.demo.dto.OperatorResponseDTO;
import com.example.demo.model.Operator;
import com.example.demo.repository.OperatorRepository;

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
public class OperatorServiceTest {

    @Mock
    private OperatorRepository operatorRepository;

    @InjectMocks
    private OperatorService operatorService;

    private Operator activeOperator;
    private Operator inactiveOperator;
    private OperatorRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        activeOperator = new Operator(1L, "AIRTEL", "Airtel", "http://logo.url", true, null, null);
        inactiveOperator = new Operator(2L, "BSNL", "BSNL", null, false, null, null);

        requestDTO = new OperatorRequestDTO("JIO", "Jio Telecom", "http://jio-logo.url", true);
    }

    // ──────────────────────────────────────────────
    // getAllActiveOperators
    // ──────────────────────────────────────────────

    @Test
    void testGetAllActiveOperators_ReturnsOnlyActive() {
        when(operatorRepository.findByIsActive(true)).thenReturn(List.of(activeOperator));

        List<OperatorResponseDTO> result = operatorService.getAllActiveOperators();

        assertEquals(1, result.size());
        assertEquals("AIRTEL", result.get(0).getOperatorCode());
        verify(operatorRepository, times(1)).findByIsActive(true);
    }

    // ──────────────────────────────────────────────
    // getAllOperators
    // ──────────────────────────────────────────────

    @Test
    void testGetAllOperators_ReturnsAll() {
        when(operatorRepository.findAll()).thenReturn(List.of(activeOperator, inactiveOperator));

        List<OperatorResponseDTO> result = operatorService.getAllOperators();

        assertEquals(2, result.size());
        verify(operatorRepository, times(1)).findAll();
    }

    // ──────────────────────────────────────────────
    // getOperatorById — found
    // ──────────────────────────────────────────────

    @Test
    void testGetOperatorById_Found() {
        when(operatorRepository.findById(1L)).thenReturn(Optional.of(activeOperator));

        OperatorResponseDTO result = operatorService.getOperatorById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Airtel", result.getOperatorName());
    }

    // ──────────────────────────────────────────────
    // getOperatorById — not found
    // ──────────────────────────────────────────────

    @Test
    void testGetOperatorById_NotFound_ThrowsException() {
        when(operatorRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> operatorService.getOperatorById(99L));
    }

    // ──────────────────────────────────────────────
    // createOperator — success
    // ──────────────────────────────────────────────

    @Test
    void testCreateOperator_Success() {
        when(operatorRepository.existsByOperatorCode("JIO")).thenReturn(false);

        Operator savedOperator = new Operator(3L, "JIO", "Jio Telecom", "http://jio-logo.url", true, null, null);
        when(operatorRepository.save(any(Operator.class))).thenReturn(savedOperator);

        OperatorResponseDTO result = operatorService.createOperator(requestDTO);

        assertNotNull(result);
        assertEquals("JIO", result.getOperatorCode());
        assertEquals("Jio Telecom", result.getOperatorName());
        verify(operatorRepository, times(1)).save(any(Operator.class));
    }

    // ──────────────────────────────────────────────
    // createOperator — duplicate code
    // ──────────────────────────────────────────────

    @Test
    void testCreateOperator_DuplicateCode_ThrowsException() {
        when(operatorRepository.existsByOperatorCode("JIO")).thenReturn(true);

        assertThrows(RuntimeException.class, () -> operatorService.createOperator(requestDTO));
        verify(operatorRepository, never()).save(any());
    }

    // ──────────────────────────────────────────────
    // updateOperator — success
    // ──────────────────────────────────────────────

    @Test
    void testUpdateOperator_Success() {
        when(operatorRepository.findById(1L)).thenReturn(Optional.of(activeOperator));

        OperatorRequestDTO updateDTO = new OperatorRequestDTO("AIRTEL", "Airtel Updated", "http://new-logo.url", true);
        Operator updatedOperator = new Operator(1L, "AIRTEL", "Airtel Updated", "http://new-logo.url", true, null, null);
        when(operatorRepository.save(any(Operator.class))).thenReturn(updatedOperator);

        OperatorResponseDTO result = operatorService.updateOperator(1L, updateDTO);

        assertNotNull(result);
        assertEquals("Airtel Updated", result.getOperatorName());
    }

    // ──────────────────────────────────────────────
    // deleteOperator — success
    // ──────────────────────────────────────────────

    @Test
    void testDeleteOperator_Success() {
        when(operatorRepository.existsById(1L)).thenReturn(true);
        doNothing().when(operatorRepository).deleteById(1L);

        assertDoesNotThrow(() -> operatorService.deleteOperator(1L));
        verify(operatorRepository, times(1)).deleteById(1L);
    }

    // ──────────────────────────────────────────────
    // deleteOperator — not found
    // ──────────────────────────────────────────────

    @Test
    void testDeleteOperator_NotFound_ThrowsException() {
        when(operatorRepository.existsById(99L)).thenReturn(false);

        assertThrows(RuntimeException.class, () -> operatorService.deleteOperator(99L));
        verify(operatorRepository, never()).deleteById(any());
    }
}
