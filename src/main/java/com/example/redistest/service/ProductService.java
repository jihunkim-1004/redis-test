package com.example.redistest.service;

import com.example.redistest.dto.ProductRequest;
import com.example.redistest.entity.Product;
import com.example.redistest.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {
    
    private final ProductRepository productRepository;
    
    // 전체 조회
    @Transactional(readOnly = true)
    public List<Product> getAllProducts() {
        log.info("전체 상품 조회");
        return productRepository.findAll();
    }
    
    // ID로 조회
    @Transactional(readOnly = true)
    public Product getProductById(Long id) {
        log.info("상품 조회: ID={}", id);
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("상품을 찾을 수 없습니다: " + id));
    }
    
    // 생성
    @Transactional
    public Product createProduct(ProductRequest request) {
        log.info("상품 생성: {}", request.getName());
        Product product = new Product();
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStock(request.getStock());
        product.setCategory(request.getCategory());
        return productRepository.save(product);
    }
    
    // 수정
    @Transactional
    public Product updateProduct(Long id, ProductRequest request) {
        log.info("상품 수정: ID={}", id);
        Product product = getProductById(id);
        
        if (request.getName() != null) product.setName(request.getName());
        if (request.getDescription() != null) product.setDescription(request.getDescription());
        if (request.getPrice() != null) product.setPrice(request.getPrice());
        if (request.getStock() != null) product.setStock(request.getStock());
        if (request.getCategory() != null) product.setCategory(request.getCategory());
        
        return productRepository.save(product);
    }
    
    // 삭제
    @Transactional
    public void deleteProduct(Long id) {
        log.info("상품 삭제: ID={}", id);
        Product product = getProductById(id);
        productRepository.delete(product);
    }
    
    // 이름으로 검색
    @Transactional(readOnly = true)
    public List<Product> searchByName(String name) {
        log.info("상품 이름 검색: {}", name);
        return productRepository.findByNameContaining(name);
    }
    
    // 카테고리로 검색
    @Transactional(readOnly = true)
    public List<Product> searchByCategory(String category) {
        log.info("카테고리 검색: {}", category);
        return productRepository.findByCategory(category);
    }
    
    // 가격 범위로 검색
    @Transactional(readOnly = true)
    public List<Product> searchByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        log.info("가격 범위 검색: {} ~ {}", minPrice, maxPrice);
        return productRepository.findByPriceBetween(minPrice, maxPrice);
    }
}

