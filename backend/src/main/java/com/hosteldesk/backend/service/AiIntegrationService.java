package com.hosteldesk.backend.service;

import com.hosteldesk.backend.config.AppProperties;
import com.hosteldesk.backend.dto.AiInferenceRequest;
import com.hosteldesk.backend.dto.AiInferenceResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;

@Service
public class AiIntegrationService {
    private static final Logger log = LoggerFactory.getLogger(AiIntegrationService.class);

    private final RestTemplate restTemplate;
    private final AppProperties appProperties;

    public AiIntegrationService(RestTemplate restTemplate, AppProperties appProperties) {
        this.restTemplate = restTemplate;
        this.appProperties = appProperties;
    }

    public AiInferenceResponse analyzeIssue(String title, String description, String category, String blockName, String roomNumber) {
        String endpoint = appProperties.getAiService().getUrl() + "/ai/analyze-issue";
        AiInferenceRequest request = new AiInferenceRequest(title, description, category, blockName, roomNumber);

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<AiInferenceRequest> entity = new HttpEntity<>(request, headers);

            log.info("Sending issue analysis request to Python AI service at: {}", endpoint);
            ResponseEntity<AiInferenceResponse> response = restTemplate.exchange(
                    endpoint, HttpMethod.POST, entity, AiInferenceResponse.class
            );

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                log.info("Received AI response: category={}, priority={}, confidence={}",
                        response.getBody().getCategory(), response.getBody().getPriority(), response.getBody().getConfidence());
                return response.getBody();
            }
        } catch (Exception ex) {
            log.warn("Python AI service unreachable or error at {}: {}. Triggering deterministic rule fallback.",
                    endpoint, ex.getMessage());
        }

        return executeDeterministicFallback(title, description, category);
    }

    public AiInferenceResponse executeDeterministicFallback(String title, String description, String fallbackCategory) {
        String combined = (title + " " + description).toLowerCase();

        String detectedCategory = "GENERAL";
        String detectedPriority = "P3_MEDIUM";
        String recommendedDepartment = "GENERAL";
        String safetyHazard = null;
        BigDecimal confidence = new BigDecimal("0.750");

        if (combined.contains("spark") || combined.contains("shock") || combined.contains("smoke") || combined.contains("fire")) {
            detectedCategory = "ELECTRICAL";
            detectedPriority = "P1_URGENT";
            recommendedDepartment = "ELECTRICAL";
            safetyHazard = "Potential electrical short circuit or spark hazard detected";
            confidence = new BigDecimal("0.920");
        } else if (combined.contains("leak") || combined.contains("pipe") || combined.contains("water") ||
                   combined.contains("tap") || combined.contains("flush") || combined.contains("drain")) {
            detectedCategory = "PLUMBING";
            detectedPriority = combined.contains("heavily") || combined.contains("burst") || combined.contains("flood")
                    ? "P1_URGENT" : "P2_HIGH";
            recommendedDepartment = "PLUMBING";
            if (combined.contains("ceiling") || combined.contains("wire") || combined.contains("light")) {
                safetyHazard = "Water leakage near ceiling light or electrical fixture";
            }
            confidence = new BigDecimal("0.880");
        } else if (combined.contains("light") || combined.contains("fan") || combined.contains("switch") ||
                   combined.contains("socket") || combined.contains("power") || combined.contains("ac")) {
            detectedCategory = "ELECTRICAL";
            detectedPriority = "P2_HIGH";
            recommendedDepartment = "ELECTRICAL";
            confidence = new BigDecimal("0.850");
        } else if (combined.contains("wifi") || combined.contains("internet") || combined.contains("lan") ||
                   combined.contains("network") || combined.contains("router")) {
            detectedCategory = "INTERNET";
            detectedPriority = "P3_MEDIUM";
            recommendedDepartment = "INTERNET";
            confidence = new BigDecimal("0.890");
        } else if (combined.contains("door") || combined.contains("lock") || combined.contains("bed") ||
                   combined.contains("window") || combined.contains("cupboard") || combined.contains("chair") || combined.contains("table")) {
            detectedCategory = "CARPENTRY";
            detectedPriority = combined.contains("lock") ? "P2_HIGH" : "P3_MEDIUM";
            recommendedDepartment = "CARPENTRY";
            confidence = new BigDecimal("0.860");
        } else if (combined.contains("clean") || combined.contains("trash") || combined.contains("garbage") ||
                   combined.contains("dust") || combined.contains("pest") || combined.contains("insect")) {
            detectedCategory = "CLEANING";
            detectedPriority = "P3_MEDIUM";
            recommendedDepartment = "CLEANING";
            confidence = new BigDecimal("0.840");
        } else if (combined.contains("wall") || combined.contains("plaster") || combined.contains("crack") ||
                   combined.contains("damp") || combined.contains("seepage")) {
            detectedCategory = "CIVIL";
            detectedPriority = "P3_MEDIUM";
            recommendedDepartment = "CIVIL";
            confidence = new BigDecimal("0.820");
        } else if (fallbackCategory != null && !fallbackCategory.trim().isEmpty()) {
            detectedCategory = fallbackCategory.toUpperCase();
            recommendedDepartment = detectedCategory;
        }

        String summary = String.format("HostelDesk Fallback: %s detected from resident report.", title);
        return new AiInferenceResponse(summary, detectedCategory, detectedPriority, recommendedDepartment, safetyHazard, confidence, true);
    }
}
