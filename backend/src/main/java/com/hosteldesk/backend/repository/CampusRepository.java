package com.hosteldesk.backend.repository;

import com.hosteldesk.backend.entity.Campus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CampusRepository extends JpaRepository<Campus, Long> {
    List<Campus> findByInstituteId(Long instituteId);
}
