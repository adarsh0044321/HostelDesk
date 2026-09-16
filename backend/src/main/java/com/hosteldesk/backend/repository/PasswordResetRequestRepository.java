package com.hosteldesk.backend.repository;

import com.hosteldesk.backend.entity.PasswordResetRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PasswordResetRequestRepository extends JpaRepository<PasswordResetRequest, Long> {
    List<PasswordResetRequest> findByInstituteIdAndStatus(Long instituteId, String status);
    List<PasswordResetRequest> findByInstituteId(Long instituteId);
    List<PasswordResetRequest> findByInstituteIdOrderByCreatedAtDesc(Long instituteId);
    List<PasswordResetRequest> findByInstituteIdAndStatusInOrderByCreatedAtDesc(Long instituteId, List<String> statuses);
    List<PasswordResetRequest> findByUser_Id(Long userId);
}
