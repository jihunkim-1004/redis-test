package com.example.redistest.controller;

import com.example.redistest.service.RedisDataStructureService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Tag(name = "6. HyperLogLog", description = "대용량 고유 값 카운팅 (순 방문자 수, UV)")
@RestController
@RequestMapping("/api/redis/hyperloglog")
@RequiredArgsConstructor
public class HyperLogLogRedisController {

    private final RedisDataStructureService redisService;

    @Operation(summary = "HyperLogLog에 항목 추가", description = "고유 값 카운팅을 위해 항목을 추가합니다. (순 방문자 수, UV)")
    @PostMapping("/{key}")
    public ResponseEntity<Map<String, Object>> addToHyperLogLog(
            @Parameter(description = "HyperLogLog 키", required = true) @PathVariable String key,
            @Parameter(description = "추가할 항목들", required = true) @RequestBody List<String> values) {
        Long addedCount = redisService.addToHyperLogLog(key, values.toArray());
        Long totalCount = redisService.getHyperLogLogSize(key);
        
        Map<String, Object> response = new HashMap<>();
        response.put("addedCount", addedCount);
        response.put("totalUniqueCount", totalCount);
        
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "HyperLogLog 카운트 조회", description = "고유 값의 개수를 추정합니다. (메모리 효율적)")
    @GetMapping("/{key}/count")
    public ResponseEntity<Map<String, Long>> getHyperLogLogCount(
            @Parameter(description = "HyperLogLog 키", required = true) @PathVariable String key) {
        Long count = redisService.getHyperLogLogSize(key);
        
        Map<String, Long> response = new HashMap<>();
        response.put("uniqueCount", count);
        
        return ResponseEntity.ok(response);
    }
}

