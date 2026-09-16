package com.hosteldesk.backend.repository;

import com.hosteldesk.backend.entity.Institute;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InstituteRepository extends JpaRepository<Institute, Long> {
    Optional<Institute> findByCode(String code);
    boolean existsByCode(String code);
}
