package com.ztp.ishop.repository;

import com.ztp.ishop.entity.Basket;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BasketRepository extends JpaRepository<Basket, Long> {

    List<Basket> findByUserId(Long userId);

    void deleteByUserId(Long userId);
}
