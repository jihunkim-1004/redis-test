# 문제 해결 가이드

## ❌ MOVED 6918 127.0.0.1:6380 에러

### 증상
```
io.lettuce.core.RedisCommandExecutionException: MOVED 6918 127.0.0.1:6380
```

### 원인
Redis가 **Cluster 모드**로 실행 중이지만, 애플리케이션이 **단일 인스턴스** 설정으로 연결을 시도하고 있습니다.

### 해결 방법 1: Redis를 단일 인스턴스로 실행 (권장)

로컬 개발 환경에서는 단일 인스턴스가 더 간단합니다.

#### 1-1. 기존 Redis 중지
```bash
# Docker 컨테이너 중지 및 삭제
docker stop redis-test
docker rm redis-test

# 또는 모든 Redis 프로세스 중지
pkill redis-server
```

#### 1-2. Redis 데이터 정리 (선택사항)
```bash
# Redis 클러스터 데이터 삭제
rm -rf /usr/local/var/db/redis/nodes.conf
rm -rf /usr/local/var/db/redis/appendonly.aof
```

#### 1-3. Docker Compose로 단일 인스턴스 실행
```bash
# 프로젝트 루트에서
docker-compose up -d

# Redis 연결 확인
redis-cli ping
# 응답: PONG
```

#### 1-4. 또는 직접 Redis 실행
```bash
# 단일 인스턴스 모드로 실행
redis-server --port 6379 --cluster-enabled no

# 별도 터미널에서 확인
redis-cli ping
```

### 해결 방법 2: Cluster 모드 지원 활성화

Redis Cluster를 계속 사용하고 싶다면:

#### 2-1. application.yml 수정
```yaml
spring:
  data:
    redis:
      # 단일 인스턴스 설정 주석 처리
      # host: localhost
      # port: 6379
      
      # Cluster 설정 활성화
      cluster:
        nodes:
          - 127.0.0.1:6379
          - 127.0.0.1:6380
          - 127.0.0.1:6381
        max-redirects: 3
```

#### 2-2. RedisConfig.java 수정
`RedisConfig.java`에서 Cluster 설정 주석 해제:
```java
@Bean
public LettuceConnectionFactory redisConnectionFactory(RedisProperties redisProperties) {
    RedisClusterConfiguration clusterConfig = new RedisClusterConfiguration(
        redisProperties.getCluster().getNodes()
    );
    
    if (redisProperties.getPassword() != null) {
        clusterConfig.setPassword(redisProperties.getPassword());
    }
    
    return new LettuceConnectionFactory(clusterConfig);
}
```

## ❌ Connection refused 에러

### 증상
```
io.lettuce.core.RedisConnectionException: Unable to connect to localhost:6379
```

### 해결 방법
```bash
# Redis가 실행 중인지 확인
redis-cli ping

# Redis 시작
docker-compose up -d
# 또는
redis-server
```

## ❌ NOAUTH Authentication required

### 증상
```
io.lettuce.core.RedisCommandExecutionException: NOAUTH Authentication required
```

### 해결 방법
application.yml에 비밀번호 추가:
```yaml
spring:
  data:
    redis:
      password: your-redis-password
```

## ❌ Gradle Wrapper 관련 에러

### 증상
```
./gradlew: Permission denied
```

### 해결 방법
```bash
chmod +x gradlew
```

## 🔍 Redis 상태 확인 명령어

```bash
# Redis 실행 확인
redis-cli ping

# Redis 정보 확인
redis-cli info

# 클러스터 모드 확인
redis-cli cluster info

# 단일 모드인지 확인 (cluster_enabled:0 이면 단일 모드)
redis-cli config get cluster-enabled

# 모든 키 확인
redis-cli KEYS "*"

# Redis 버전 확인
redis-cli --version
```

## 🐳 Docker 관련 명령어

```bash
# 컨테이너 상태 확인
docker ps

# Redis 로그 확인
docker logs redis-test

# Redis 컨테이너 재시작
docker restart redis-test

# 컨테이너 완전 재생성
docker-compose down -v
docker-compose up -d
```

## 💡 개발 팁

### 로컬 개발 환경
- **단일 인스턴스** 사용 권장 (간단, 빠름)
- Docker Compose 사용으로 일관된 환경 유지

### 프로덕션 환경
- **Cluster 모드** 사용 권장 (고가용성, 확장성)
- Redis Sentinel 또는 Redis Cluster 구성

### 테스트 시 주의사항
- 각 테스트마다 `FLUSHALL`로 데이터 초기화
- 테스트용 별도 Redis 인스턴스 사용
- TTL이 있는 키는 시간 주의

## 📚 추가 참고 자료

- [Spring Data Redis 공식 문서](https://spring.io/projects/spring-data-redis)
- [Redis Cluster 튜토리얼](https://redis.io/docs/manual/scaling/)
- [Lettuce 공식 문서](https://lettuce.io/)

