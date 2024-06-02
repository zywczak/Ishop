package com.ztp.ishop.repository;

import com.ztp.ishop.entity.Motherboard;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MotherboardRepository extends JpaRepository<Motherboard, Long> {
    Optional<Motherboard> findByProductId(Long productId);
}
