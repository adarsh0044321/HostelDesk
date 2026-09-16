package com.hosteldesk.backend.repository;

import com.hosteldesk.backend.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {
    Optional<Department> findByName(String name);
    java.util.List<Department> findByInstituteId(Long instituteId);
    Optional<Department> findByInstituteIdAndName(Long instituteId, String name);
    long countByInstituteId(Long instituteId);
}
