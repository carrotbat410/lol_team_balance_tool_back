#!/bin/bash

# GitHub Actions에서 전달받은 Docker Hub 사용자 이름을 환경변수로 설정
export DOCKERHUB_USERNAME=$1
if [ -z "$DOCKERHUB_USERNAME" ]; then
    echo "Docker Hub username must be provided as the first argument." >&2
    exit 1
fi

# Blue/Green 그룹 설정
BLUE_SERVICES="blue1 blue2"
GREEN_SERVICES="green1 green2"

BLUE_PORTS=(8081 8082)
GREEN_PORTS=(8083 8084)

# 현재 활성화된 그룹 확인 (blue1 컨테이너 존재 여부로 판단)
EXISTING_BLUE=$(docker compose ps -q blue1)

if [ -n "$EXISTING_BLUE" ]; then
    CURRENT_SERVICES=$BLUE_SERVICES
    NEW_SERVICES=$GREEN_SERVICES
    NEW_PORTS=("${GREEN_PORTS[@]}")
    NEW_UPSTREAM_CONFIG="server green1:8080; server green2:8080;"
    CURRENT_COLOR="blue"
    NEW_COLOR="green"
else
    CURRENT_SERVICES=$GREEN_SERVICES
    NEW_SERVICES=$BLUE_SERVICES
    NEW_PORTS=("${BLUE_PORTS[@]}")
    NEW_UPSTREAM_CONFIG="server blue1:8080; server blue2:8080;"
    CURRENT_COLOR="green"
    NEW_COLOR="blue"
fi

echo "### Current active group is ${CURRENT_COLOR} ###"

# 1. 새 버전의 Docker 이미지 pull
# docker compose.yml에 정의된 모든 서비스의 이미지를 한번에 pull 합니다.
echo "### Pulling new images ###"
docker compose pull

# 2. 새 버전의 컨테이너(그룹) 실행
echo "### Starting ${NEW_COLOR} group ###"
docker compose up -d --no-deps $NEW_SERVICES

# 3. Health check
echo "### Health checking ${NEW_COLOR} group ###"

HEALTHY_COUNT=0
TOTAL_NEW_SERVICES=$(echo $NEW_SERVICES | wc -w)

for i in {1..15}; do
    HEALTHY_COUNT=0
    for PORT in "${NEW_PORTS[@]}"; do
        HEALTH_CHECK_URL="http://127.0.0.1:${PORT}/tmpHealthCheck"
        RESPONSE_CODE=$(curl -s -o /dev/null -w "%{http_code}" $HEALTH_CHECK_URL)
        
        if [ $RESPONSE_CODE -eq 200 ]; then
            echo "Instance on port ${PORT} is healthy."
            HEALTHY_COUNT=$((HEALTHY_COUNT + 1))
        else
            echo "Instance on port ${PORT} is not healthy (HTTP code: $RESPONSE_CODE)."
        fi
    done

    if [ $HEALTHY_COUNT -eq $TOTAL_NEW_SERVICES ]; then
        echo "### All instances in ${NEW_COLOR} group are healthy ###"
        
        # 4. Nginx 설정 변경
        echo "### Switching Nginx to ${NEW_COLOR} group ###"
        echo $NEW_UPSTREAM_CONFIG | sudo tee /home/ubuntu/lol_team_balance_tool_back/nginx/service-url.inc
        
        # 5. Nginx 리로드
        docker compose exec nginx nginx -s reload
        
        # 6. 이전 버전 컨테이너(그룹) 중지
        echo "### Stopping ${CURRENT_COLOR} group ###"
        docker compose stop $CURRENT_SERVICES

        # 7. 사용하지 않는 Docker 이미지 정리 (tag가 <none> 으로 변한 이미지들)
        echo "### Pruning old images ###"
        docker image prune -f
        
        echo "### Deployment successful ###"
        exit 0
    fi
    
    echo "Health check failed (${HEALTHY_COUNT}/${TOTAL_NEW_SERVICES} healthy). Retrying in 10 seconds..."
    sleep 10
done

#확인용 주석
echo "### ${NEW_COLOR} group health check failed. Rolling back. ###"
docker compose stop $NEW_SERVICES
exit 1