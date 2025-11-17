package com.example.redistest.service;

import com.example.redistest.model.Location;
import com.example.redistest.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.geo.*;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisDataStructureService {

    private final RedisTemplate<String, Object> redisTemplate;

    // ==================== String Operations ====================
    
    /**
     * String: 가장 기본적인 Key-Value 저장
     */
    public void setString(String key, String value) {
        redisTemplate.opsForValue().set(key, value);
        log.debug("String 저장 - key: {}, value: {}", key, value);
    }

    public String getString(String key) {
        Object value = redisTemplate.opsForValue().get(key);
        return value != null ? value.toString() : null;
    }

    /**
     * String with TTL (Time To Live)
     */
    public void setStringWithExpire(String key, String value, long timeout, TimeUnit unit) {
        redisTemplate.opsForValue().set(key, value, timeout, unit);
        log.debug("String 저장 (TTL) - key: {}, value: {}, timeout: {}", key, value, timeout);
    }

    /**
     * Increment/Decrement 연산
     */
    public Long increment(String key) {
        return redisTemplate.opsForValue().increment(key);
    }

    public Long incrementBy(String key, long delta) {
        return redisTemplate.opsForValue().increment(key, delta);
    }

    // ==================== List Operations ====================
    
    /**
     * List: 순서가 있는 문자열 리스트
     */
    public void pushToList(String key, Object... values) {
        redisTemplate.opsForList().rightPushAll(key, values);
        log.debug("List에 추가 - key: {}, values: {}", key, Arrays.toString(values));
    }

    public List<Object> getList(String key, long start, long end) {
        return redisTemplate.opsForList().range(key, start, end);
    }

    public Object popFromList(String key) {
        return redisTemplate.opsForList().leftPop(key);
    }

    public Long getListSize(String key) {
        return redisTemplate.opsForList().size(key);
    }

    // ==================== Set Operations ====================
    
    /**
     * Set: 중복이 없는 집합
     */
    public void addToSet(String key, Object... values) {
        redisTemplate.opsForSet().add(key, values);
        log.debug("Set에 추가 - key: {}, values: {}", key, Arrays.toString(values));
    }

    public Set<Object> getSetMembers(String key) {
        return redisTemplate.opsForSet().members(key);
    }

    public Boolean isMemberOfSet(String key, Object value) {
        return redisTemplate.opsForSet().isMember(key, value);
    }

    public Long getSetSize(String key) {
        return redisTemplate.opsForSet().size(key);
    }

    /**
     * Set 교집합, 합집합, 차집합
     */
    public Set<Object> setIntersect(String key1, String key2) {
        return redisTemplate.opsForSet().intersect(key1, key2);
    }

    public Set<Object> setUnion(String key1, String key2) {
        return redisTemplate.opsForSet().union(key1, key2);
    }

    public Set<Object> setDifference(String key1, String key2) {
        return redisTemplate.opsForSet().difference(key1, key2);
    }

    // ==================== Sorted Set Operations ====================
    
    /**
     * Sorted Set: 점수(score)로 정렬되는 집합
     */
    public void addToSortedSet(String key, Object value, double score) {
        redisTemplate.opsForZSet().add(key, value, score);
        log.debug("Sorted Set에 추가 - key: {}, value: {}, score: {}", key, value, score);
    }

    /**
     * 점수 범위로 조회 (오름차순)
     */
    public Set<Object> getSortedSetRange(String key, long start, long end) {
        return redisTemplate.opsForZSet().range(key, start, end);
    }

    /**
     * 점수 범위로 조회 (내림차순)
     */
    public Set<Object> getSortedSetReverseRange(String key, long start, long end) {
        return redisTemplate.opsForZSet().reverseRange(key, start, end);
    }

    /**
     * 특정 값의 순위 조회
     */
    public Long getSortedSetRank(String key, Object value) {
        return redisTemplate.opsForZSet().rank(key, value);
    }

    /**
     * 특정 값의 점수 조회
     */
    public Double getSortedSetScore(String key, Object value) {
        return redisTemplate.opsForZSet().score(key, value);
    }

    // ==================== Hash Operations ====================
    
    /**
     * Hash: 필드-값 쌍의 맵 (객체 저장에 유용)
     */
    public void setHash(String key, String field, Object value) {
        redisTemplate.opsForHash().put(key, field, value);
        log.debug("Hash 저장 - key: {}, field: {}, value: {}", key, field, value);
    }

    public void setHashAll(String key, Map<String, Object> map) {
        redisTemplate.opsForHash().putAll(key, map);
        log.debug("Hash 전체 저장 - key: {}, map size: {}", key, map.size());
    }

    public Object getHashField(String key, String field) {
        return redisTemplate.opsForHash().get(key, field);
    }

    public Map<Object, Object> getHashAll(String key) {
        return redisTemplate.opsForHash().entries(key);
    }

    public Set<Object> getHashKeys(String key) {
        return redisTemplate.opsForHash().keys(key);
    }

    public List<Object> getHashValues(String key) {
        return redisTemplate.opsForHash().values(key);
    }

    public Boolean hasHashKey(String key, String field) {
        return redisTemplate.opsForHash().hasKey(key, field);
    }

    // ==================== HyperLogLog Operations ====================
    
    /**
     * HyperLogLog: 대용량 고유 값 카운팅 (메모리 효율적)
     * 예: 순 방문자 수 추정
     */
    public Long addToHyperLogLog(String key, Object... values) {
        Long result = redisTemplate.opsForHyperLogLog().add(key, values);
        log.debug("HyperLogLog에 추가 - key: {}, values: {}", key, Arrays.toString(values));
        return result;
    }

    public Long getHyperLogLogSize(String key) {
        return redisTemplate.opsForHyperLogLog().size(key);
    }

    // ==================== Geo Operations ====================
    
    /**
     * Geo: 지리적 위치 데이터 저장 및 검색
     */
    public void addGeoLocation(String key, double longitude, double latitude, String member) {
        redisTemplate.opsForGeo().add(key, new Point(longitude, latitude), member);
        log.debug("Geo 위치 추가 - key: {}, member: {}, lon: {}, lat: {}", 
                  key, member, longitude, latitude);
    }

    /**
     * 두 지점 간의 거리 계산
     */
    public Distance getDistance(String key, String member1, String member2) {
        return redisTemplate.opsForGeo().distance(key, member1, member2, 
                                                  RedisGeoCommands.DistanceUnit.KILOMETERS);
    }

    /**
     * 특정 지점 주변의 위치 검색 (반경 기준)
     */
    public GeoResults<RedisGeoCommands.GeoLocation<Object>> getGeoRadius(
            String key, double longitude, double latitude, double radius) {
        
        Circle circle = new Circle(new Point(longitude, latitude), 
                                  new Distance(radius, Metrics.KILOMETERS));
        
        RedisGeoCommands.GeoRadiusCommandArgs args = 
            RedisGeoCommands.GeoRadiusCommandArgs.newGeoRadiusArgs()
                .includeDistance()
                .includeCoordinates()
                .sortAscending()
                .limit(10);
        
        return redisTemplate.opsForGeo().radius(key, circle, args);
    }

    /**
     * 특정 멤버 주변의 위치 검색
     */
    public GeoResults<RedisGeoCommands.GeoLocation<Object>> getGeoRadiusByMember(
            String key, String member, double radius) {
        
        RedisGeoCommands.GeoRadiusCommandArgs args = 
            RedisGeoCommands.GeoRadiusCommandArgs.newGeoRadiusArgs()
                .includeDistance()
                .includeCoordinates()
                .sortAscending()
                .limit(10);
        
        return redisTemplate.opsForGeo().radius(key, member, 
                                               new Distance(radius, Metrics.KILOMETERS), args);
    }

    // ==================== Common Operations ====================
    
    /**
     * 키 삭제
     */
    public Boolean deleteKey(String key) {
        return redisTemplate.delete(key);
    }

    /**
     * 키 존재 여부 확인
     */
    public Boolean hasKey(String key) {
        return redisTemplate.hasKey(key);
    }

    /**
     * TTL 설정
     */
    public Boolean expire(String key, long timeout, TimeUnit unit) {
        return redisTemplate.expire(key, timeout, unit);
    }

    /**
     * TTL 조회 (초 단위)
     */
    public Long getExpire(String key) {
        return redisTemplate.getExpire(key, TimeUnit.SECONDS);
    }
}


