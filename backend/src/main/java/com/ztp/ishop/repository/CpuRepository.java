package com.ztp.ishop.repository;

import com.ztp.ishop.entity.CPU;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CpuRepository extends JpaRepository<CPU, Long> {
    Optional<CPU> findByProductId(Long productId);
}