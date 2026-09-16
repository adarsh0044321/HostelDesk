package com.hosteldesk.backend.controller;

import com.hosteldesk.backend.dto.InsightDto;
import com.hosteldesk.backend.repository.InfrastructureInsightRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/analytics")
@PreAuthorize("hasAnyRole('WARDEN', 'ADMIN', 'INSTITUTE_ADMIN', 'SUPER_ADMIN')")
public class AnalyticsController {

    private final InfrastructureInsightRepository insightRepository;

    public AnalyticsController(InfrastructureInsightRepository insightRepository) {
        this.insightRepository = insightRepository;
    }

    @GetMapping("/insights")
    public ResponseEntity<List<InsightDto>> getInsights() {
        return ResponseEntity.ok(insightRepository.findByOrderByCreatedAtDesc()
                .stream().map(InsightDto::fromEntity).collect(Collectors.toList()));
    }
}
