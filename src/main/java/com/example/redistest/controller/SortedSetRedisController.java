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
import java.util.Set;

@Tag(name = "4. Sorted Set", description = "점수로 정렬되는 집합 (리더보드, 랭킹, 타임라인)")
@RestController
@RequestMapping("/api/redis/sortedset")
@RequiredArgsConstructor
public class SortedSetRedisController {

    private final RedisDataStructureService redisService;

    @Operation(summary = "Sorted Set에 항목 추가", description = "점수와 함께 항목을 추가합니다. (리더보드, 랭킹)")
    @PostMapping("/{key}")
    public ResponseEntity<String> addToSortedSet(
            @Parameter(description = "Sorted Set 키", required = true) @PathVariable String key,
            @Parameter(description = "추가할 값", required = true) @RequestParam String value,
            @Parameter(description = "점수", required = true, example = "100.0") @RequestParam double score) {
        redisService.addToSortedSet(key, value, score);
        return ResponseEntity.ok("Sorted Set에 추가 완료 (score: " + score + ")");
    }

    @Operation(summary = "Sorted Set 조회 (오름차순)", description = "점수가 낮은 순서대로 조회합니다.")
    @GetMapping("/{key}/range")
    public ResponseEntity<Set<Object>> getSortedSetRange(
            @Parameter(description = "Sorted Set 키", required = true) @PathVariable String key,
            @Parameter(description = "시작 인덱스", example = "0") @RequestParam(defaultValue = "0") long start,
            @Parameter(description = "끝 인덱스", example = "9") @RequestParam(defaultValue = "9") long end) {
        Set<Object> result = redisService.getSortedSetRange(key, start, end);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "Sorted Set 조회 (내림차순)", description = "점수가 높은 순서대로 조회합니다. (Top 10 등)")
    @GetMapping("/{key}/reverse")
    public ResponseEntity<Set<Object>> getSortedSetReverseRange(
            @Parameter(description = "Sorted Set 키", required = true) @PathVariable String key,
            @Parameter(description = "시작 인덱스", example = "0") @RequestParam(defaultValue = "0") long start,
            @Parameter(description = "끝 인덱스", example = "9") @RequestParam(defaultValue = "9") long end) {
        Set<Object> result = redisService.getSortedSetReverseRange(key, start, end);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "Sorted Set 순위 조회", description = "특정 항목의 순위와 점수를 조회합니다.")
    @GetMapping("/{key}/rank")
    public ResponseEntity<Map<String, Object>> getSortedSetRank(
            @Parameter(description = "Sorted Set 키", required = true) @PathVariable String key,
            @Parameter(description = "조회할 값", required = true) @RequestParam String value) {
        Long rank = redisService.getSortedSetRank(key, value);
        Double score = redisService.getSortedSetScore(key, value);
        
        Map<String, Object> response = new HashMap<>();
        response.put("value", value);
        response.put("rank", rank);
        response.put("score", score);
        
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Sorted Set 점수 조회", description = "특정 항목의 점수를 조회합니다.")
    @GetMapping("/{key}/score")
    public ResponseEntity<Double> getSortedSetScore(
            @Parameter(description = "Sorted Set 키", required = true) @PathVariable String key,
            @Parameter(description = "조회할 값", required = true) @RequestParam String value) {
        Double score = redisService.getSortedSetScore(key, value);
        return ResponseEntity.ok(score);
    }
}

