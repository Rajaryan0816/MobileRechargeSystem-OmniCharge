package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.model.Plan;

import java.util.List;

@Repository
public interface PlanRepository extends JpaRepository<Plan, Long> {
    List<Plan> findByOperatorId(Long operatorId);
    List<Plan> findByOperatorIdAndIsActive(Long operatorId, Boolean isActive);
    List<Plan> findByOperatorIdAndCategory_NameAndIsActive(Long operatorId, String categoryName, Boolean isActive);
    List<Plan> findByOperatorIdAndTags_NameAndIsActive(Long operatorId, String tagName, Boolean isActive);
    List<Plan> findByOperatorIdAndCategory_NameAndTags_NameAndIsActive(Long operatorId, String categoryName, String tagName, Boolean isActive);
    List<Plan> findByCategory_NameAndIsActive(String categoryName, Boolean isActive);
    List<Plan> findByTags_NameAndIsActive(String tagName, Boolean isActive);
    List<Plan> findByCategory_NameAndTags_NameAndIsActive(String category, String tag, Boolean isActive);
    List<Plan> findByIsActive(Boolean isActive);
}
