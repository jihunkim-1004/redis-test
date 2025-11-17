#!/bin/bash

# Spring Redis 자료구조 데모 스크립트
# 애플리케이션이 실행 중이어야 합니다 (http://localhost:8080)

BASE_URL="http://localhost:8080/api/redis"

echo "💡 Tip: Swagger UI에서 편리하게 테스트할 수 있습니다!"
echo "   http://localhost:8080/swagger-ui.html"
echo ""

echo "========================================"
echo "Spring Redis 자료구조 데모"
echo "========================================"
echo ""

# 1. String 예제
echo "📝 1. String 자료구조"
echo "----------------------------------------"
echo "✓ String 저장..."
curl -s -X POST "$BASE_URL/string?key=greeting&value=안녕하세요" | echo $(cat)
echo ""

echo "✓ String 조회..."
curl -s -X GET "$BASE_URL/string/greeting" | echo $(cat)
echo ""

echo "✓ 카운터 증가..."
curl -s -X POST "$BASE_URL/string/increment/visit-count" | echo "방문 횟수: $(cat)"
curl -s -X POST "$BASE_URL/string/increment/visit-count" | echo "방문 횟수: $(cat)"
curl -s -X POST "$BASE_URL/string/increment/visit-count" | echo "방문 횟수: $(cat)"
echo ""
echo ""

# 2. List 예제
echo "📋 2. List 자료구조"
echo "----------------------------------------"
echo "✓ List에 항목 추가..."
curl -s -X POST "$BASE_URL/list/tasks" \
  -H "Content-Type: application/json" \
  -d '["프로젝트 기획", "데이터베이스 설계", "API 개발", "테스트 작성"]' | echo $(cat)
echo ""

echo "✓ List 조회..."
curl -s -X GET "$BASE_URL/list/tasks" | jq .
echo ""
echo ""

# 3. Set 예제
echo "🔢 3. Set 자료구조"
echo "----------------------------------------"
echo "✓ Set에 항목 추가..."
curl -s -X POST "$BASE_URL/set/tags" \
  -H "Content-Type: application/json" \
  -d '["Spring", "Redis", "Java", "Database", "Spring"]' | echo $(cat)
echo ""

echo "✓ Set 조회 (중복 제거됨)..."
curl -s -X GET "$BASE_URL/set/tags" | jq .
echo ""

echo "✓ 멤버십 확인..."
curl -s -X GET "$BASE_URL/set/tags/contains?value=Redis" | echo "Redis가 Set에 있는가? $(cat)"
echo ""
echo ""

# 4. Sorted Set 예제
echo "🏆 4. Sorted Set 자료구조 (리더보드)"
echo "----------------------------------------"
echo "✓ 게임 점수 추가..."
curl -s -X POST "$BASE_URL/sortedset/game-scores?value=김철수&score=1500"
curl -s -X POST "$BASE_URL/sortedset/game-scores?value=이영희&score=2300"
curl -s -X POST "$BASE_URL/sortedset/game-scores?value=박지민&score=1800"
curl -s -X POST "$BASE_URL/sortedset/game-scores?value=최민수&score=2100"
curl -s -X POST "$BASE_URL/sortedset/game-scores?value=정수아&score=1950"
echo ""

echo "✓ 상위 랭킹 조회 (내림차순)..."
curl -s -X GET "$BASE_URL/sortedset/game-scores/reverse?start=0&end=4" | jq .
echo ""

echo "✓ 특정 플레이어 순위 조회..."
curl -s -X GET "$BASE_URL/sortedset/game-scores/rank?value=이영희" | jq .
echo ""
echo ""

# 5. Hash 예제
echo "👤 5. Hash 자료구조 (사용자 정보)"
echo "----------------------------------------"
echo "✓ 사용자 정보 저장..."
curl -s -X POST "$BASE_URL/hash/user:1001/user" \
  -H "Content-Type: application/json" \
  -d '{
    "id": "1001",
    "name": "홍길동",
    "email": "hong@example.com",
    "age": 28
  }' | echo $(cat)
