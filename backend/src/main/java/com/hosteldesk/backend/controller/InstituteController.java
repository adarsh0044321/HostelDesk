package com.hosteldesk.backend.controller;

import com.hosteldesk.backend.dto.*;
import com.hosteldesk.backend.entity.Department;
import com.hosteldesk.backend.entity.Hostel;
import com.hosteldesk.backend.entity.PasswordResetRequest;
import com.hosteldesk.backend.exception.ForbiddenException;
import com.hosteldesk.backend.security.UserPrincipal;
import com.hosteldesk.backend.service.InstituteService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/institute")
@PreAuthorize("hasAnyRole('INSTITUTE_ADMIN', 'ADMIN', 'SUPER_ADMIN', 'WARDEN')")
public class InstituteController {

    private final InstituteService instituteService;

    public InstituteController(InstituteService instituteService) {
        this.instituteService = instituteService;
    }

    private Long requireInstituteId(UserPrincipal principal) {
        if (principal == null || principal.getInstituteId() == null) {
            throw new ForbiddenException("No institution association found in session.");
        }
        return principal.getInstituteId();
    }

    @GetMapping("/dashboard")
    @PreAuthorize("hasAnyRole('INSTITUTE_ADMIN', 'ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<InstituteDashboardDto> getDashboard(@AuthenticationPrincipal UserPrincipal principal) {
        Long instituteId = requireInstituteId(principal);
        return ResponseEntity.ok(instituteService.getDashboard(instituteId));
    }

    @PostMapping("/wardens")
    @PreAuthorize("hasAnyRole('INSTITUTE_ADMIN', 'ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<CredentialResponse> createWarden(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody CreateUserWithTempPasswordRequest request) {
        Long instituteId = requireInstituteId(principal);
        return ResponseEntity.ok(instituteService.createWarden(instituteId, request));
    }

    @GetMapping("/wardens")
    @PreAuthorize("hasAnyRole('INSTITUTE_ADMIN', 'ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<List<UserDto>> getWardens(@AuthenticationPrincipal UserPrincipal principal) {
        Long instituteId = requireInstituteId(principal);
        return ResponseEntity.ok(instituteService.getWardens(instituteId));
    }

    @PostMapping("/students")
    @PreAuthorize("hasAnyRole('INSTITUTE_ADMIN', 'ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<CredentialResponse> createStudent(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody CreateUserWithTempPasswordRequest request) {
        Long instituteId = requireInstituteId(principal);
        return ResponseEntity.ok(instituteService.createStudent(instituteId, request));
    }

    @GetMapping("/students")
    @PreAuthorize("hasAnyRole('INSTITUTE_ADMIN', 'ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<List<UserDto>> getStudents(@AuthenticationPrincipal UserPrincipal principal) {
        Long instituteId = requireInstituteId(principal);
        return ResponseEntity.ok(instituteService.getStudents(instituteId));
    }

    @PostMapping("/staff")
    public ResponseEntity<CredentialResponse> createStaff(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody CreateUserWithTempPasswordRequest request) {
        Long instituteId = requireInstituteId(principal);
        return ResponseEntity.ok(instituteService.createStaff(instituteId, request));
    }

    @GetMapping("/staff")
    public ResponseEntity<List<UserDto>> getStaff(@AuthenticationPrincipal UserPrincipal principal) {
        Long instituteId = requireInstituteId(principal);
        return ResponseEntity.ok(instituteService.getStaff(instituteId));
    }

    @PostMapping("/users/{id}/reset-password")
    @PreAuthorize("hasAnyRole('INSTITUTE_ADMIN', 'ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<CredentialResponse> resetUserPassword(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable("id") Long userId) {
        Long instituteId = requireInstituteId(principal);
        return ResponseEntity.ok(instituteService.resetUserPassword(instituteId, userId));
    }

    @GetMapping("/hostels")
    @PreAuthorize("hasAnyRole('INSTITUTE_ADMIN', 'ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<List<HostelDto>> getHostels(@AuthenticationPrincipal UserPrincipal principal) {
        Long instituteId = requireInstituteId(principal);
        return ResponseEntity.ok(instituteService.getHostels(instituteId));
    }

    @PostMapping("/hostels")
    @PreAuthorize("hasAnyRole('INSTITUTE_ADMIN', 'ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<HostelDto> createHostel(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestBody Hostel hostel) {
        Long instituteId = requireInstituteId(principal);
        return ResponseEntity.ok(instituteService.createHostel(instituteId, hostel));
    }

    @PutMapping("/hostels/{id}/assign-warden")
    @PreAuthorize("hasAnyRole('INSTITUTE_ADMIN', 'ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<HostelDto> assignWarden(
            @PathVariable("id") Long hostelId,
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestBody AssignWardenRequest request) {
        Long instituteId = requireInstituteId(principal);
        return ResponseEntity.ok(instituteService.assignWardenToHostel(instituteId, hostelId, request.getWardenId()));
    }

