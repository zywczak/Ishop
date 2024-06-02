package com.ztp.ishop.repository;

import com.ztp.ishop.entity.Cooler;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CoolerRepository extends JpaRepository<Cooler, Long> {
    Optional<Cooler> findByProductId(Long productId);
}
