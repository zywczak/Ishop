package com.ztp.ishop.repository;

import com.ztp.ishop.entity.RAM;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RamRepository extends JpaRepository<RAM, Long> {
    Optional<RAM> findByProductId(Long productId);
}
