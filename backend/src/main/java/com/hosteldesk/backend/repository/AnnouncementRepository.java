package com.hosteldesk.backend.repository;

import com.hosteldesk.backend.entity.Announcement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.List;

@Repository
public interface AnnouncementRepository extends JpaRepository<Announcement, Long> {

    @Query("SELECT a FROM Announcement a WHERE a.institute.id = :instituteId AND (a.hostel.id = :hostelId OR a.hostel IS NULL) AND (a.expiresAt IS NULL OR a.expiresAt > :now) ORDER BY a.pinned DESC, a.createdAt DESC")
    List<Announcement> findActiveForStudent(
            @Param("instituteId") Long instituteId,
            @Param("hostelId") Long hostelId,
            @Param("now") ZonedDateTime now
    );

    @Query("SELECT a FROM Announcement a WHERE a.institute.id = :instituteId AND (a.expiresAt IS NULL OR a.expiresAt > :now) ORDER BY a.pinned DESC, a.createdAt DESC")
    List<Announcement> findActiveForInstitute(
            @Param("instituteId") Long instituteId,
            @Param("now") ZonedDateTime now
    );

    List<Announcement> findByInstituteIdOrderByCreatedAtDesc(Long instituteId);

    List<Announcement> findByHostelIdOrderByCreatedAtDesc(Long hostelId);
}
