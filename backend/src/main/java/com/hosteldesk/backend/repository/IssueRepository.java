package com.hosteldesk.backend.repository;

import com.hosteldesk.backend.entity.Issue;
import com.hosteldesk.backend.entity.IssuePriority;
import com.hosteldesk.backend.entity.IssueStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface IssueRepository extends JpaRepository<Issue, Long> {
    Optional<Issue> findByTicketNumber(String ticketNumber);
    boolean existsByTicketNumber(String ticketNumber);

    List<Issue> findByReportedByIdOrderByCreatedAtDesc(Long studentId);
    Page<Issue> findByReportedByIdOrderByCreatedAtDesc(Long studentId, Pageable pageable);
    List<Issue> findByReportedByIdAndStatus(Long studentId, IssueStatus status);
    List<Issue> findByReportedByIdAndStatusInOrderByCreatedAtDesc(Long studentId, List<IssueStatus> statuses);
    long countByReportedByIdAndStatusIn(Long studentId, List<IssueStatus> statuses);

    List<Issue> findByAssignedStaffIdOrderByCreatedAtDesc(Long staffId);
    List<Issue> findByAssignedStaffIdAndStatus(Long staffId, IssueStatus status);
    List<Issue> findByAssignedStaffIdAndStatusIn(Long staffId, List<IssueStatus> statuses);
    long countByAssignedStaffIdAndStatusIn(Long staffId, List<IssueStatus> statuses);
    long countByAssignedStaffId(Long staffId);

    List<Issue> findByHostelIdOrderByCreatedAtDesc(Long hostelId);
    List<Issue> findByStatusOrderByCreatedAtDesc(IssueStatus status);
    List<Issue> findByPriorityOrderByCreatedAtDesc(IssuePriority priority);

    long countByStatus(IssueStatus status);
    long countByStatusIn(List<IssueStatus> statuses);
    long countByPriority(IssuePriority priority);
    long countByAssignedDepartmentId(Long departmentId);
    long countByAssignedDepartmentIdAndStatusIn(Long departmentId, List<IssueStatus> statuses);

    // Multi-tenant queries
    List<Issue> findByInstituteIdOrderByCreatedAtDesc(Long instituteId);
    long countByInstituteId(Long instituteId);
    long countByInstituteIdAndStatus(Long instituteId, IssueStatus status);
    long countByInstituteIdAndStatusIn(Long instituteId, List<IssueStatus> statuses);
    long countByInstituteIdAndPriority(Long instituteId, IssuePriority priority);
    long countByHostelIdAndPriority(Long hostelId, IssuePriority priority);

    @Query("SELECT i FROM Issue i WHERE (:instituteId IS NULL OR i.institute.id = :instituteId) " +
           "AND (:hostelId IS NULL OR i.hostel.id = :hostelId) " +
           "AND (:status IS NULL OR i.status = :status) " +
           "AND (:priority IS NULL OR i.priority = :priority) " +
           "AND (:departmentId IS NULL OR i.assignedDepartment.id = :departmentId) " +
           "ORDER BY i.createdAt DESC")
    List<Issue> searchAdminIssues(@Param("instituteId") Long instituteId,
                                  @Param("hostelId") Long hostelId,
                                  @Param("status") IssueStatus status,
                                  @Param("priority") IssuePriority priority,
                                  @Param("departmentId") Long departmentId);

    @Query("SELECT i FROM Issue i WHERE (:instituteId IS NULL OR i.institute.id = :instituteId) " +
           "AND (:hostelId IS NULL OR i.hostel.id = :hostelId) " +
           "AND (i.status IN (:statuses)) " +
           "AND (:priority IS NULL OR i.priority = :priority) " +
           "AND (:departmentId IS NULL OR i.assignedDepartment.id = :departmentId) " +
           "ORDER BY i.createdAt DESC")
    List<Issue> searchAdminIssuesByStatuses(@Param("instituteId") Long instituteId,
                                            @Param("hostelId") Long hostelId,
                                            @Param("statuses") List<IssueStatus> statuses,
                                            @Param("priority") IssuePriority priority,
                                            @Param("departmentId") Long departmentId);

    @Query("SELECT COUNT(i) FROM Issue i WHERE i.blockName = :blockName AND i.category = :category AND i.createdAt >= :since")
    long countComplaintsInBlockByCategorySince(@Param("blockName") String blockName,
                                              @Param("category") String category,
                                              @Param("since") ZonedDateTime since);

    List<Issue> findByBlockNameAndCategoryAndCreatedAtGreaterThanEqualOrderByCreatedAtDesc(
            String blockName, String category, ZonedDateTime since);

    long countByReportedById(Long studentId);
    long countByHostelId(Long hostelId);
    long countByHostelIdAndStatus(Long hostelId, IssueStatus status);
    long countByHostelIdAndStatusIn(Long hostelId, List<IssueStatus> statuses);
    long countByAssignedDepartmentIdAndStatus(Long departmentId, IssueStatus status);
    long countByInstituteIdAndAssignedDepartmentIdAndStatusIn(Long instituteId, Long departmentId, List<IssueStatus> statuses);
    long countByInstituteIdAndAssignedDepartmentIdAndStatus(Long instituteId, Long departmentId, IssueStatus status);

    long countByHostelIdAndAssignedStaffIsNotNull(Long hostelId);
    long countByHostelIdAndAssignedStaffIsNull(Long hostelId);
    long countByHostelIdAndWardenViewCountGreaterThan(Long hostelId, Integer count);
    long countByHostelIdAndWardenViewCount(Long hostelId, Integer count);

    @Query("SELECT COALESCE(SUM(i.wardenViewCount), 0) FROM Issue i WHERE i.hostel.id = :hostelId")
    long sumWardenViewCountByHostelId(@Param("hostelId") Long hostelId);
}
