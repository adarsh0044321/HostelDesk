package com.hosteldesk.backend.repository;

import com.hosteldesk.backend.entity.IssueAiAnalysis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IssueAiAnalysisRepository extends JpaRepository<IssueAiAnalysis, Long> {
    Optional<IssueAiAnalysis> findByIssueId(Long issueId);
}