echo ""

curl -s -X POST "$BASE_URL/hash/user:1002/user" \
  -H "Content-Type: application/json" \
  -d '{
    "id": "1002",
    "name": "김영희",
    "email": "kim@example.com",
    "age": 25
  }'
echo ""

echo "✓ 사용자 정보 조회..."
curl -s -X GET "$BASE_URL/hash/user:1001" | jq .
echo ""

echo "✓ 특정 필드만 조회..."
curl -s -X GET "$BASE_URL/hash/user:1001/field?field=name" | echo "이름: $(cat)"
echo ""
echo ""

# 6. HyperLogLog 예제
echo "📊 6. HyperLogLog (고유 방문자 카운팅)"
echo "----------------------------------------"
echo "✓ 방문자 추가 (중복 포함)..."
curl -s -X POST "$BASE_URL/hyperloglog/daily-visitors" \
  -H "Content-Type: application/json" \
  -d '["user1", "user2", "user3", "user4", "user5"]'
echo ""

curl -s -X POST "$BASE_URL/hyperloglog/daily-visitors" \
  -H "Content-Type: application/json" \
  -d '["user1", "user2", "user6", "user7"]'
echo ""

echo "✓ 고유 방문자 수 조회..."
curl -s -X GET "$BASE_URL/hyperloglog/daily-visitors/count" | jq .
echo ""
echo ""

# 7. Geo 예제
echo "🌍 7. Geo 자료구조 (위치 정보)"
echo "----------------------------------------"
echo "✓ 한국 주요 도시 위치 저장..."
curl -s -X POST "$BASE_URL/geo/korea-cities" \
  -H "Content-Type: application/json" \
  -d '{"name": "서울", "longitude": 126.9780, "latitude": 37.5665}'
echo ""

curl -s -X POST "$BASE_URL/geo/korea-cities" \
  -H "Content-Type: application/json" \
  -d '{"name": "부산", "longitude": 129.0756, "latitude": 35.1796}'
echo ""

curl -s -X POST "$BASE_URL/geo/korea-cities" \
  -H "Content-Type: application/json" \
  -d '{"name": "대구", "longitude": 128.6014, "latitude": 35.8714}'
echo ""

curl -s -X POST "$BASE_URL/geo/korea-cities" \
  -H "Content-Type: application/json" \
  -d '{"name": "인천", "longitude": 126.7052, "latitude": 37.4563}'
echo ""

echo "✓ 서울-부산 거리 계산..."
curl -s -X GET "$BASE_URL/geo/korea-cities/distance?member1=서울&member2=부산" | jq .
echo ""

echo "✓ 서울 주변 100km 이내 도시 검색..."
curl -s -X GET "$BASE_URL/geo/korea-cities/radius/member?member=서울&radius=100" | jq .
echo ""
echo ""

# 공통 작업
echo "🔧 8. 공통 작업 (TTL, 키 관리)"
echo "----------------------------------------"
echo "✓ 임시 데이터 저장 (30초 TTL)..."
curl -s -X POST "$BASE_URL/string/expire?key=temp-data&value=임시값&seconds=30" | echo $(cat)
echo ""

echo "✓ TTL 확인..."
curl -s -X GET "$BASE_URL/key/temp-data/ttl" | jq .
echo ""

echo "✓ 키 존재 여부 확인..."
curl -s -X GET "$BASE_URL/key/greeting/exists" | echo "greeting 키 존재: $(cat)"
echo ""

echo ""
echo "========================================"
echo "✅ 데모 완료!"
echo "========================================"
echo ""
echo "💡 Tip: Redis CLI로 직접 확인해보세요!"
echo "   $ redis-cli"
echo "   > KEYS *"
echo "   > GET greeting"
echo "   > LRANGE tasks 0 -1"
echo "   > SMEMBERS tags"
echo "   > ZRANGE game-scores 0 -1 WITHSCORES"
echo "   > HGETALL user:1001"
echo ""


