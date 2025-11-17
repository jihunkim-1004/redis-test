package com.example.redistest.controller;

import com.example.redistest.model.Location;
import com.example.redistest.service.RedisDataStructureService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Tag(name = "7. Geo", description = "지리적 위치 데이터 (주변 검색, 거리 계산)")
@RestController
@RequestMapping("/api/redis/geo")
@RequiredArgsConstructor
public class GeoRedisController {

    private final RedisDataStructureService redisService;

    @Operation(summary = "지리적 위치 저장", description = "위도/경도 좌표를 저장합니다. (매장 위치, 배달 위치)")
    @PostMapping("/{key}")
    public ResponseEntity<String> addGeoLocation(
            @Parameter(description = "Geo 키", required = true) @PathVariable String key,
            @Parameter(description = "위치 정보", required = true) @RequestBody Location location) {
        redisService.addGeoLocation(key, location.getLongitude(), 
                                    location.getLatitude(), location.getName());
        return ResponseEntity.ok("위치 정보 저장 완료: " + location.getName());
    }

    @Operation(summary = "여러 위치 한번에 저장", description = "여러 개의 위치를 한 번에 저장합니다.")
    @PostMapping("/{key}/batch")
    public ResponseEntity<String> addGeoLocations(
            @Parameter(description = "Geo 키", required = true) @PathVariable String key,
            @Parameter(description = "위치 정보 목록", required = true) @RequestBody List<Location> locations) {
        for (Location location : locations) {
            redisService.addGeoLocation(key, location.getLongitude(), 
                                       location.getLatitude(), location.getName());
        }
        return ResponseEntity.ok("위치 정보 " + locations.size() + "개 저장 완료");
    }

    @Operation(summary = "두 지점 간 거리 계산", description = "두 위치 간의 거리를 계산합니다. (km 단위)")
    @GetMapping("/{key}/distance")
    public ResponseEntity<Map<String, Object>> getDistance(
            @Parameter(description = "Geo 키", required = true) @PathVariable String key,
            @Parameter(description = "첫 번째 위치명", required = true) @RequestParam String member1,
            @Parameter(description = "두 번째 위치명", required = true) @RequestParam String member2) {
        Distance distance = redisService.getDistance(key, member1, member2);
        
        Map<String, Object> response = new HashMap<>();
        response.put("from", member1);
        response.put("to", member2);
        response.put("distance", distance != null ? distance.getValue() : null);
        response.put("unit", "km");
        
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "반경 내 위치 검색", description = "특정 좌표 주변의 위치를 검색합니다. (주변 검색)")
    @GetMapping("/{key}/radius")
    public ResponseEntity<List<Map<String, Object>>> getGeoRadius(
            @Parameter(description = "Geo 키", required = true) @PathVariable String key,
            @Parameter(description = "경도", required = true, example = "126.9780") @RequestParam double longitude,
            @Parameter(description = "위도", required = true, example = "37.5665") @RequestParam double latitude,
            @Parameter(description = "반경 (km)", example = "10") @RequestParam(defaultValue = "10") double radius) {
        
        GeoResults<RedisGeoCommands.GeoLocation<Object>> results = 
            redisService.getGeoRadius(key, longitude, latitude, radius);
        
        List<Map<String, Object>> response = new ArrayList<>();
        
        if (results != null) {
            results.forEach(result -> {
                Map<String, Object> item = new HashMap<>();
                item.put("name", result.getContent().getName());
                item.put("distance", result.getDistance().getValue());
                item.put("longitude", result.getContent().getPoint().getX());
                item.put("latitude", result.getContent().getPoint().getY());
                response.add(item);
            });
        }
        
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "특정 위치 주변 검색", description = "저장된 위치를 기준으로 주변을 검색합니다.")
    @GetMapping("/{key}/radius/member")
    public ResponseEntity<List<Map<String, Object>>> getGeoRadiusByMember(
            @Parameter(description = "Geo 키", required = true) @PathVariable String key,
            @Parameter(description = "기준 위치명", required = true) @RequestParam String member,
            @Parameter(description = "반경 (km)", example = "10") @RequestParam(defaultValue = "10") double radius) {
        
        GeoResults<RedisGeoCommands.GeoLocation<Object>> results = 
            redisService.getGeoRadiusByMember(key, member, radius);
        
        List<Map<String, Object>> response = new ArrayList<>();
        
        if (results != null) {
            results.forEach(result -> {
                Map<String, Object> item = new HashMap<>();
                item.put("name", result.getContent().getName());
                item.put("distance", result.getDistance().getValue());
                item.put("longitude", result.getContent().getPoint().getX());
                item.put("latitude", result.getContent().getPoint().getY());
                response.add(item);
            });
        }
        
        return ResponseEntity.ok(response);
    }
}

