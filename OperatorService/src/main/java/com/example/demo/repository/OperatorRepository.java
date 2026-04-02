package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.model.Operator;

import java.util.List;
import java.util.Optional;

@Repository
public interface OperatorRepository extends JpaRepository<Operator, Long> {
    Optional<Operator> findByOperatorCode(String operatorCode);
    List<Operator> findByIsActive(Boolean isActive);
    boolean existsByOperatorCode(String operatorCode);
}
