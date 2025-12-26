package com.example.redistest.controller;

import com.example.redistest.dto.ProductRequest;
import com.example.redistest.entity.Product;
import com.example.redistest.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Tag(name = "Product", description = "상품 관리 API")
public class ProductController {
    
    private final ProductService productService;
    
    @GetMapping
    @Operation(summary = "전체 상품 조회", description = "모든 상품 목록을 조회합니다")
    public ResponseEntity<List<Product>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "상품 조회", description = "ID로 특정 상품을 조회합니다")
    public ResponseEntity<Product> getProduct(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }
    
    @PostMapping
    @Operation(summary = "상품 생성", description = "새로운 상품을 생성합니다")
    public ResponseEntity<Product> createProduct(@RequestBody ProductRequest request) {
        Product product = productService.createProduct(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(product);
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "상품 수정", description = "기존 상품 정보를 수정합니다")
    public ResponseEntity<Product> updateProduct(@PathVariable Long id, @RequestBody ProductRequest request) {
        return ResponseEntity.ok(productService.updateProduct(id, request));
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "상품 삭제", description = "상품을 삭제합니다")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/search")
    @Operation(summary = "상품 이름 검색", description = "상품 이름으로 검색합니다")
    public ResponseEntity<List<Product>> searchByName(@RequestParam String name) {
        return ResponseEntity.ok(productService.searchByName(name));
    }
    
    @GetMapping("/category/{category}")
    @Operation(summary = "카테고리별 조회", description = "카테고리로 상품을 조회합니다")
    public ResponseEntity<List<Product>> getByCategory(@PathVariable String category) {
        return ResponseEntity.ok(productService.searchByCategory(category));
    }
    
    @GetMapping("/price-range")
    @Operation(summary = "가격 범위 검색", description = "최소~최대 가격 범위로 상품을 검색합니다")
    public ResponseEntity<List<Product>> searchByPriceRange(
            @RequestParam BigDecimal minPrice,
            @RequestParam BigDecimal maxPrice) {
        return ResponseEntity.ok(productService.searchByPriceRange(minPrice, maxPrice));
    }
}

