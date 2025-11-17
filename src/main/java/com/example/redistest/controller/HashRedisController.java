package com.example.redistest.controller;

import com.example.redistest.model.User;
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
import java.util.Set;

@Tag(name = "5. Hash", description = "필드-값 쌍의 맵 (사용자 프로필, 상품 정보, 세션)")
@RestController
@RequestMapping("/api/redis/hash")
@RequiredArgsConstructor
public class HashRedisController {

    private final RedisDataStructureService redisService;

    @Operation(summary = "Hash 필드 저장", description = "Hash의 특정 필드에 값을 저장합니다.")
    @PostMapping("/{key}/field")
    public ResponseEntity<String> setHashField(
            @Parameter(description = "Hash 키", required = true) @PathVariable String key,
            @Parameter(description = "필드명", required = true) @RequestParam String field,
            @Parameter(description = "필드값", required = true) @RequestParam String value) {
        redisService.setHash(key, field, value);
        return ResponseEntity.ok("Hash 필드 저장 완료");
    }

    @Operation(summary = "Hash로 사용자 정보 저장", description = "사용자 객체를 Hash로 저장합니다. (프로필, 세션)")
    @PostMapping("/{key}/user")
    public ResponseEntity<String> setHashUser(
            @Parameter(description = "Hash 키 (예: user:1)", required = true) @PathVariable String key,
            @Parameter(description = "사용자 정보", required = true) @RequestBody User user) {
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("id", user.getId());
        userMap.put("name", user.getName());
        userMap.put("email", user.getEmail());
        userMap.put("age", user.getAge());
        
        redisService.setHashAll(key, userMap);
        return ResponseEntity.ok("사용자 정보 저장 완료");
    }

    @Operation(summary = "Hash 전체 저장", description = "여러 필드를 한 번에 저장합니다.")
    @PostMapping("/{key}")
    public ResponseEntity<String> setHashAll(
            @Parameter(description = "Hash 키", required = true) @PathVariable String key,
            @Parameter(description = "필드-값 맵", required = true) @RequestBody Map<String, Object> data) {
        redisService.setHashAll(key, data);
        return ResponseEntity.ok("Hash 전체 저장 완료");
    }

    @Operation(summary = "Hash 전체 조회", description = "Hash의 모든 필드와 값을 조회합니다.")
    @GetMapping("/{key}")
    public ResponseEntity<Map<Object, Object>> getHash(
            @Parameter(description = "Hash 키", required = true) @PathVariable String key) {
        Map<Object, Object> hash = redisService.getHashAll(key);
        return ResponseEntity.ok(hash);
    }

    @Operation(summary = "Hash 필드 조회", description = "Hash의 특정 필드 값을 조회합니다.")
    @GetMapping("/{key}/field")
    public ResponseEntity<Object> getHashField(
            @Parameter(description = "Hash 키", required = true) @PathVariable String key,
            @Parameter(description = "필드명", required = true) @RequestParam String field) {
        Object value = redisService.getHashField(key, field);
        return ResponseEntity.ok(value);
    }

    @Operation(summary = "Hash 키 목록 조회", description = "Hash의 모든 필드명을 조회합니다.")
    @GetMapping("/{key}/keys")
    public ResponseEntity<Set<Object>> getHashKeys(
            @Parameter(description = "Hash 키", required = true) @PathVariable String key) {
        Set<Object> keys = redisService.getHashKeys(key);
        return ResponseEntity.ok(keys);
    }

    @Operation(summary = "Hash 값 목록 조회", description = "Hash의 모든 값을 조회합니다.")
    @GetMapping("/{key}/values")
    public ResponseEntity<List<Object>> getHashValues(
            @Parameter(description = "Hash 키", required = true) @PathVariable String key) {
        List<Object> values = redisService.getHashValues(key);
        return ResponseEntity.ok(values);
    }

    @Operation(summary = "Hash 필드 존재 확인", description = "Hash에 특정 필드가 있는지 확인합니다.")
    @GetMapping("/{key}/exists")
    public ResponseEntity<Boolean> hasHashKey(
            @Parameter(description = "Hash 키", required = true) @PathVariable String key,
            @Parameter(description = "필드명", required = true) @RequestParam String field) {
        Boolean exists = redisService.hasHashKey(key, field);
        return ResponseEntity.ok(exists);
    }
}

