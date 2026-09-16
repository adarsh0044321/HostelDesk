package com.hosteldesk.backend.service;

import com.hosteldesk.backend.entity.Department;
import com.hosteldesk.backend.entity.RoutingRule;
import com.hosteldesk.backend.repository.DepartmentRepository;
import com.hosteldesk.backend.repository.RoutingRuleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class RoutingService {
    private final RoutingRuleRepository routingRuleRepository;
    private final DepartmentRepository departmentRepository;

    public RoutingService(RoutingRuleRepository routingRuleRepository, DepartmentRepository departmentRepository) {
        this.routingRuleRepository = routingRuleRepository;
        this.departmentRepository = departmentRepository;
    }

    @Transactional(readOnly = true)
    public Department resolveDepartmentForCategory(String category) {
        if (category == null) category = "GENERAL";

        Optional<RoutingRule> ruleOpt = routingRuleRepository.findByCategoryAndActiveTrue(category.toUpperCase());
        if (ruleOpt.isPresent()) {
            return ruleOpt.get().getDepartment();
        }

        return departmentRepository.findByName(category.toUpperCase())
                .or(() -> departmentRepository.findByName("GENERAL"))
                .orElse(null);
    }
}
