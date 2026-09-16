package com.hosteldesk.backend;

import com.hosteldesk.backend.dto.AiInferenceResponse;
import com.hosteldesk.backend.service.AiIntegrationService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class AiFallbackTest {

    @Autowired
    private AiIntegrationService aiIntegrationService;

    @Test
    void testPlumbingLeakKeywordFallback() {
        AiInferenceResponse res = aiIntegrationService.executeDeterministicFallback(
                "Bathroom pipe burst", "Water is leaking from the sink tap onto the floor", "PLUMBING"
        );

        Assertions.assertTrue(res.getIsFallback());
        Assertions.assertEquals("PLUMBING", res.getCategory());
        Assertions.assertEquals("PLUMBING", res.getRecommendedDepartment());
    }

    @Test
    void testElectricalSparkHazardFallback() {
        AiInferenceResponse res = aiIntegrationService.executeDeterministicFallback(
                "Wall socket sparking", "Smoke and sparks coming out of the study socket", "ELECTRICAL"
        );

        Assertions.assertTrue(res.getIsFallback());
        Assertions.assertEquals("ELECTRICAL", res.getCategory());
        Assertions.assertEquals("P1_URGENT", res.getPriority());
        Assertions.assertNotNull(res.getSafetyHazardNote());
    }

    @Test
    void testWifiNetworkFallback() {
        AiInferenceResponse res = aiIntegrationService.executeDeterministicFallback(
                "No wifi in room", "Campus router is blinking red and LAN has no internet", "INTERNET"
        );

        Assertions.assertTrue(res.getIsFallback());
        Assertions.assertEquals("INTERNET", res.getCategory());
    }
}
