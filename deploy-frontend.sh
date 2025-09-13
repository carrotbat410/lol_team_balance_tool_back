#!/bin/bash

#set -a: 셸 옵션 중 하나. -a는 "all" 또는 "auto-export"를 의미함.
#set -a를 활성화하면, 이후에 셸에 정의되거나 수정되는 모든 변수들은 자동으로 "export" 속성을 갖게 됨.
#export된 변수는 현재 스크립트가 실행하는 모든 자식 프로세스(예: docker compose 명령어)로 전달된다.
set -a
# .env 파일 로드 (docker-compose.yml frontend: DOCKERHUB_USERNAME 등 변수 사용)
source .env
set +a

echo "### Pulling new frontend image ###"
docker compose pull frontend

echo "### Restarting frontend container ###"
# --no-deps 옵션으로 다른 서비스(백엔드 등)는 건드리지 않고 frontend만 재시작
docker compose up -d --no-deps frontend

echo "### Frontend deployment successful ###"