    @PutMapping("/hostels/{id}")
    @PreAuthorize("hasAnyRole('INSTITUTE_ADMIN', 'ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<HostelDto> updateHostel(
            @PathVariable("id") Long hostelId,
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestBody CreateHostelRequest request) {
        Long instituteId = requireInstituteId(principal);
        return ResponseEntity.ok(instituteService.updateHostel(instituteId, hostelId, request));
    }

    @DeleteMapping("/hostels/{id}")
    @PreAuthorize("hasAnyRole('INSTITUTE_ADMIN', 'ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<Map<String, String>> deleteHostel(
            @PathVariable("id") Long hostelId,
            @AuthenticationPrincipal UserPrincipal principal) {
        Long instituteId = requireInstituteId(principal);
        instituteService.deleteHostel(instituteId, hostelId);
        return ResponseEntity.ok(java.util.Collections.singletonMap("message", "Hostel deleted successfully."));
    }

    @PutMapping("/wardens/my-contact")
    public ResponseEntity<UserDto> updateWardenContact(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestBody UpdateContactRequest request) {
        return ResponseEntity.ok(instituteService.updateWardenContact(principal.getId(), request));
    }

    @GetMapping("/crews")
    @PreAuthorize("hasAnyRole('INSTITUTE_ADMIN', 'ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<List<CrewWorkloadDto>> getCrewWorkloads(@AuthenticationPrincipal UserPrincipal principal) {
        Long instituteId = requireInstituteId(principal);
        return ResponseEntity.ok(instituteService.getCrewWorkloads(instituteId));
    }

    @GetMapping("/departments")
    public ResponseEntity<List<Department>> getDepartments(@AuthenticationPrincipal UserPrincipal principal) {
        Long instituteId = requireInstituteId(principal);
        return ResponseEntity.ok(instituteService.getDepartments(instituteId));
    }

    @PostMapping("/departments")
    @PreAuthorize("hasAnyRole('INSTITUTE_ADMIN', 'ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<Department> createDepartment(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestBody Department department) {
        Long instituteId = requireInstituteId(principal);
        return ResponseEntity.ok(instituteService.createDepartment(instituteId, department));
    }

    @GetMapping("/emergency-contacts")
    public ResponseEntity<EmergencyContactsDto> getEmergencyContacts(@AuthenticationPrincipal UserPrincipal principal) {
        Long instituteId = requireInstituteId(principal);
        return ResponseEntity.ok(instituteService.getEmergencyContacts(instituteId, principal.getHostelId()));
    }

    @PutMapping("/emergency-contacts")
    @PreAuthorize("hasAnyRole('INSTITUTE_ADMIN', 'ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<EmergencyContactsDto> updateEmergencyContacts(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestBody EmergencyContactsDto dto) {
        Long instituteId = requireInstituteId(principal);
        return ResponseEntity.ok(instituteService.updateEmergencyContacts(instituteId, dto));
    }

    @GetMapping("/password-resets")
    @PreAuthorize("hasAnyRole('INSTITUTE_ADMIN', 'ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<List<PasswordResetRequest>> getPasswordResets(@AuthenticationPrincipal UserPrincipal principal) {
        Long instituteId = requireInstituteId(principal);
        return ResponseEntity.ok(instituteService.getPasswordResets(instituteId));
    }

    @PostMapping("/password-resets/{id}/approve")
    @PreAuthorize("hasAnyRole('INSTITUTE_ADMIN', 'ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<CredentialResponse> approvePasswordReset(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable("id") Long requestId) {
        Long instituteId = requireInstituteId(principal);
        return ResponseEntity.ok(instituteService.approvePasswordReset(instituteId, requestId, principal.getId()));
    }

    @PostMapping("/password-resets/{id}/assign")
    @PreAuthorize("hasAnyRole('INSTITUTE_ADMIN', 'ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<PasswordResetRequest> assignPasswordReset(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable("id") Long requestId,
            @RequestBody(required = false) Map<String, String> body) {
        Long instituteId = requireInstituteId(principal);
        String handlerName = body != null && body.containsKey("handlerName") ? body.get("handlerName") : "Institute IT Helpdesk";
        String handlerDepartment = body != null && body.containsKey("handlerDepartment") ? body.get("handlerDepartment") : "IT_SUPPORT";
        return ResponseEntity.ok(instituteService.assignPasswordReset(instituteId, requestId, handlerName, handlerDepartment));
    }

    @PostMapping("/password-resets/{id}/reject")
    @PreAuthorize("hasAnyRole('INSTITUTE_ADMIN', 'ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<Map<String, String>> rejectPasswordReset(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable("id") Long requestId) {
        Long instituteId = requireInstituteId(principal);
        instituteService.rejectPasswordReset(instituteId, requestId, principal.getId());
        return ResponseEntity.ok(Map.of("message", "Password reset request rejected."));
    }
}
