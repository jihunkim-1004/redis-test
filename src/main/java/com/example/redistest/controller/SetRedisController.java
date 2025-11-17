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
import java.util.Set;

@Tag(name = "3. Set", description = "중복이 없는 집합 (태그, 팔로워 관계, 중복 제거)")
@RestController
@RequestMapping("/api/redis/set")
@RequiredArgsConstructor
public class SetRedisController {

    private final RedisDataStructureService redisService;

    @Operation(summary = "Set에 항목 추가", description = "중복이 없는 Set에 항목을 추가합니다. (태그, 팔로워 관계)")
    @PostMapping("/{key}")
    public ResponseEntity<String> addToSet(
            @Parameter(description = "Set 키", required = true) @PathVariable String key,
            @Parameter(description = "추가할 항목들 (중복 자동 제거)", required = true) @RequestBody List<String> values) {
        redisService.addToSet(key, values.toArray());
        return ResponseEntity.ok("Set에 " + values.size() + "개 항목 추가 완료");
    }

    @Operation(summary = "Set 조회", description = "Set의 모든 멤버를 조회합니다.")
    @GetMapping("/{key}")
    public ResponseEntity<Map<String, Object>> getSet(
            @Parameter(description = "Set 키", required = true) @PathVariable String key) {
        Set<Object> members = redisService.getSetMembers(key);
        Long size = redisService.getSetSize(key);
        
        Map<String, Object> response = new HashMap<>();
        response.put("size", size);
        response.put("members", members);
        
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Set 멤버십 확인", description = "특정 값이 Set에 포함되어 있는지 확인합니다.")
    @GetMapping("/{key}/contains")
    public ResponseEntity<Boolean> isSetMember(
            @Parameter(description = "Set 키", required = true) @PathVariable String key,
            @Parameter(description = "확인할 값", required = true) @RequestParam String value) {
        Boolean isMember = redisService.isMemberOfSet(key, value);
        return ResponseEntity.ok(isMember);
    }

    @Operation(summary = "Set 크기 조회", description = "Set의 멤버 개수를 조회합니다.")
    @GetMapping("/{key}/size")
    public ResponseEntity<Long> getSetSize(
            @Parameter(description = "Set 키", required = true) @PathVariable String key) {
        Long size = redisService.getSetSize(key);
        return ResponseEntity.ok(size);
    }

    @Operation(summary = "Set 교집합", description = "두 Set의 교집합을 반환합니다.")
    @GetMapping("/intersect")
    public ResponseEntity<Set<Object>> setIntersect(
            @Parameter(description = "첫 번째 Set 키", required = true) @RequestParam String key1,
            @Parameter(description = "두 번째 Set 키", required = true) @RequestParam String key2) {
        Set<Object> result = redisService.setIntersect(key1, key2);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "Set 합집합", description = "두 Set의 합집합을 반환합니다.")
    @GetMapping("/union")
    public ResponseEntity<Set<Object>> setUnion(
            @Parameter(description = "첫 번째 Set 키", required = true) @RequestParam String key1,
            @Parameter(description = "두 번째 Set 키", required = true) @RequestParam String key2) {
        Set<Object> result = redisService.setUnion(key1, key2);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "Set 차집합", description = "두 Set의 차집합을 반환합니다.")
    @GetMapping("/difference")
    public ResponseEntity<Set<Object>> setDifference(
            @Parameter(description = "첫 번째 Set 키", required = true) @RequestParam String key1,
            @Parameter(description = "두 번째 Set 키", required = true) @RequestParam String key2) {
        Set<Object> result = redisService.setDifference(key1, key2);
        return ResponseEntity.ok(result);
    }
}

