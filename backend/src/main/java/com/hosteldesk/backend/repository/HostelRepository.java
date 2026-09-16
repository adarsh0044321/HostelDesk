package com.hosteldesk.backend.repository;

import com.hosteldesk.backend.entity.Hostel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HostelRepository extends JpaRepository<Hostel, Long> {
    Optional<Hostel> findByName(String name);
    java.util.List<Hostel> findByInstituteId(Long instituteId);
    long countByInstituteId(Long instituteId);
}
