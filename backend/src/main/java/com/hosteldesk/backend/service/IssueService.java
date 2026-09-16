package com.hosteldesk.backend.service;

import com.hosteldesk.backend.dto.*;
import com.hosteldesk.backend.entity.*;
import com.hosteldesk.backend.exception.ForbiddenException;
import com.hosteldesk.backend.exception.InvalidStateTransitionException;
import com.hosteldesk.backend.exception.ResourceNotFoundException;
import com.hosteldesk.backend.repository.*;
import com.hosteldesk.backend.security.UserPrincipal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class IssueService {
    private static final Logger log = LoggerFactory.getLogger(IssueService.class);

    private final IssueRepository issueRepository;
    private final IssueAttachmentRepository attachmentRepository;
    private final IssueAiAnalysisRepository aiAnalysisRepository;
    private final IssueActivityRepository activityRepository;
    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;
    private final RoutingService routingService;
    private final AiIntegrationService aiIntegrationService;
    private final FileStorageService fileStorageService;
    private final NotificationService notificationService;
    private final HostelRepository hostelRepository;

    public IssueService(IssueRepository issueRepository,
                        IssueAttachmentRepository attachmentRepository,
                        IssueAiAnalysisRepository aiAnalysisRepository,
                        IssueActivityRepository activityRepository,
                        UserRepository userRepository,
                        DepartmentRepository departmentRepository,
                        RoutingService routingService,
                        AiIntegrationService aiIntegrationService,
                        FileStorageService fileStorageService,
                        NotificationService notificationService,
                        HostelRepository hostelRepository) {
        this.issueRepository = issueRepository;
        this.attachmentRepository = attachmentRepository;
        this.aiAnalysisRepository = aiAnalysisRepository;
        this.activityRepository = activityRepository;
        this.userRepository = userRepository;
        this.departmentRepository = departmentRepository;
        this.routingService = routingService;
        this.aiIntegrationService = aiIntegrationService;
        this.fileStorageService = fileStorageService;
        this.notificationService = notificationService;
        this.hostelRepository = hostelRepository;
    }

    @Transactional
    public IssueDetailDto createIssue(User student, CreateIssueRequest request, MultipartFile attachment) {
        log.info("Creating issue for student: {} ({})", student.getFullName(), student.getEmail());

        long nextNum = 1000 + issueRepository.count() + 1;
        String ticketNumber = "HD-" + nextNum;
        while (issueRepository.existsByTicketNumber(ticketNumber)) {
            nextNum++;
            ticketNumber = "HD-" + nextNum;
        }

        Hostel hostel = student.getHostel();
        if (hostel == null && student.getInstitute() != null) {
            List<Hostel> hostels = hostelRepository.findByInstituteId(student.getInstitute().getId());
            if (!hostels.isEmpty()) {
                hostel = hostels.get(0);
                student.setHostel(hostel);
                userRepository.save(student);
            }
        }
        if (hostel == null) {
            List<Hostel> allHostels = hostelRepository.findAll();
            if (!allHostels.isEmpty()) {
                hostel = allHostels.get(0);
                student.setHostel(hostel);
                userRepository.save(student);
            }
        }

        String block = (request.getBlockName() != null && !request.getBlockName().trim().isEmpty())
                ? request.getBlockName().trim()
                : (hostel != null ? hostel.getName() : "Main Block");
        String room = (request.getRoomNumber() != null && !request.getRoomNumber().trim().isEmpty())
                ? request.getRoomNumber().trim()
                : (student.getRoomNumber() != null ? student.getRoomNumber() : "101");

        Issue issue = new Issue();
        issue.setTicketNumber(ticketNumber);
        issue.setReportedBy(student);
        issue.setInstitute(student.getInstitute());
        issue.setCampus(student.getCampus());
        issue.setHostel(hostel);
        issue.setBlockName(block);
        issue.setRoomNumber(room);
        issue.setCategory(request.getCategory() != null ? request.getCategory().toUpperCase() : "GENERAL");
        issue.setTitle(request.getTitle());
        issue.setDescription(request.getDescription());
        issue.setPriority(request.getPriority() != null ? request.getPriority() : IssuePriority.P3_MEDIUM);
        issue.setStatus(IssueStatus.REPORTED);

        issue = issueRepository.save(issue);

        // Save Attachment if present
        if (attachment != null && !attachment.isEmpty()) {
            String fileUrl = fileStorageService.storeFile(attachment);
            IssueAttachment att = new IssueAttachment(
                    null, issue, fileUrl, attachment.getOriginalFilename(),
                    attachment.getContentType(), attachment.getSize(), "STUDENT_REPORT"
            );
            attachmentRepository.save(att);
            issue.getAttachments().add(att);
        }

        // Initial Activity
        activityRepository.save(new IssueActivity(
                null, issue, student, "REPORTED", "Issue #" + ticketNumber + " submitted by " + student.getFullName()
        ));

        // Trigger AI Analysis
        issue.setStatus(IssueStatus.AI_ANALYZING);
        AiInferenceResponse ai = aiIntegrationService.analyzeIssue(
                issue.getTitle(), issue.getDescription(), issue.getCategory(),
                issue.getBlockName(), issue.getRoomNumber()
        );

        IssueAiAnalysis aiAnalysis = new IssueAiAnalysis(
                null, issue, ai.getCategory(), ai.getPriority(),
                ai.getRecommendedDepartment(), ai.getSummary(),
                ai.getSafetyHazardNote(), ai.getConfidence(), ai.getIsFallback()
        );
        aiAnalysisRepository.save(aiAnalysis);
        issue.setAiAnalysis(aiAnalysis);

        // Apply AI classification recommendations if confident
        if (ai.getCategory() != null && !ai.getCategory().trim().isEmpty() &&
            (issue.getCategory() == null || issue.getCategory().trim().isEmpty() ||
             issue.getCategory().toLowerCase().contains("auto-detect") ||
             issue.getCategory().equalsIgnoreCase("GENERAL") ||
             issue.getCategory().equalsIgnoreCase("Plumbing"))) {
            issue.setCategory(ai.getCategory());
        }

        if (ai.getConfidence().doubleValue() > 0.80) {
            try {
                issue.setPriority(IssuePriority.valueOf(ai.getPriority()));
            } catch (Exception ignored) {}
        }
        issue.setStatus(IssueStatus.ANALYZED);

        activityRepository.save(new IssueActivity(
                null, issue, null, "ANALYZED",
                String.format("AI classified as %s (%s). Summary: %s", ai.getCategory(), ai.getPriority(), ai.getSummary())
        ));

        // Automated Routing
        Department dept = routingService.resolveDepartmentForCategory(ai.getRecommendedDepartment());
        if (dept != null) {
            issue.setAssignedDepartment(dept);
            issue.setCategory(dept.getName());
            issue.setStatus(IssueStatus.ASSIGNED);

            activityRepository.save(new IssueActivity(
                null, issue, null, "ROUTED", "Automatically routed to " + dept.getDisplayName()
            ));
        }

        issue = issueRepository.save(issue);

        // In-app Notifications
        notificationService.createNotification(
                student, "Issue Created", "Your ticket #" + ticketNumber + " has been registered and routed for review.",
                "ISSUE_CREATED", issue
        );

        List<User> wardens;
        if (student.getInstitute() != null) {
            wardens = userRepository.findByInstituteIdAndRole(student.getInstitute().getId(), Role.WARDEN);
            final Long assignedHostelId = hostel != null ? hostel.getId() : null;
            if (assignedHostelId != null) {
                List<User> hostelWardens = wardens.stream()
                        .filter(w -> w.getHostel() != null && assignedHostelId.equals(w.getHostel().getId()))
                        .collect(Collectors.toList());
                if (!hostelWardens.isEmpty()) {
                    wardens = hostelWardens;
                }
            }
        } else {
            wardens = userRepository.findByRole(Role.WARDEN);
        }

        for (User warden : wardens) {
            notificationService.createNotification(
                    warden, "New Issue: #" + ticketNumber,
                    String.format("%s in %s Room %s: %s", issue.getCategory(), issue.getBlockName(), issue.getRoomNumber(), issue.getTitle()),
                    "NEW_ISSUE_WARDEN", issue
            );
        }

        return IssueDetailDto.fromEntity(issue);
    }

    @Transactional(readOnly = true)
    public List<IssueDto> getStudentIssues(Long studentId, IssueStatus status) {
        List<Issue> issues;
        if (status != null) {
            issues = issueRepository.findByReportedByIdAndStatus(studentId, status);
        } else {
            issues = issueRepository.findByReportedByIdOrderByCreatedAtDesc(studentId);
        }
        return issues.stream().map(IssueDto::fromEntity).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<IssueDto> getStudentIssuesByFilter(Long studentId, String filter) {
        if (filter == null || filter.trim().isEmpty() || filter.equalsIgnoreCase("ALL")) {
            return getStudentIssues(studentId, null);
        }
        String upper = filter.trim().toUpperCase();
        if (upper.equals("AWAITING_VERIFICATION") || upper.equals("NEEDS_VERIFICATION")) {
            return getStudentIssues(studentId, IssueStatus.AWAITING_VERIFICATION);
        } else if (upper.equals("OPEN") || upper.equals("SUBMITTED")) {
            List<IssueStatus> openStatuses = List.of(
                    IssueStatus.REPORTED, IssueStatus.AI_ANALYZING,
                    IssueStatus.ANALYZED, IssueStatus.ASSIGNED, IssueStatus.REOPENED
            );
            List<Issue> issues = issueRepository.findByReportedByIdAndStatusInOrderByCreatedAtDesc(studentId, openStatuses);
            return issues.stream().map(IssueDto::fromEntity).collect(Collectors.toList());
        } else if (upper.equals("IN_PROGRESS")) {
            return getStudentIssues(studentId, IssueStatus.IN_PROGRESS);
        } else if (upper.equals("RESOLVED")) {
            List<IssueStatus> resolvedStatuses = List.of(IssueStatus.RESOLVED, IssueStatus.VERIFIED, IssueStatus.AWAITING_VERIFICATION);
            List<Issue> issues = issueRepository.findByReportedByIdAndStatusInOrderByCreatedAtDesc(studentId, resolvedStatuses);
            return issues.stream().map(IssueDto::fromEntity).collect(Collectors.toList());
        } else if (upper.equals("CLOSED")) {
            List<IssueStatus> closedStatuses = List.of(IssueStatus.RESOLVED, IssueStatus.VERIFIED, IssueStatus.CANCELLED);
            List<Issue> issues = issueRepository.findByReportedByIdAndStatusInOrderByCreatedAtDesc(studentId, closedStatuses);
            return issues.stream().map(IssueDto::fromEntity).collect(Collectors.toList());
        } else {
            try {
                IssueStatus directStatus = IssueStatus.valueOf(upper);
                return getStudentIssues(studentId, directStatus);
            } catch (Exception e) {
                return getStudentIssues(studentId, null);
            }
        }
    }

    @Transactional
    public IssueDetailDto getIssueDetail(Long issueId, UserPrincipal principal) {
        Issue issue = issueRepository.findById(issueId)
                .orElseThrow(() -> new ResourceNotFoundException("Issue not found with id: " + issueId));

        if (principal.getRole() == Role.STUDENT && !issue.getReportedBy().getId().equals(principal.getId())) {
            throw new ForbiddenException("Access Denied: You are not authorized to view another student's complaint.");
        }

        if (principal.getRole() == Role.WARDEN) {
            int currentViews = issue.getWardenViewCount() != null ? issue.getWardenViewCount() : 0;
            issue.setWardenViewCount(currentViews + 1);
            issue.setWardenViewedAt(ZonedDateTime.now());
            issue = issueRepository.save(issue);
        }

        return IssueDetailDto.fromEntity(issue);
    }

    @Transactional(readOnly = true)
    public List<IssueDto> searchAdminIssues(IssueStatus status, IssuePriority priority, Long departmentId) {
        return searchAdminIssues(null, null, status, priority, departmentId);
    }

    @Transactional(readOnly = true)
    public List<IssueDto> searchAdminIssues(Long instituteId, IssueStatus status, IssuePriority priority, Long departmentId) {
        return searchAdminIssues(instituteId, null, status, priority, departmentId);
    }

    @Transactional(readOnly = true)
    public List<IssueDto> searchAdminIssues(Long instituteId, Long hostelId, String statusFilter, IssuePriority priority, Long departmentId) {
        if (statusFilter == null || statusFilter.trim().isEmpty() || statusFilter.equalsIgnoreCase("ALL")) {
            return issueRepository.searchAdminIssues(instituteId, hostelId, null, priority, departmentId)
                    .stream().map(IssueDto::fromEntity).collect(Collectors.toList());
        }

        String upper = statusFilter.trim().toUpperCase();
        List<IssueStatus> statuses;
        if (upper.equals("CLOSED")) {
            statuses = List.of(IssueStatus.VERIFIED, IssueStatus.RESOLVED, IssueStatus.CANCELLED);
        } else if (upper.equals("RESOLVED")) {
            statuses = List.of(IssueStatus.RESOLVED, IssueStatus.VERIFIED, IssueStatus.AWAITING_VERIFICATION);
        } else if (upper.equals("AWAITING_VERIFICATION") || upper.equals("NEEDS_VERIFICATION")) {
            statuses = List.of(IssueStatus.AWAITING_VERIFICATION);
        } else if (upper.equals("SUBMITTED") || upper.equals("OPEN")) {
            statuses = List.of(IssueStatus.REPORTED, IssueStatus.AI_ANALYZING, IssueStatus.ANALYZED, IssueStatus.ASSIGNED, IssueStatus.REOPENED);
        } else if (upper.equals("IN_PROGRESS")) {
            statuses = List.of(IssueStatus.IN_PROGRESS);
        } else if (upper.equals("ASSIGNED")) {
            statuses = List.of(IssueStatus.ASSIGNED);
        } else {
            try {
                IssueStatus direct = IssueStatus.valueOf(upper);
                statuses = List.of(direct);
            } catch (Exception e) {
                return issueRepository.searchAdminIssues(instituteId, hostelId, null, priority, departmentId)
                        .stream().map(IssueDto::fromEntity).collect(Collectors.toList());
            }
        }

        return issueRepository.searchAdminIssuesByStatuses(instituteId, hostelId, statuses, priority, departmentId)
                .stream().map(IssueDto::fromEntity).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<IssueDto> searchAdminIssues(Long instituteId, Long hostelId, IssueStatus status, IssuePriority priority, Long departmentId) {
        return issueRepository.searchAdminIssues(instituteId, hostelId, status, priority, departmentId)
                .stream().map(IssueDto::fromEntity).collect(Collectors.toList());
    }

    @Transactional
    public IssueDetailDto assignIssue(Long issueId, AssignIssueRequest request, User warden) {
        Issue issue = issueRepository.findById(issueId)
                .orElseThrow(() -> new ResourceNotFoundException("Issue not found with id: " + issueId));

        Department dept = null;
        if (request.getDepartmentId() != null) {
            dept = departmentRepository.findById(request.getDepartmentId()).orElse(null);
        }
        if (dept == null && issue.getAssignedDepartment() != null) {
            dept = issue.getAssignedDepartment();
        }
        if (dept == null) {
            dept = departmentRepository.findAll().stream().findFirst().orElse(null);
        }
        if (dept == null) {
            throw new ResourceNotFoundException("No departments configured in the system.");
        }

        User staff = null;
        if (request.getStaffId() != null) {
            staff = userRepository.findById(request.getStaffId())
                    .orElseThrow(() -> new ResourceNotFoundException("Staff user not found: " + request.getStaffId()));
        }

        issue.setAssignedDepartment(dept);
        issue.setAssignedStaff(staff);
        issue.setCategory(dept.getName());
        if (request.getPriority() != null) issue.setPriority(request.getPriority());
        issue.setStatus(IssueStatus.ASSIGNED);

        String msg = "Assigned to " + dept.getDisplayName() + (staff != null ? " (Tech: " + staff.getFullName() + ")" : "");
        if (request.getNotes() != null && !request.getNotes().trim().isEmpty()) {
            msg += " — " + request.getNotes();
        }

        activityRepository.save(new IssueActivity(null, issue, warden, "ASSIGNED", msg));
        issue = issueRepository.save(issue);

        if (staff != null) {
            notificationService.createNotification(
                    staff, "New Work Order Assigned", "You have been assigned ticket #" + issue.getTicketNumber(),
                    "ISSUE_ASSIGNED", issue
            );
        }

        notificationService.createNotification(
                issue.getReportedBy(), "Technician Assigned",
                "Your issue #" + issue.getTicketNumber() + " has been assigned to " + dept.getDisplayName(),
                "ISSUE_ASSIGNED", issue
        );

        return IssueDetailDto.fromEntity(issue);
    }

    @Transactional
    public IssueDetailDto startWork(Long issueId, User staff) {
        Issue issue = issueRepository.findById(issueId)
                .orElseThrow(() -> new ResourceNotFoundException("Issue not found: " + issueId));

        if (issue.getStatus() != IssueStatus.ASSIGNED && issue.getStatus() != IssueStatus.REOPENED) {
            throw new InvalidStateTransitionException(issue.getStatus(), IssueStatus.IN_PROGRESS, "Work can only start on ASSIGNED or REOPENED issues.");
        }

        issue.setStatus(IssueStatus.IN_PROGRESS);
        if (issue.getAssignedStaff() == null) {
            issue.setAssignedStaff(staff);
        }

        activityRepository.save(new IssueActivity(null, issue, staff, "IN_PROGRESS", "Tech " + staff.getFullName() + " started work."));
        issue = issueRepository.save(issue);

        notificationService.createNotification(
                issue.getReportedBy(), "Technician On Site",
                "Work has begun on your ticket #" + issue.getTicketNumber(),
                "WORK_STARTED", issue
        );

        return IssueDetailDto.fromEntity(issue);
    }

    @Transactional
    public IssueDetailDto updateProgressNote(Long issueId, String note, User staff) {
        Issue issue = issueRepository.findById(issueId)
                .orElseThrow(() -> new ResourceNotFoundException("Issue not found: " + issueId));

        issue.setTechnicianNotes(note);
        activityRepository.save(new IssueActivity(null, issue, staff, "NOTE_ADDED", "Technician note: " + note));
        issue = issueRepository.save(issue);

        return IssueDetailDto.fromEntity(issue);
    }

    @Transactional
    public IssueDetailDto completeWork(Long issueId, String note, MultipartFile proofPhoto, User staff) {
        Issue issue = issueRepository.findById(issueId)
                .orElseThrow(() -> new ResourceNotFoundException("Issue not found: " + issueId));

        if (issue.getStatus() != IssueStatus.IN_PROGRESS && issue.getStatus() != IssueStatus.ASSIGNED && issue.getStatus() != IssueStatus.REOPENED) {
            throw new InvalidStateTransitionException(issue.getStatus(), IssueStatus.AWAITING_VERIFICATION, "Can only complete issues currently ASSIGNED, IN_PROGRESS, or REOPENED.");
        }

        if (proofPhoto != null && !proofPhoto.isEmpty()) {
            String fileUrl = fileStorageService.storeFile(proofPhoto);
            IssueAttachment att = new IssueAttachment(
                    null, issue, fileUrl, proofPhoto.getOriginalFilename(),
                    proofPhoto.getContentType(), proofPhoto.getSize(), "STAFF_COMPLETION_PROOF"
            );
            attachmentRepository.save(att);
            issue.getAttachments().add(att);
        }

        issue.setTechnicianNotes(note);
        issue.setStatus(IssueStatus.AWAITING_VERIFICATION);

        activityRepository.save(new IssueActivity(
                null, issue, staff, "COMPLETED",
                "Work marked completed by tech " + staff.getFullName() + ": " + note
        ));
        issue = issueRepository.save(issue);

        // Notify student for resolution verification
        notificationService.createNotification(
                issue.getReportedBy(), "Please Verify Resolution",
                "Technician " + staff.getFullName() + " completed repair on #" + issue.getTicketNumber() + ". Please inspect and confirm.",
                "VERIFICATION_REQUEST", issue
        );

        return IssueDetailDto.fromEntity(issue);
    }

    @Transactional
    public IssueDetailDto verifyResolution(Long issueId, String satisfactionNote, User student) {
        return verifyResolution(issueId, satisfactionNote, null, null, student);
    }

    @Transactional
    public IssueDetailDto verifyResolution(Long issueId, String satisfactionNote, Integer rating, String workerReview, User student) {
        Issue issue = issueRepository.findById(issueId)
                .orElseThrow(() -> new ResourceNotFoundException("Issue not found: " + issueId));

        boolean isReporter = issue.getReportedBy() != null &&
                (issue.getReportedBy().getId().equals(student.getId()) ||
                 issue.getReportedBy().getEmail().equalsIgnoreCase(student.getEmail()));

        if (!isReporter) {
            boolean sameRoom = student.getRoomNumber() != null &&
                    student.getRoomNumber().equalsIgnoreCase(issue.getRoomNumber()) &&
                    student.getHostel() != null && issue.getHostel() != null &&
                    student.getHostel().getId().equals(issue.getHostel().getId());
            if (!sameRoom) {
                throw new ForbiddenException("Only the resident who reported this issue can verify resolution.");
            }
        }

        if (issue.getStatus() == IssueStatus.VERIFIED) {
            return IssueDetailDto.fromEntity(issue);
        }

        if (issue.getStatus() != IssueStatus.AWAITING_VERIFICATION && issue.getStatus() != IssueStatus.RESOLVED) {
            throw new InvalidStateTransitionException(issue.getStatus(), IssueStatus.VERIFIED, "Cannot verify issue in status: " + issue.getStatus());
        }

        issue.setStatus(IssueStatus.VERIFIED);
        issue.setResolvedAt(issue.getResolvedAt() != null ? issue.getResolvedAt() : ZonedDateTime.now());
        issue.setVerifiedAt(ZonedDateTime.now());
        issue.setReopenReason(null);
        issue.setResolutionNotes(satisfactionNote != null ? satisfactionNote : "Resolved and verified by resident.");
        if (rating != null && rating >= 1 && rating <= 5) {
            issue.setRating(rating);
        }
        if (workerReview != null && !workerReview.trim().isEmpty()) {
            issue.setWorkerReview(workerReview.trim());
        }

        String activityMsg = "Resident verified resolution. Issue closed.";
        if (issue.getRating() != null) {
            activityMsg += " Rated " + issue.getRating() + "★" + (issue.getWorkerReview() != null ? ": \"" + issue.getWorkerReview() + "\"" : "");
        }

        activityRepository.save(new IssueActivity(
                null, issue, student, "VERIFIED", activityMsg
        ));
        issue = issueRepository.save(issue);

        if (issue.getAssignedStaff() != null) {
            String staffNotifMsg = "Student confirmed resolution for #" + issue.getTicketNumber();
            if (issue.getRating() != null) {
                staffNotifMsg += " (" + issue.getRating() + "★ Rating)";
            }
            notificationService.createNotification(
                    issue.getAssignedStaff(), "Ticket Verified by Student",
                    staffNotifMsg,
                    "ISSUE_RESOLVED", issue
            );
        }

        return IssueDetailDto.fromEntity(issue);
    }


    @Transactional
    public IssueDetailDto reopenIssue(Long issueId, String reason, User student) {
        Issue issue = issueRepository.findById(issueId)
                .orElseThrow(() -> new ResourceNotFoundException("Issue not found: " + issueId));

        boolean isReporter = issue.getReportedBy() != null &&
                (issue.getReportedBy().getId().equals(student.getId()) ||
                 issue.getReportedBy().getEmail().equalsIgnoreCase(student.getEmail()));

        if (!isReporter) {
            boolean sameRoom = student.getRoomNumber() != null &&
                    student.getRoomNumber().equalsIgnoreCase(issue.getRoomNumber()) &&
                    student.getHostel() != null && issue.getHostel() != null &&
                    student.getHostel().getId().equals(issue.getHostel().getId());
            if (!sameRoom) {
                throw new ForbiddenException("Only the resident who reported this issue can reopen it.");
            }
        }

        if (issue.getStatus() != IssueStatus.AWAITING_VERIFICATION && issue.getStatus() != IssueStatus.RESOLVED && issue.getStatus() != IssueStatus.VERIFIED) {
            throw new InvalidStateTransitionException(issue.getStatus(), IssueStatus.REOPENED, "Cannot reopen issue in state: " + issue.getStatus());
        }

        issue.setStatus(IssueStatus.REOPENED);
        issue.setReopenReason(reason);
        issue.setVerifiedAt(null);
        issue.setResolvedAt(null);

        activityRepository.save(new IssueActivity(
                null, issue, student, "REOPENED", "Resident rejected resolution: " + reason
        ));
        issue = issueRepository.save(issue);

        if (issue.getAssignedStaff() != null) {
            notificationService.createNotification(
                    issue.getAssignedStaff(), "Ticket Reopened: #" + issue.getTicketNumber(),
                    "Student indicated problem is still not resolved: " + reason,
                    "ISSUE_REOPENED", issue
            );
        }

        for (User warden : userRepository.findByRole(Role.WARDEN)) {
            notificationService.createNotification(
                    warden, "Alert: Ticket Reopened #" + issue.getTicketNumber(),
                    "Resident in Room " + issue.getRoomNumber() + " reopened issue: " + reason,
                    "ISSUE_REOPENED", issue
            );
        }

        return IssueDetailDto.fromEntity(issue);
    }

    @Transactional(readOnly = true)
    public List<IssueDto> getStaffIssues(Long staffId, String filter) {
        User staff = userRepository.findById(staffId)
                .orElseThrow(() -> new ResourceNotFoundException("Staff user not found: " + staffId));

        if ("COMPLETED".equalsIgnoreCase(filter) || "RESOLVED_HISTORY".equalsIgnoreCase(filter)) {
            return issueRepository.findByAssignedStaffIdAndStatusIn(staffId, List.of(IssueStatus.RESOLVED, IssueStatus.VERIFIED))
                    .stream().map(IssueDto::fromEntity).collect(Collectors.toList());
        } else if ("QUEUE".equalsIgnoreCase(filter) || "DEPT_QUEUE".equalsIgnoreCase(filter)) {
            if (staff.getDepartment() != null) {
                return issueRepository.findAll().stream()
                        .filter(i -> i.getAssignedDepartment() != null &&
                                     i.getAssignedDepartment().getId().equals(staff.getDepartment().getId()) &&
                                     i.getStatus() != IssueStatus.RESOLVED &&
                                     i.getStatus() != IssueStatus.VERIFIED &&
                                     i.getStatus() != IssueStatus.CANCELLED)
                        .map(IssueDto::fromEntity).collect(Collectors.toList());
            }
            return new ArrayList<>();
        } else {
            // Default: MY_WORK
            List<IssueStatus> activeWork = Arrays.asList(IssueStatus.ASSIGNED, IssueStatus.IN_PROGRESS, IssueStatus.REOPENED, IssueStatus.AWAITING_VERIFICATION);
            List<Issue> directIssues = new ArrayList<>(issueRepository.findByAssignedStaffIdAndStatusIn(staffId, activeWork));
            if (staff.getDepartment() != null) {
                List<Issue> deptIssues = issueRepository.findAll().stream()
                        .filter(i -> i.getAssignedDepartment() != null &&
                                     i.getAssignedDepartment().getId().equals(staff.getDepartment().getId()) &&
                                     i.getAssignedStaff() == null &&
                                     activeWork.contains(i.getStatus()))
                        .collect(Collectors.toList());
                for (Issue di : deptIssues) {
                    if (!directIssues.contains(di)) {
                        directIssues.add(di);
                    }
                }
            }
            return directIssues.stream().map(IssueDto::fromEntity).collect(Collectors.toList());
        }
    }
}
