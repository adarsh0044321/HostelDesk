package com.hosteldesk.backend;

import com.hosteldesk.backend.dto.AnnouncementDto;
import com.hosteldesk.backend.dto.CreateAnnouncementRequest;
import com.hosteldesk.backend.entity.Role;
import com.hosteldesk.backend.entity.User;
import com.hosteldesk.backend.exception.ForbiddenException;
import com.hosteldesk.backend.repository.UserRepository;
import com.hosteldesk.backend.security.UserPrincipal;
import com.hosteldesk.backend.service.AnnouncementService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("test")
public class AnnouncementRoleScopingTest {

    @Autowired
    private AnnouncementService announcementService;

    @Autowired
    private UserRepository userRepository;

    @Test
    @Transactional
    void testInstituteCanManageAllAnnouncementsAndWardenCannotModifyInstituteAnnouncements() {
        User admin = userRepository.findByEmail("admin@campus.edu").orElseThrow();
        User warden = userRepository.findByEmail("warden.sharma@campus.edu").orElseThrow();

        UserPrincipal adminPrincipal = UserPrincipal.create(admin);
        UserPrincipal wardenPrincipal = UserPrincipal.create(warden);

        // 1. Institute Admin creates an announcement
        CreateAnnouncementRequest instReq = new CreateAnnouncementRequest(
                "Campus Power Outage", "Power grid maintenance from 2 AM to 5 AM", null, 24, true
        );
        AnnouncementDto instNotice = announcementService.createAnnouncement(admin.getInstitute().getId(), admin, instReq);
        Assertions.assertNotNull(instNotice.getId());

        // 2. Warden creates a hostel notice
        CreateAnnouncementRequest wardenReq = new CreateAnnouncementRequest(
                "Hostel Mess Cleaning", "Mess will open 30 min later tomorrow", warden.getHostel().getId(), 12, false
        );
        AnnouncementDto wardenNotice = announcementService.createAnnouncement(warden.getInstitute().getId(), warden, wardenReq);
        Assertions.assertNotNull(wardenNotice.getId());

        // 3. Institute Admin can update Warden's notice
        CreateAnnouncementRequest adminUpdateReq = new CreateAnnouncementRequest(
                "Hostel Mess Cleaning (Approved)", "Mess will open 45 min later tomorrow", warden.getHostel().getId(), 12, false
        );
        AnnouncementDto updatedByAdmin = announcementService.updateAnnouncement(wardenNotice.getId(), adminUpdateReq, adminPrincipal);
        Assertions.assertEquals("Hostel Mess Cleaning (Approved)", updatedByAdmin.getTitle());

        // 4. Warden CANNOT update Institute announcement -> Must throw ForbiddenException
        CreateAnnouncementRequest wardenAttemptUpdate = new CreateAnnouncementRequest(
                "Hacked Notice", "Warden trying to edit institute notice", null, 24, false
        );
        Assertions.assertThrows(ForbiddenException.class, () -> {
            announcementService.updateAnnouncement(instNotice.getId(), wardenAttemptUpdate, wardenPrincipal);
        });

        // 5. Warden CANNOT delete Institute announcement -> Must throw ForbiddenException
        Assertions.assertThrows(ForbiddenException.class, () -> {
            announcementService.deleteAnnouncement(instNotice.getId(), wardenPrincipal);
        });

        // 6. Institute Admin CAN delete Warden's notice
        Assertions.assertDoesNotThrow(() -> {
            announcementService.deleteAnnouncement(wardenNotice.getId(), adminPrincipal);
        });

        // 7. Institute Admin CAN delete Institute notice
        Assertions.assertDoesNotThrow(() -> {
            announcementService.deleteAnnouncement(instNotice.getId(), adminPrincipal);
        });
    }
}