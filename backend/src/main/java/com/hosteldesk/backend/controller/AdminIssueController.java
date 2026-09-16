package com.hosteldesk.backend.controller;

import com.hosteldesk.backend.dto.*;
import com.hosteldesk.backend.entity.Department;
import com.hosteldesk.backend.entity.IssuePriority;
import com.hosteldesk.backend.entity.IssueStatus;
import com.hosteldesk.backend.entity.Role;
import com.hosteldesk.backend.entity.User;
import com.hosteldesk.backend.exception.ResourceNotFoundException;
import com.hosteldesk.backend.repository.DepartmentRepository;
import com.hosteldesk.backend.repository.UserRepository;
import com.hosteldesk.backend.security.UserPrincipal;
import com.hosteldesk.backend.service.AnalyticsService;
import com.hosteldesk.backend.service.IssueService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasAnyRole('WARDEN', 'ADMIN', 'INSTITUTE_ADMIN', 'SUPER_ADMIN')")
public class AdminIssueController {

    private final IssueService issueService;
    private final AnalyticsService analyticsService;
    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;
    private final com.hosteldesk.backend.repository.IssueRepository issueRepository;

    public AdminIssueController(IssueService issueService,
                                AnalyticsService analyticsService,
                                UserRepository userRepository,
                                DepartmentRepository departmentRepository,
                                com.hosteldesk.backend.repository.IssueRepository issueRepository) {
        this.issueService = issueService;
        this.analyticsService = analyticsService;
        this.userRepository = userRepository;
        this.departmentRepository = departmentRepository;
        this.issueRepository = issueRepository;
    }

    @GetMapping("/dashboard")
    public ResponseEntity<WardenDashboardDto> getDashboard(@AuthenticationPrincipal UserPrincipal principal) {
        Long instituteId = principal != null ? principal.getInstituteId() : null;
        Long hostelId = principal != null ? principal.getHostelId() : null;
        com.hosteldesk.backend.entity.Role role = principal != null ? principal.getRole() : null;
        return ResponseEntity.ok(analyticsService.getWardenDashboard(instituteId, hostelId, role));
    }

    @GetMapping("/issues")
    public ResponseEntity<List<IssueDto>> getIssues(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "priority", required = false) String priority,
            @RequestParam(value = "departmentId", required = false) Long departmentId) {

        Long instituteId = principal != null ? principal.getInstituteId() : null;
        Long hostelId = (principal != null && principal.getRole() == com.hosteldesk.backend.entity.Role.WARDEN) ? principal.getHostelId() : null;

        IssuePriority issuePriority = null;
        if (priority != null && !priority.isEmpty()) {
            try {
                issuePriority = IssuePriority.valueOf(priority.toUpperCase());
            } catch (Exception ignored) {}
        }

        return ResponseEntity.ok(issueService.searchAdminIssues(instituteId, hostelId, status, issuePriority, departmentId));
    }

    @GetMapping("/issues/{id}")
    public ResponseEntity<IssueDetailDto> getIssueDetail(
            @PathVariable("id") Long id,
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(issueService.getIssueDetail(id, principal));
    }

    @PostMapping("/issues/{id}/assign")
    public ResponseEntity<IssueDetailDto> assignIssue(
            @PathVariable("id") Long id,
            @Valid @RequestBody AssignIssueRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {

        User warden = userRepository.findById(principal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Warden not found"));

        return ResponseEntity.ok(issueService.assignIssue(id, request, warden));
    }

    @GetMapping("/staff")
    public ResponseEntity<List<UserDto>> getStaffList(@AuthenticationPrincipal UserPrincipal principal) {
        List<User> staffUsers = new ArrayList<>();
        if (principal != null && principal.getInstituteId() != null) {
            staffUsers.addAll(userRepository.findByInstituteIdAndRole(principal.getInstituteId(), Role.STAFF));
            staffUsers.addAll(userRepository.findByInstituteIdAndRole(principal.getInstituteId(), Role.MAINTENANCE_STAFF));
        } else {
            staffUsers.addAll(userRepository.findByRole(Role.STAFF));
            staffUsers.addAll(userRepository.findByRole(Role.MAINTENANCE_STAFF));
        }
        List<UserDto> dtos = staffUsers.stream()
                .distinct()
                .map(u -> {
                    UserDto dto = UserDto.fromEntity(u);
                    try {
                        long active = issueRepository.countByAssignedStaffIdAndStatusIn(
                                u.getId(),
                                List.of(IssueStatus.ASSIGNED, IssueStatus.IN_PROGRESS)
                        );
                        long total = issueRepository.countByAssignedStaffId(u.getId());
                        dto.setActiveComplaints(active);
                        dto.setTotalComplaints(total);
                    } catch (Exception ignored) {}
                    return dto;
                })
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/departments")
    public ResponseEntity<List<Department>> getDepartments(@AuthenticationPrincipal UserPrincipal principal) {
        if (principal != null && principal.getInstituteId() != null) {
            List<Department> list = departmentRepository.findByInstituteId(principal.getInstituteId());
            if (list != null && !list.isEmpty()) {
                return ResponseEntity.ok(list);
            }
        }
        return ResponseEntity.ok(departmentRepository.findAll());
    }
}
