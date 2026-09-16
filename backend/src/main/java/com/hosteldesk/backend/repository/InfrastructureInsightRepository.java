package com.hosteldesk.backend.repository;

import com.hosteldesk.backend.entity.InfrastructureInsight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InfrastructureInsightRepository extends JpaRepository<InfrastructureInsight, Long> {
    List<InfrastructureInsight> findByOrderByCreatedAtDesc();
    List<InfrastructureInsight> findByHostelIdOrderByCreatedAtDesc(Long hostelId);
}
