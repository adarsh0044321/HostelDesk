package com.hosteldesk.backend.repository;

import com.hosteldesk.backend.entity.Block;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BlockRepository extends JpaRepository<Block, Long> {
    List<Block> findByHostelId(Long hostelId);
    Optional<Block> findByHostelIdAndName(Long hostelId, String name);
}
