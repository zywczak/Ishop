package com.ztp.ishop.repository;

import com.ztp.ishop.entity.Basket;
import com.ztp.ishop.entity.Product;
import com.ztp.ishop.entity.User;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface BasketRepository extends JpaRepository<Basket, Long> {
    List<Basket> findByUserId(Long userId);
    void deleteByUserId(Long id);
    Basket findByUserAndProduct(User user, Product product);
}