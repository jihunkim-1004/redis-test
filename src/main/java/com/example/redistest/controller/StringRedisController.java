package com.example.redistest.controller;

import com.example.redistest.service.RedisDataStructureService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.TimeUnit;

@Tag(name = "1. String", description = "기본적인 Key-Value 저장소 (캐싱, 카운터, 세션)")
@RestController
@RequestMapping("/api/redis/string")
@RequiredArgsConstructor
public class StringRedisController {

    private final RedisDataStructureService redisService;

    @Operation(summary = "String 저장", description = "가장 기본적인 Key-Value 형태로 데이터를 저장합니다.")
    @PostMapping
    public ResponseEntity<String> setString(
            @Parameter(description = "저장할 키", required = true) @RequestParam String key,
            @Parameter(description = "저장할 값", required = true) @RequestParam String value) {
        redisService.setString(key, value);
        return ResponseEntity.ok("String 저장 완료: " + key);
    }

    @Operation(summary = "String 조회", description = "키에 해당하는 값을 조회합니다.")
    @GetMapping("/{key}")
    public ResponseEntity<String> getString(
            @Parameter(description = "조회할 키", required = true) @PathVariable String key) {
        String value = redisService.getString(key);
        return ResponseEntity.ok("값: " + value);
    }

    @Operation(summary = "String 저장 (TTL)", description = "만료 시간(TTL)과 함께 데이터를 저장합니다.")
    @PostMapping("/expire")
    public ResponseEntity<String> setStringWithExpire(
            @Parameter(description = "저장할 키", required = true) @RequestParam String key,
            @Parameter(description = "저장할 값", required = true) @RequestParam String value,
            @Parameter(description = "만료 시간(초)", example = "60") @RequestParam(defaultValue = "60") long seconds) {
        redisService.setStringWithExpire(key, value, seconds, TimeUnit.SECONDS);
        return ResponseEntity.ok("String 저장 완료 (TTL: " + seconds + "초)");
    }

    @Operation(summary = "카운터 증가", description = "숫자 값을 1씩 증가시킵니다. (조회수, 좋아요 수 등)")
    @PostMapping("/increment/{key}")
    public ResponseEntity<Long> increment(
            @Parameter(description = "증가시킬 키", required = true) @PathVariable String key) {
        Long result = redisService.increment(key);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "카운터 증가 (값 지정)", description = "숫자 값을 지정한 만큼 증가시킵니다.")
    @PostMapping("/increment/{key}/by")
    public ResponseEntity<Long> incrementBy(
            @Parameter(description = "증가시킬 키", required = true) @PathVariable String key,
            @Parameter(description = "증가량", required = true, example = "10") @RequestParam long delta) {
        Long result = redisService.incrementBy(key, delta);
        return ResponseEntity.ok(result);
    }
}

