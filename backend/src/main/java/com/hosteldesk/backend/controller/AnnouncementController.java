package com.hosteldesk.backend.controller;

import com.hosteldesk.backend.dto.AnnouncementDto;
import com.hosteldesk.backend.dto.CreateAnnouncementRequest;
import com.hosteldesk.backend.entity.User;
import com.hosteldesk.backend.exception.ResourceNotFoundException;
import com.hosteldesk.backend.repository.UserRepository;
import com.hosteldesk.backend.security.UserPrincipal;
import com.hosteldesk.backend.service.AnnouncementService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class AnnouncementController {

    private final AnnouncementService announcementService;
    private final UserRepository userRepository;

    public AnnouncementController(AnnouncementService announcementService, UserRepository userRepository) {
        this.announcementService = announcementService;
        this.userRepository = userRepository;
    }

    @GetMapping("/student/announcements")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<List<AnnouncementDto>> getStudentAnnouncements(@AuthenticationPrincipal UserPrincipal principal) {
        User student = userRepository.findById(principal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));

        Long instituteId = student.getInstitute() != null ? student.getInstitute().getId() : null;
        Long hostelId = student.getHostel() != null ? student.getHostel().getId() : null;

        return ResponseEntity.ok(announcementService.getStudentAnnouncements(instituteId, hostelId));
    }

    @GetMapping({"/admin/announcements", "/institute/announcements"})
    @PreAuthorize("hasAnyRole('WARDEN', 'ADMIN', 'INSTITUTE_ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<List<AnnouncementDto>> getAdminAnnouncements(@AuthenticationPrincipal UserPrincipal principal) {
        Long instituteId = principal.getInstituteId();
        return ResponseEntity.ok(announcementService.getInstituteAnnouncements(instituteId));
    }

    @PostMapping("/institute/announcements")
    @PreAuthorize("hasAnyRole('INSTITUTE_ADMIN', 'ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<AnnouncementDto> createInstituteAnnouncement(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody CreateAnnouncementRequest request) {
        User author = userRepository.findById(principal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Author user not found"));

        Long instituteId = principal.getInstituteId();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(announcementService.createAnnouncement(instituteId, author, request));
    }

    @PostMapping("/admin/announcements")
    @PreAuthorize("hasAnyRole('WARDEN', 'ADMIN', 'INSTITUTE_ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<AnnouncementDto> createWardenAnnouncement(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody CreateAnnouncementRequest request) {
        User author = userRepository.findById(principal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Author user not found"));

        Long instituteId = principal.getInstituteId();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(announcementService.createAnnouncement(instituteId, author, request));
    }

    @PutMapping({"/admin/announcements/{id}", "/announcements/{id}"})
    @PreAuthorize("hasAnyRole('WARDEN', 'ADMIN', 'INSTITUTE_ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<AnnouncementDto> updateAnnouncement(
            @PathVariable("id") Long id,
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody CreateAnnouncementRequest request) {
        return ResponseEntity.ok(announcementService.updateAnnouncement(id, request, principal));
    }

    @DeleteMapping({"/admin/announcements/{id}", "/announcements/{id}"})
    @PreAuthorize("hasAnyRole('WARDEN', 'ADMIN', 'INSTITUTE_ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<Void> deleteAnnouncement(
            @PathVariable("id") Long id,
            @AuthenticationPrincipal UserPrincipal principal) {
        announcementService.deleteAnnouncement(id, principal);
        return ResponseEntity.noContent().build();
    }
}

