package com.hosteldesk.backend;

import com.hosteldesk.backend.dto.*;
import com.hosteldesk.backend.entity.AccountStatus;
import com.hosteldesk.backend.entity.Role;
import com.hosteldesk.backend.entity.User;
import com.hosteldesk.backend.exception.BadRequestException;
import com.hosteldesk.backend.repository.UserRepository;
import com.hosteldesk.backend.service.AuthService;
import com.hosteldesk.backend.service.InstituteService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("test")
public class MultiTenantIsolationTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private InstituteService instituteService;

    @Autowired
    private UserRepository userRepository;

    @Test
    @Transactional
    void testInstituteRegistrationAndTenantIsolation() {
        // 1. Register a new Institute
        RegisterInstituteRequest req = new RegisterInstituteRequest();
        req.setInstituteName("Indian Institute of Tech Bombay");
        req.setInstituteCode("IITB-01");
        req.setAdminName("Dr. Sharma");
        req.setAdminId("ADM-IITB-1");
        req.setAdminEmail("admin@iitb.ac.in");
        req.setPassword("AdminPass#2026");

        LoginResponse regResponse = authService.registerInstitute(req);
        Assertions.assertNotNull(regResponse.getToken());
        Assertions.assertEquals("IITB-01", regResponse.getUser().getInstituteCode());
        Assertions.assertEquals(Role.INSTITUTE_ADMIN, regResponse.getUser().getRole());

        // 2. Attempting to log into NCH-001 with IITB-01 credentials must fail
        LoginRequest crossTenantLogin = new LoginRequest();
        crossTenantLogin.setInstituteCode("NCH-001");
        crossTenantLogin.setEmailOrInstitutionalId("ADM-IITB-1");
        crossTenantLogin.setPassword("AdminPass#2026");

        Assertions.assertThrows(BadRequestException.class, () -> authService.login(crossTenantLogin));

        // 3. Valid login against IITB-01 succeeds
        LoginRequest validLogin = new LoginRequest();
        validLogin.setInstituteCode("IITB-01");
        validLogin.setEmailOrInstitutionalId("ADM-IITB-1");
        validLogin.setPassword("AdminPass#2026");

        LoginResponse successLogin = authService.login(validLogin);
        Assertions.assertNotNull(successLogin.getToken());
    }

    @Test
    @Transactional
    void testPasswordResetAndTemporaryCredentials() {
        // 1. Request password reset for existing student aarav in NCH-001
        ForgotPasswordRequest forgotReq = new ForgotPasswordRequest();
        forgotReq.setInstituteCode("NCH-001");
        forgotReq.setIdentifier("ST-8819");
        forgotReq.setReason("Forgot keycard and password");

        Assertions.assertDoesNotThrow(() -> authService.requestPasswordReset(forgotReq));

        // 2. Admin resets password directly
        User student = userRepository.findByInstitutionalId("ST-8819").orElseThrow();
        CredentialResponse resetRes = instituteService.resetUserPassword(student.getInstitute().getId(), student.getId());

        Assertions.assertNotNull(resetRes.getTemporaryPassword());
        User updatedStudent = userRepository.findByInstitutionalId("ST-8819").orElseThrow();
        Assertions.assertTrue(updatedStudent.getNeedsPasswordChange());

        // 3. Student logs in with temporary password
        LoginRequest tempLogin = new LoginRequest();
        tempLogin.setInstituteCode("NCH-001");
        tempLogin.setEmailOrInstitutionalId("ST-8819");
        tempLogin.setPassword(resetRes.getTemporaryPassword());

        LoginResponse tempLoginRes = authService.login(tempLogin);
        Assertions.assertNotNull(tempLoginRes.getToken());
        Assertions.assertTrue(tempLoginRes.getUser().getNeedsPasswordChange());

        // 4. Student changes password to permanent password
        ChangePasswordRequest changeReq = new ChangePasswordRequest(resetRes.getTemporaryPassword(), "NewPermanentPass#123");
        authService.changePassword(student.getId(), changeReq);

        User updated = userRepository.findById(student.getId()).orElseThrow();
        Assertions.assertFalse(updated.getNeedsPasswordChange());
    }
}
