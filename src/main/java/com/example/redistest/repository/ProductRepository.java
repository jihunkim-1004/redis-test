package com.example.redistest.repository;

import com.example.redistest.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    
    // 이름으로 검색
    List<Product> findByNameContaining(String name);
    
    // 카테고리로 검색
    List<Product> findByCategory(String category);
    
    // 가격 범위로 검색
    List<Product> findByPriceBetween(java.math.BigDecimal minPrice, java.math.BigDecimal maxPrice);
}

