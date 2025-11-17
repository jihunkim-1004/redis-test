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

@Tag(name = "2. List", description = "순서가 있는 리스트 (메시지 큐, 작업 대기열, 최근 활동)")
@RestController
@RequestMapping("/api/redis/list")
@RequiredArgsConstructor
public class ListRedisController {

    private final RedisDataStructureService redisService;

    @Operation(summary = "List에 항목 추가", description = "순서가 있는 List에 여러 항목을 추가합니다. (메시지 큐, 작업 대기열)")
    @PostMapping("/{key}")
    public ResponseEntity<String> addToList(
            @Parameter(description = "List 키", required = true) @PathVariable String key,
            @Parameter(description = "추가할 항목들", required = true) @RequestBody List<String> values) {
        redisService.pushToList(key, values.toArray());
        return ResponseEntity.ok("List에 " + values.size() + "개 항목 추가 완료");
    }

    @Operation(summary = "List 조회", description = "List의 모든 항목을 조회합니다.")
    @GetMapping("/{key}")
    public ResponseEntity<Map<String, Object>> getList(
            @Parameter(description = "List 키", required = true) @PathVariable String key) {
        List<Object> list = redisService.getList(key, 0, -1);
        Long size = redisService.getListSize(key);
        
        Map<String, Object> response = new HashMap<>();
        response.put("size", size);
        response.put("items", list);
        
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "List 범위 조회", description = "List의 특정 범위 항목을 조회합니다.")
    @GetMapping("/{key}/range")
    public ResponseEntity<List<Object>> getListRange(
            @Parameter(description = "List 키", required = true) @PathVariable String key,
            @Parameter(description = "시작 인덱스", example = "0") @RequestParam(defaultValue = "0") long start,
            @Parameter(description = "끝 인덱스", example = "9") @RequestParam(defaultValue = "9") long end) {
        List<Object> list = redisService.getList(key, start, end);
        return ResponseEntity.ok(list);
    }

    @Operation(summary = "List에서 항목 꺼내기 (LPOP)", description = "List의 첫 번째 항목을 꺼냅니다.")
    @DeleteMapping("/{key}/pop")
    public ResponseEntity<Object> popFromList(
            @Parameter(description = "List 키", required = true) @PathVariable String key) {
        Object value = redisService.popFromList(key);
        return ResponseEntity.ok(value);
    }

    @Operation(summary = "List 크기 조회", description = "List의 항목 개수를 조회합니다.")
    @GetMapping("/{key}/size")
    public ResponseEntity<Long> getListSize(
            @Parameter(description = "List 키", required = true) @PathVariable String key) {
        Long size = redisService.getListSize(key);
        return ResponseEntity.ok(size);
    }
}

