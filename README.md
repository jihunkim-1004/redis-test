# Spring Redis 자료구조 예제 프로젝트

Spring Data Redis에서 제공하는 다양한 자료구조를 활용하는 예제 프로젝트입니다.

## 📋 지원하는 Redis 자료구조

1. **String** - 기본적인 Key-Value 저장
2. **List** - 순서가 있는 리스트
3. **Set** - 중복이 없는 집합
4. **Sorted Set** - 점수로 정렬되는 집합
5. **Hash** - 필드-값 쌍의 맵 (객체 저장에 유용)
6. **HyperLogLog** - 대용량 고유 값 카운팅
7. **Geo** - 지리적 위치 데이터

## 🚀 시작하기

### 필요 사항

- Java 17 이상
- Gradle 8.5 이상 (또는 Gradle Wrapper 사용)
- Redis 서버 (localhost:6379)

### Redis 서버 실행

```bash
# Docker를 사용하는 경우
docker run -d --name redis -p 6379:6379 redis:latest

# 또는 로컬에 설치된 Redis 실행
redis-server
```

### 애플리케이션 실행

```bash
# Gradle Wrapper로 빌드 및 실행
./gradlew clean build
./gradlew bootRun

# 또는 빌드된 JAR 실행
./gradlew bootJar
java -jar build/libs/redis-test-1.0.0.jar
```

서버가 시작되면 `http://localhost:8080` 에서 접근 가능합니다.

## 📖 Swagger UI로 API 문서 보기

애플리케이션을 실행한 후 브라우저에서 아래 URL에 접속하세요:

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/api-docs

Swagger UI에서 모든 API를 쉽게 테스트하고 문서를 확인할 수 있습니다! 🎉

## 📚 API 사용 예제

자료구조별로 분리된 8개의 컨트롤러를 제공합니다:
- **String**: `/api/redis/string/*`
- **List**: `/api/redis/list/*`
- **Set**: `/api/redis/set/*`
- **Sorted Set**: `/api/redis/sortedset/*`
- **Hash**: `/api/redis/hash/*`
- **HyperLogLog**: `/api/redis/hyperloglog/*`
- **Geo**: `/api/redis/geo/*`
- **공통 작업**: `/api/redis/key/*`

### 1. String 자료구조

```bash
# String 저장
curl -X POST "http://localhost:8080/api/redis/string?key=mykey&value=hello"

# String 조회
curl -X GET "http://localhost:8080/api/redis/string/mykey"

# TTL과 함께 String 저장 (60초 후 만료)
curl -X POST "http://localhost:8080/api/redis/string/expire?key=tempkey&value=temporary&seconds=60"

# 숫자 증가
curl -X POST "http://localhost:8080/api/redis/string/increment/counter"
```

### 2. List 자료구조

```bash
# List에 항목 추가
curl -X POST "http://localhost:8080/api/redis/list/mylist" \
  -H "Content-Type: application/json" \
  -d '["항목1", "항목2", "항목3"]'

# List 조회
curl -X GET "http://localhost:8080/api/redis/list/mylist"

# List에서 항목 꺼내기 (LPOP)
curl -X DELETE "http://localhost:8080/api/redis/list/mylist/pop"
```

### 3. Set 자료구조

```bash
# Set에 항목 추가
curl -X POST "http://localhost:8080/api/redis/set/myset" \
  -H "Content-Type: application/json" \
  -d '["apple", "banana", "orange", "apple"]'

# Set 조회 (중복 제거됨)
curl -X GET "http://localhost:8080/api/redis/set/myset"

# Set 멤버십 확인
curl -X GET "http://localhost:8080/api/redis/set/myset/contains?value=apple"

# 두 Set의 교집합
curl -X GET "http://localhost:8080/api/redis/set/intersect?key1=set1&key2=set2"

# 두 Set의 합집합
curl -X GET "http://localhost:8080/api/redis/set/union?key1=set1&key2=set2"
```

### 4. Sorted Set 자료구조 (점수 기반 정렬)

```bash
# Sorted Set에 항목 추가 (점수와 함께)
curl -X POST "http://localhost:8080/api/redis/sortedset/leaderboard?value=player1&score=100"
curl -X POST "http://localhost:8080/api/redis/sortedset/leaderboard?value=player2&score=200"
curl -X POST "http://localhost:8080/api/redis/sortedset/leaderboard?value=player3&score=150"

# 점수 순으로 조회 (오름차순)
curl -X GET "http://localhost:8080/api/redis/sortedset/leaderboard/range?start=0&end=9"

# 점수 순으로 조회 (내림차순)
curl -X GET "http://localhost:8080/api/redis/sortedset/leaderboard/reverse?start=0&end=9"

# 특정 항목의 순위와 점수 조회
curl -X GET "http://localhost:8080/api/redis/sortedset/leaderboard/rank?value=player2"
```

### 5. Hash 자료구조 (객체 저장)

```bash
# Hash 필드 저장
curl -X POST "http://localhost:8080/api/redis/hash/myuser/field?field=name&value=홍길동"

# 사용자 객체 전체 저장
curl -X POST "http://localhost:8080/api/redis/hash/user:1/user" \
  -H "Content-Type: application/json" \
  -d '{
    "id": "1",
    "name": "김철수",
    "email": "kim@example.com",
    "age": 30
  }'

# Hash 전체 조회
curl -X GET "http://localhost:8080/api/redis/hash/user:1"

# Hash 특정 필드 조회
curl -X GET "http://localhost:8080/api/redis/hash/user:1/field?field=name"

# Hash의 모든 키 조회
curl -X GET "http://localhost:8080/api/redis/hash/user:1/keys"
```

