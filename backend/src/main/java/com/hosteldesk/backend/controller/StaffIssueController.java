package com.hosteldesk.backend.controller;

import com.hosteldesk.backend.dto.IssueDetailDto;
import com.hosteldesk.backend.dto.IssueDto;
import com.hosteldesk.backend.dto.UpdateProgressRequest;
import com.hosteldesk.backend.entity.User;
import com.hosteldesk.backend.exception.ResourceNotFoundException;
import com.hosteldesk.backend.repository.UserRepository;
import com.hosteldesk.backend.security.UserPrincipal;
import com.hosteldesk.backend.service.IssueService;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/staff")
@PreAuthorize("hasAnyRole('MAINTENANCE_STAFF', 'STAFF', 'WARDEN', 'ADMIN', 'INSTITUTE_ADMIN', 'SUPER_ADMIN')")
public class StaffIssueController {

    private final IssueService issueService;
    private final UserRepository userRepository;

    public StaffIssueController(IssueService issueService, UserRepository userRepository) {
        this.issueService = issueService;
        this.userRepository = userRepository;
    }

    @GetMapping("/issues")
    public ResponseEntity<List<IssueDto>> getStaffIssues(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam(value = "filter", defaultValue = "MY_WORK") String filter) {
        if (principal.getRole() != com.hosteldesk.backend.entity.Role.STAFF && 
            principal.getRole() != com.hosteldesk.backend.entity.Role.MAINTENANCE_STAFF) {
            return ResponseEntity.ok(java.util.Collections.emptyList());
        }
        return ResponseEntity.ok(issueService.getStaffIssues(principal.getId(), filter));
    }

    @PostMapping("/issues/{id}/start")
    public ResponseEntity<IssueDetailDto> startWork(
            @PathVariable("id") Long id,
            @AuthenticationPrincipal UserPrincipal principal) {

        User staff = userRepository.findById(principal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Staff user not found"));

        return ResponseEntity.ok(issueService.startWork(id, staff));
    }

    @PostMapping("/issues/{id}/progress-note")
    public ResponseEntity<IssueDetailDto> updateProgressNote(
            @PathVariable("id") Long id,
            @Valid @RequestBody UpdateProgressRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {

        User staff = userRepository.findById(principal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Staff user not found"));

        return ResponseEntity.ok(issueService.updateProgressNote(id, request.getNote(), staff));
    }

    @PostMapping(value = "/issues/{id}/complete", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<IssueDetailDto> completeWork(
            @PathVariable("id") Long id,
            @RequestParam("technicianNote") String technicianNote,
            @RequestPart(value = "proofPhoto", required = false) MultipartFile proofPhoto,
            @AuthenticationPrincipal UserPrincipal principal) {

        User staff = userRepository.findById(principal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Staff user not found"));

        return ResponseEntity.ok(issueService.completeWork(id, technicianNote, proofPhoto, staff));
    }
}
