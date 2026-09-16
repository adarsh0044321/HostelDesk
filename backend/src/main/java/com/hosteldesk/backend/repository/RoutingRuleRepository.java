package com.hosteldesk.backend.repository;

import com.hosteldesk.backend.entity.RoutingRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoutingRuleRepository extends JpaRepository<RoutingRule, Long> {
    Optional<RoutingRule> findByCategory(String category);
    Optional<RoutingRule> findByCategoryAndActiveTrue(String category);
    Optional<RoutingRule> findByInstituteIdAndCategoryAndActiveTrue(Long instituteId, String category);
    List<RoutingRule> findByInstituteId(Long instituteId);
}
