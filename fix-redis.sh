#!/bin/bash

# Redis Cluster 문제 자동 해결 스크립트

echo "=================================="
echo "Redis 문제 해결 스크립트"
echo "=================================="
echo ""

# 현재 Redis 상태 확인
echo "🔍 1. 현재 Redis 상태 확인 중..."
if redis-cli ping &> /dev/null; then
    echo "✅ Redis가 실행 중입니다."
    
    # 클러스터 모드 확인
    CLUSTER_ENABLED=$(redis-cli config get cluster-enabled 2>/dev/null | tail -n 1)
    if [ "$CLUSTER_ENABLED" = "yes" ]; then
        echo "⚠️  Redis가 Cluster 모드로 실행 중입니다."
        echo ""
        echo "해결 방법을 선택하세요:"
        echo "1) Redis를 중지하고 단일 인스턴스로 재시작 (권장)"
        echo "2) 그냥 종료 (수동으로 해결)"
        echo ""
        read -p "선택 (1 또는 2): " choice
        
        if [ "$choice" = "1" ]; then
            echo ""
            echo "📦 2. 기존 Redis 중지 중..."
            
            # Docker Redis 중지
            if docker ps | grep -q redis-test; then
                docker stop redis-test &> /dev/null
                docker rm redis-test &> /dev/null
                echo "✅ Docker Redis 컨테이너 중지 완료"
            fi
            
            # 로컬 Redis 중지
            pkill redis-server &> /dev/null
            echo "✅ Redis 프로세스 중지 완료"
            
            echo ""
            echo "🚀 3. 단일 인스턴스 Redis 시작 중..."
            
            # Docker Compose가 있으면 사용
            if [ -f "docker-compose.yml" ]; then
                docker-compose up -d
                sleep 3
                
                if redis-cli ping &> /dev/null; then
                    echo "✅ Docker Compose로 Redis 시작 완료"
                else
                    echo "❌ Redis 시작 실패"
                    exit 1
                fi
            else
                echo "❌ docker-compose.yml 파일을 찾을 수 없습니다."
                echo "💡 다음 명령어로 수동 실행하세요:"
                echo "   docker run -d --name redis-test -p 6379:6379 redis:7-alpine redis-server --cluster-enabled no"
                exit 1
            fi
            
            echo ""
            echo "✅ 문제 해결 완료!"
            echo ""
            echo "📌 다음 단계:"
            echo "1. Spring Boot 애플리케이션 재시작"
            echo "   ./gradlew bootRun"
            echo ""
            echo "2. Swagger UI 접속"
            echo "   http://localhost:8080/swagger-ui.html"
            
        else
            echo "종료합니다."
            exit 0
        fi
    else
        echo "✅ Redis가 단일 인스턴스 모드로 실행 중입니다."
        echo "✅ 정상 상태입니다!"
    fi
else
    echo "⚠️  Redis가 실행 중이지 않습니다."
    echo ""
    echo "🚀 Redis 시작 중..."
    
    # Docker Compose로 시작
    if [ -f "docker-compose.yml" ]; then
        docker-compose up -d
        sleep 3
        
        if redis-cli ping &> /dev/null; then
            echo "✅ Redis 시작 완료!"
        else
            echo "❌ Redis 시작 실패"
            echo "💡 수동으로 실행해보세요:"
            echo "   docker-compose up -d"
            exit 1
        fi
    else
        echo "❌ docker-compose.yml 파일을 찾을 수 없습니다."
        echo "💡 다음 명령어로 수동 실행하세요:"
        echo "   docker run -d --name redis-test -p 6379:6379 redis:7-alpine"
        exit 1
    fi
fi

echo ""
echo "=================================="
echo "✨ 모든 작업 완료!"
echo "=================================="

