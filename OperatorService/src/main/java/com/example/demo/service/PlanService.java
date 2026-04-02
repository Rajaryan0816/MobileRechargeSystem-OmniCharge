package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.dto.PlanRequestDTO;
import com.example.demo.dto.PlanResponseDTO;
import com.example.demo.model.Category;
import com.example.demo.model.Plan;
import com.example.demo.model.Tag;
import com.example.demo.repository.CategoryRepository;
import com.example.demo.repository.OperatorRepository;
import com.example.demo.repository.PlanRepository;
import com.example.demo.repository.TagRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PlanService {

    @Autowired
    private PlanRepository planRepository;

    @Autowired
    private OperatorRepository operatorRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private TagRepository tagRepository;

    // ──────────────────────────────────────────────
    // Mapper helpers
    // ──────────────────────────────────────────────

    private PlanResponseDTO toDTO(Plan plan) {
        return new PlanResponseDTO(
                plan.getId(),
                plan.getOperatorId(),
                plan.getPlanName(),
                plan.getAmount(),
                plan.getValidity(),
                plan.getData(),
                plan.getVoice(),
                plan.getSms(),
                plan.getDescription(),
                plan.getCategory() != null ? plan.getCategory().getName() : null,
                plan.getTags() != null ? plan.getTags().stream().map(Tag::getName).collect(Collectors.toList()) : null
        );
    }

    private Category resolveCategory(String categoryName) {
        return categoryRepository.findByName(categoryName)
                .orElseGet(() -> categoryRepository.save(new Category(null, categoryName)));
    }

    private List<Tag> resolveTags(List<String> tagNames) {
        if (tagNames == null) return List.of();
        return tagNames.stream().map(name -> 
            tagRepository.findByName(name)
                    .orElseGet(() -> tagRepository.save(new Tag(null, name)))
        ).collect(Collectors.toList());
    }

    public List<String> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(Category::getName)
                .collect(Collectors.toList());
    }

    public List<String> getAllTags() {
        return tagRepository.findAll().stream()
                .map(Tag::getName)
                .collect(Collectors.toList());
    }

    private Plan toEntity(PlanRequestDTO dto) {
        Plan plan = new Plan();
        plan.setOperatorId(dto.getOperatorId());
        plan.setPlanName(dto.getPlanName());
        plan.setAmount(dto.getAmount());
        plan.setValidity(dto.getValidity());
        plan.setData(dto.getData());
        plan.setVoice(dto.getVoice());
        plan.setSms(dto.getSms());
        plan.setDescription(dto.getDescription());
        plan.setIsActive(dto.getIsActive() != null ? dto.getIsActive() : true);
        
        plan.setCategory(resolveCategory(dto.getCategory().toUpperCase()));
        plan.setTags(resolveTags(dto.getTags().stream().map(String::toUpperCase).collect(Collectors.toList())));
        
        return plan;
    }

    // ──────────────────────────────────────────────
    // Public service methods
    // ──────────────────────────────────────────────

    public List<PlanResponseDTO> getAllActivePlans() {
        return planRepository.findByIsActive(true)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
    
    public List<PlanResponseDTO> getFilteredPlans(String category, String tag) {
        List<Plan> plans;
        if (category != null && tag != null) {
            plans = planRepository.findByCategory_NameAndTags_NameAndIsActive(category.toUpperCase(), tag.toUpperCase(), true);
        } else if (category != null) {
            plans = planRepository.findByCategory_NameAndIsActive(category.toUpperCase(), true);
        } else if (tag != null) {
            plans = planRepository.findByTags_NameAndIsActive(tag.toUpperCase(), true);
        } else {
            plans = planRepository.findByIsActive(true);
        }
        return plans.stream().map(this::toDTO).collect(Collectors.toList());
    }

    public List<PlanResponseDTO> getPlansByOperator(Long operatorId, String category, String tag) {
        List<Plan> plans;
        if (category != null && tag != null) {
            plans = planRepository.findByOperatorIdAndCategory_NameAndTags_NameAndIsActive(operatorId, category.toUpperCase(), tag.toUpperCase(), true);
        } else if (category != null) {
            plans = planRepository.findByOperatorIdAndCategory_NameAndIsActive(operatorId, category.toUpperCase(), true);
        } else if (tag != null) {
            plans = planRepository.findByOperatorIdAndTags_NameAndIsActive(operatorId, tag.toUpperCase(), true);
        } else {
            plans = planRepository.findByOperatorIdAndIsActive(operatorId, true);
        }
        return plans.stream().map(this::toDTO).collect(Collectors.toList());
    }

    public List<PlanResponseDTO> getAllPlans() {
        return planRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public PlanResponseDTO getPlanById(Long id) {
        Plan plan = planRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Plan not found with id: " + id));
        return toDTO(plan);
    }

    @Transactional
    public PlanResponseDTO createPlan(PlanRequestDTO dto) {
        if (!operatorRepository.existsById(dto.getOperatorId())) {
            throw new RuntimeException("Operator not found with id: " + dto.getOperatorId());
        }
        Plan plan = toEntity(dto);
        plan.setIsActive(true);
        return toDTO(planRepository.save(plan));
    }

    @Transactional
    public PlanResponseDTO updatePlan(Long id, PlanRequestDTO dto) {
        Plan plan = planRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Plan not found with id: " + id));
        plan.setPlanName(dto.getPlanName());
        plan.setAmount(dto.getAmount());
        plan.setValidity(dto.getValidity());
        plan.setData(dto.getData());
        plan.setVoice(dto.getVoice());
        plan.setSms(dto.getSms());
        plan.setDescription(dto.getDescription());
        
        plan.setCategory(resolveCategory(dto.getCategory().toUpperCase()));
        plan.setTags(resolveTags(dto.getTags().stream().map(String::toUpperCase).collect(Collectors.toList())));

        if (dto.getIsActive() != null) {
            plan.setIsActive(dto.getIsActive());
        }
        return toDTO(planRepository.save(plan));
    }

    public void deletePlan(Long id) {
        if (!planRepository.existsById(id)) {
            throw new RuntimeException("Plan not found with id: " + id);
        }
        planRepository.deleteById(id);
    }
}