### 6. HyperLogLog (고유 방문자 카운팅)

```bash
# HyperLogLog에 방문자 추가
curl -X POST "http://localhost:8080/api/redis/hyperloglog/unique-visitors" \
  -H "Content-Type: application/json" \
  -d '["user1", "user2", "user3", "user1"]'

# 고유 방문자 수 조회 (중복 제거된 카운트)
curl -X GET "http://localhost:8080/api/redis/hyperloglog/unique-visitors/count"
```

### 7. Geo (지리적 위치 데이터)

```bash
# 위치 정보 저장
curl -X POST "http://localhost:8080/api/redis/geo/cities" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "서울",
    "longitude": 126.9780,
    "latitude": 37.5665
  }'

curl -X POST "http://localhost:8080/api/redis/geo/cities" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "부산",
    "longitude": 129.0756,
    "latitude": 35.1796
  }'

# 두 지점 간 거리 계산
curl -X GET "http://localhost:8080/api/redis/geo/cities/distance?member1=서울&member2=부산"

# 특정 좌표 주변 10km 이내의 위치 검색
curl -X GET "http://localhost:8080/api/redis/geo/cities/radius?longitude=126.9780&latitude=37.5665&radius=10"

# 특정 위치 주변의 다른 위치 검색
curl -X GET "http://localhost:8080/api/redis/geo/cities/radius/member?member=서울&radius=100"
```

### 8. 공통 작업

```bash
# 키 존재 여부 확인
curl -X GET "http://localhost:8080/api/redis/key/mykey/exists"

# 키에 TTL 설정 (60초)
curl -X POST "http://localhost:8080/api/redis/key/mykey/expire?seconds=60"

# 키의 남은 TTL 조회
curl -X GET "http://localhost:8080/api/redis/key/mykey/ttl"

# 키 삭제
curl -X DELETE "http://localhost:8080/api/redis/key/mykey"
```

## 🎯 실전 활용 사례

### String
- 캐싱 (API 응답, 세션 등)
- 카운터 (조회수, 좋아요 수)
- 분산 락

### List
- 메시지 큐
- 최근 활동 피드
- 작업 대기열

### Set
- 태그 시스템
- 팔로워/친구 관계
- 중복 제거

### Sorted Set
- 리더보드/랭킹
- 시간 기반 정렬 (타임라인)
- 우선순위 큐

### Hash
- 사용자 프로필
- 상품 정보
- 세션 저장

### HyperLogLog
- 순 방문자 수 (UV)
- 고유 이벤트 카운팅
- 대용량 중복 제거

### Geo
- 주변 검색 (음식점, 매장 등)
- 배달 거리 계산
- 위치 기반 서비스

## 🛠 설정

`src/main/resources/application.yml`에서 Redis 연결 정보를 수정할 수 있습니다:

```yaml
spring:
  data:
    redis:
      host: localhost
      port: 6379
      password: yourpassword  # 필요시
```

## 📝 프로젝트 구조

```
src/main/java/com/example/redistest/
├── RedisTestApplication.java          # 메인 애플리케이션
├── config/
│   ├── RedisConfig.java               # Redis 설정
│   └── SwaggerConfig.java             # Swagger 설정
├── controller/
│   ├── StringRedisController.java     # String 자료구조 API
│   ├── ListRedisController.java       # List 자료구조 API
│   ├── SetRedisController.java        # Set 자료구조 API
│   ├── SortedSetRedisController.java  # Sorted Set 자료구조 API
│   ├── HashRedisController.java       # Hash 자료구조 API
│   ├── HyperLogLogRedisController.java # HyperLogLog 자료구조 API
│   ├── GeoRedisController.java        # Geo 자료구조 API
│   └── CommonRedisController.java     # 공통 작업 API
├── service/
│   └── RedisDataStructureService.java # 비즈니스 로직
└── model/
    ├── User.java                      # 사용자 모델
    └── Location.java                  # 위치 모델
```

## 💡 추가 기능

- 모든 API는 RESTful 방식으로 설계
- Lombok을 사용한 간결한 코드
- 로깅을 통한 디버깅 지원
- JSON 직렬화/역직렬화 지원

## 🔍 Redis CLI로 확인하기

```bash
# Redis CLI 접속
redis-cli

# 모든 키 확인
KEYS *

# 특정 키의 타입 확인
TYPE mykey

# String 조회
GET mykey

# List 조회
LRANGE mylist 0 -1

# Set 조회
SMEMBERS myset

# Hash 조회
HGETALL user:1

# Sorted Set 조회
ZRANGE leaderboard 0 -1 WITHSCORES
```

## 🔧 문제 해결

### ❌ MOVED 에러 발생 시

```
io.lettuce.core.RedisCommandExecutionException: MOVED 6918 127.0.0.1:6380
```

**원인**: Redis가 Cluster 모드로 실행 중입니다.

**해결 방법** (자동):
```bash
./fix-redis.sh
```

**해결 방법** (수동):
```bash
# 1. 기존 Redis 중지
docker stop redis-test && docker rm redis-test

# 2. 단일 인스턴스로 재시작
docker-compose up -d

# 3. 애플리케이션 재시작
./gradlew bootRun
```

상세한 문제 해결 가이드는 [TROUBLESHOOTING.md](TROUBLESHOOTING.md)를 참고하세요.

## 📖 참고 자료

- [Spring Data Redis 공식 문서](https://spring.io/projects/spring-data-redis)
- [Redis 공식 문서](https://redis.io/docs/)
- [Redis 명령어 참조](https://redis.io/commands/)
- test

