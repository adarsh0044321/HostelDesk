package com.hosteldesk.backend;

import com.hosteldesk.backend.dto.LoginRequest;
import com.hosteldesk.backend.dto.LoginResponse;
import com.hosteldesk.backend.entity.Role;
import com.hosteldesk.backend.exception.BadRequestException;
import com.hosteldesk.backend.exception.ForbiddenException;
import com.hosteldesk.backend.service.AuthService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class AuthServiceTest {

    @Autowired
    private AuthService authService;

    @Test
    void testStudentLoginSuccess() {
        LoginRequest req = new LoginRequest("aarav@campus.edu", "student123", "STUDENT");
        LoginResponse res = authService.login(req);

        Assertions.assertNotNull(res.getToken());
        Assertions.assertEquals(Role.STUDENT, res.getUser().getRole());
        Assertions.assertEquals("ST-8819", res.getUser().getInstitutionalId());
    }

    @Test
    void testStudentLoginBlockedFromAdminApp() {
        LoginRequest req = new LoginRequest("aarav@campus.edu", "student123", "ADMIN");
        Assertions.assertThrows(ForbiddenException.class, () -> authService.login(req));
    }

    @Test
    void testWardenLoginSuccess() {
        LoginRequest req = new LoginRequest("warden.sharma@campus.edu", "warden123", "ADMIN");
        LoginResponse res = authService.login(req);

        Assertions.assertNotNull(res.getToken());
        Assertions.assertEquals(Role.WARDEN, res.getUser().getRole());
    }

    @Test
    void testInvalidPasswordFails() {
        LoginRequest req = new LoginRequest("aarav@campus.edu", "wrongPassword!", "STUDENT");
        Assertions.assertThrows(Exception.class, () -> authService.login(req));
    }

    @Test
    void testInstitutePublicLookup() {
        var info = authService.getInstitutePublicInfo("NCH-001");
        Assertions.assertNotNull(info);
        Assertions.assertEquals("NCH-001", info.getInstituteCode());
        Assertions.assertEquals("North Campus Housing Institute", info.getInstituteName());
        Assertions.assertEquals("North Campus", info.getCampusName());
        Assertions.assertNotNull(info.getContactNumber());
    }
}

