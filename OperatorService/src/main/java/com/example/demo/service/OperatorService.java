package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.dto.OperatorRequestDTO;
import com.example.demo.dto.OperatorResponseDTO;
import com.example.demo.model.Operator;
import com.example.demo.repository.OperatorRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class OperatorService {

    @Autowired
    private OperatorRepository operatorRepository;

    // ──────────────────────────────────────────────
    // Mapper helpers
    // ──────────────────────────────────────────────

    private OperatorResponseDTO toDTO(Operator operator) {
        return new OperatorResponseDTO(
                operator.getId(),
                operator.getOperatorCode(),
                operator.getOperatorName()
        );
    }

    private Operator toEntity(OperatorRequestDTO dto) {
        Operator operator = new Operator();
        operator.setOperatorCode(dto.getOperatorCode());
        operator.setOperatorName(dto.getOperatorName());
        operator.setLogoUrl(dto.getLogoUrl());
        operator.setIsActive(dto.getIsActive() != null ? dto.getIsActive() : true);
        return operator;
    }

    // ──────────────────────────────────────────────
    // Public service methods
    // ──────────────────────────────────────────────

    public List<OperatorResponseDTO> getAllActiveOperators() {
        return operatorRepository.findByIsActive(true)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<OperatorResponseDTO> getAllOperators() {
        return operatorRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public OperatorResponseDTO getOperatorById(Long id) {
        Operator operator = operatorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Operator not found with id: " + id));
        return toDTO(operator);
    }

    public OperatorResponseDTO createOperator(OperatorRequestDTO dto) {
        if (operatorRepository.existsByOperatorCode(dto.getOperatorCode())) {
            throw new RuntimeException("Operator with code " + dto.getOperatorCode() + " already exists");
        }
        Operator operator = toEntity(dto);
        operator.setIsActive(true);
        return toDTO(operatorRepository.save(operator));
    }

    public OperatorResponseDTO updateOperator(Long id, OperatorRequestDTO dto) {
        Operator operator = operatorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Operator not found with id: " + id));
        operator.setOperatorName(dto.getOperatorName());
        operator.setLogoUrl(dto.getLogoUrl());
        if (dto.getIsActive() != null) {
            operator.setIsActive(dto.getIsActive());
        }
        return toDTO(operatorRepository.save(operator));
    }

    public void deleteOperator(Long id) {
        if (!operatorRepository.existsById(id)) {
            throw new RuntimeException("Operator not found with id: " + id);
        }
        operatorRepository.deleteById(id);
    }
}
