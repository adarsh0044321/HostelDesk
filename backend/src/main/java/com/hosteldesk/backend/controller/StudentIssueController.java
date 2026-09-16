package com.hosteldesk.backend.controller;

import com.hosteldesk.backend.dto.*;
import com.hosteldesk.backend.entity.IssuePriority;
import com.hosteldesk.backend.entity.IssueStatus;
import com.hosteldesk.backend.entity.User;
import com.hosteldesk.backend.exception.ResourceNotFoundException;
import com.hosteldesk.backend.repository.UserRepository;
import com.hosteldesk.backend.security.UserPrincipal;
import com.hosteldesk.backend.service.AnalyticsService;
import com.hosteldesk.backend.service.InstituteService;
import com.hosteldesk.backend.service.IssueService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/student")
@PreAuthorize("hasRole('STUDENT')")
public class StudentIssueController {

    private final IssueService issueService;
    private final AnalyticsService analyticsService;
    private final UserRepository userRepository;
    private final InstituteService instituteService;

    public StudentIssueController(IssueService issueService,
                                  AnalyticsService analyticsService,
                                  UserRepository userRepository,
                                  InstituteService instituteService) {
        this.issueService = issueService;
        this.analyticsService = analyticsService;
        this.userRepository = userRepository;
        this.instituteService = instituteService;
    }

    @GetMapping("/dashboard")
    public ResponseEntity<StudentDashboardDto> getDashboard(@AuthenticationPrincipal UserPrincipal principal) {
        User student = userRepository.findById(principal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));
        return ResponseEntity.ok(analyticsService.getStudentDashboard(student));
    }

    @GetMapping("/emergency-contacts")
    public ResponseEntity<EmergencyContactsDto> getEmergencyContacts(@AuthenticationPrincipal UserPrincipal principal) {
        User student = userRepository.findById(principal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));
        Long instId = student.getInstitute() != null ? student.getInstitute().getId() : null;
        Long hostelId = student.getHostel() != null ? student.getHostel().getId() : null;
        return ResponseEntity.ok(instituteService.getEmergencyContacts(instId, hostelId));
    }

    @PostMapping(value = "/issues", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<IssueDetailDto> createIssue(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam("title") String title,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "priority", required = false) String priority,
            @RequestParam(value = "blockName", required = false) String blockName,
            @RequestParam(value = "roomNumber", required = false) String roomNumber,
            @RequestPart(value = "attachment", required = false) MultipartFile attachment) {

        User student = userRepository.findById(principal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));

        CreateIssueRequest req = new CreateIssueRequest();
        req.setTitle(title);
        req.setDescription(description != null && !description.trim().isEmpty() ? description : title);
        req.setCategory(category != null && !category.trim().isEmpty() ? category : "GENERAL");
        req.setBlockName(blockName);
        req.setRoomNumber(roomNumber);
        if (priority != null && !priority.isEmpty()) {
            try {
                req.setPriority(IssuePriority.valueOf(priority));
            } catch (Exception ignored) {}
        }

        IssueDetailDto created = issueService.createIssue(student, req, attachment);
        return ResponseEntity.status(201).body(created);
    }

    @GetMapping("/issues")
    public ResponseEntity<List<IssueDto>> getMyIssues(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam(value = "status", required = false) String status) {

        return ResponseEntity.ok(issueService.getStudentIssuesByFilter(principal.getId(), status));
    }

    @GetMapping("/issues/{id}")
    public ResponseEntity<IssueDetailDto> getIssueDetail(
            @PathVariable("id") Long id,
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(issueService.getIssueDetail(id, principal));
    }

    @PostMapping("/issues/{id}/verify")
    public ResponseEntity<IssueDetailDto> verifyResolution(
            @PathVariable("id") Long id,
            @RequestBody(required = false) VerifyResolutionRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {

        User student = userRepository.findById(principal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));

        String note = request != null && request.getSatisfactionNote() != null ? request.getSatisfactionNote() : "Confirmed fixed by resident";
        Integer rating = request != null ? request.getRating() : null;
        String workerReview = request != null ? request.getWorkerReview() : null;
        return ResponseEntity.ok(issueService.verifyResolution(id, note, rating, workerReview, student));
    }


    @PostMapping("/issues/{id}/reopen")
    public ResponseEntity<IssueDetailDto> reopenIssue(
            @PathVariable("id") Long id,
            @RequestBody ReopenIssueRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {

        User student = userRepository.findById(principal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));

        return ResponseEntity.ok(issueService.reopenIssue(id, request.getReason(), student));
    }
}
