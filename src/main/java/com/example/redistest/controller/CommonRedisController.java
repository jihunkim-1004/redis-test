package com.example.redistest.controller;

import com.example.redistest.service.RedisDataStructureService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Tag(name = "8. 공통 작업", description = "키 관리, TTL 설정, 삭제 등 공통 작업")
@RestController
@RequestMapping("/api/redis/key")
@RequiredArgsConstructor
public class CommonRedisController {

    private final RedisDataStructureService redisService;

    @Operation(summary = "키 존재 여부 확인", description = "특정 키가 Redis에 존재하는지 확인합니다.")
    @GetMapping("/{key}/exists")
    public ResponseEntity<Map<String, Object>> hasKey(
            @Parameter(description = "확인할 키", required = true) @PathVariable String key) {
        Boolean exists = redisService.hasKey(key);
        
        Map<String, Object> response = new HashMap<>();
        response.put("key", key);
        response.put("exists", exists);
        
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "TTL 설정", description = "키의 만료 시간(TTL)을 설정합니다.")
    @PostMapping("/{key}/expire")
    public ResponseEntity<String> setExpire(
            @Parameter(description = "키", required = true) @PathVariable String key,
            @Parameter(description = "만료 시간(초)", required = true, example = "60") @RequestParam long seconds) {
        Boolean success = redisService.expire(key, seconds, TimeUnit.SECONDS);
        return ResponseEntity.ok(success ? "TTL 설정 완료 (" + seconds + "초)" : "실패 (키가 존재하지 않음)");
    }

    @Operation(summary = "TTL 조회", description = "키의 남은 만료 시간을 조회합니다. (-1: 만료 없음, -2: 키 없음)")
    @GetMapping("/{key}/ttl")
    public ResponseEntity<Map<String, Object>> getTTL(
            @Parameter(description = "키", required = true) @PathVariable String key) {
        Long ttl = redisService.getExpire(key);
        
        Map<String, Object> response = new HashMap<>();
        response.put("key", key);
        response.put("ttl", ttl);
        response.put("unit", "seconds");
        
        String status;
        if (ttl == -1) {
            status = "만료 시간 없음";
        } else if (ttl == -2) {
            status = "키가 존재하지 않음";
        } else {
            status = "만료까지 " + ttl + "초 남음";
        }
        response.put("status", status);
        
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "키 삭제", description = "Redis에서 특정 키를 삭제합니다.")
    @DeleteMapping("/{key}")
    public ResponseEntity<Map<String, Object>> deleteKey(
            @Parameter(description = "삭제할 키", required = true) @PathVariable String key) {
        Boolean deleted = redisService.deleteKey(key);
        
        Map<String, Object> response = new HashMap<>();
        response.put("key", key);
        response.put("deleted", deleted);
        response.put("message", deleted ? "키 삭제 완료" : "키가 존재하지 않음");
        
        return ResponseEntity.ok(response);
    }
}

