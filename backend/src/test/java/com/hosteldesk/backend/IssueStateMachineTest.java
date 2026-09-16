package com.hosteldesk.backend;

import com.hosteldesk.backend.dto.AssignIssueRequest;
import com.hosteldesk.backend.dto.IssueDetailDto;
import com.hosteldesk.backend.dto.IssueDto;
import java.util.List;
import com.hosteldesk.backend.entity.IssuePriority;
import com.hosteldesk.backend.entity.IssueStatus;
import com.hosteldesk.backend.entity.Role;
import com.hosteldesk.backend.entity.User;
import com.hosteldesk.backend.exception.InvalidStateTransitionException;
import com.hosteldesk.backend.repository.DepartmentRepository;
import com.hosteldesk.backend.repository.UserRepository;
import com.hosteldesk.backend.service.IssueService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("test")
class IssueStateMachineTest {

    @Autowired
    private IssueService issueService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Test
    @Transactional
    void testValidIssueLifecycleAndVerification() {
        User student = userRepository.findByEmail("aarav@campus.edu").orElseThrow();
        User staff = userRepository.findByEmail("suresh@campus.edu").orElseThrow();
        User warden = userRepository.findByEmail("warden.sharma@campus.edu").orElseThrow();

        // Find initial sample issue #HD-1038 (which is IN_PROGRESS) or HD-1042 (AWAITING_VERIFICATION)
        IssueDetailDto hd1042 = issueService.getStudentIssues(student.getId(), IssueStatus.AWAITING_VERIFICATION)
                .stream().findFirst()
                .map(i -> issueService.getIssueDetail(i.getId(), com.hosteldesk.backend.security.UserPrincipal.create(student)))
                .orElseThrow();

        // Check getStudentIssuesByFilter
        List<IssueDto> awaitingList = issueService.getStudentIssuesByFilter(student.getId(), "AWAITING_VERIFICATION");
        Assertions.assertFalse(awaitingList.isEmpty(), "Should return awaiting verification issues");

        // Verify resolution with rating and worker review
        IssueDetailDto resolved = issueService.verifyResolution(hd1042.getId(), "Ceiling confirmed dry", 5, "Outstanding plumber work", student);
        Assertions.assertEquals(IssueStatus.VERIFIED, resolved.getStatus());
        Assertions.assertNotNull(resolved.getResolvedAt());
        Assertions.assertNotNull(resolved.getVerifiedAt());
        Assertions.assertEquals(5, resolved.getRating());
        Assertions.assertEquals("Outstanding plumber work", resolved.getWorkerReview());
    }


    @Test
    @Transactional
    void testReopenIssueTransition() {
        User student = userRepository.findByEmail("aarav@campus.edu").orElseThrow();

        // HD-1042 is awaiting verification; resident can reopen
        IssueDetailDto hd1042 = issueService.getStudentIssues(student.getId(), null)
                .stream().filter(i -> i.getTicketNumber().equals("HD-1042")).findFirst()
                .map(i -> issueService.getIssueDetail(i.getId(), com.hosteldesk.backend.security.UserPrincipal.create(student)))
                .orElseThrow();

        IssueDetailDto reopened = issueService.reopenIssue(hd1042.getId(), "Still dripping water slowly", student);
        Assertions.assertEquals(IssueStatus.REOPENED, reopened.getStatus());
        Assertions.assertEquals("Still dripping water slowly", reopened.getReopenReason());
    }

    @Test
    @Transactional
    void testIllegalStateTransitionRejected() {
        User student = userRepository.findByEmail("aarav@campus.edu").orElseThrow();
        User staff = userRepository.findByEmail("suresh@campus.edu").orElseThrow();

        // HD-1042 is AWAITING_VERIFICATION, staff cannot startWork directly without it being ASSIGNED or REOPENED
        IssueDetailDto hd1042 = issueService.getStudentIssues(student.getId(), null).stream()
                .filter(i -> i.getTicketNumber().equals("HD-1042")).findFirst()
                .map(i -> issueService.getIssueDetail(i.getId(), com.hosteldesk.backend.security.UserPrincipal.create(staff)))
                .orElseThrow();

        Assertions.assertThrows(InvalidStateTransitionException.class, () -> issueService.startWork(hd1042.getId(), staff));
    }
}
