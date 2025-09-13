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
#docker compose up -d --no-deps

# docker compose up 명령어는 이미 컨테이너가 실행 중이고, docker-compose.yml 설정이 변경되지 않았다면, 로컬에 새로운 이미지를 받아왔더라도 컨테이너를 굳이 다시 만들지 않는 특징이 있음.
# 그래서 스크립트가 docker compose pull로 새 이미지를 가져오긴 했지만, up 명령어는 "이미 컨테이너가 실행 중이네"라고 판단하고 아무것도 하지 않은 것.
# 컨테이너를 강제로 다시 생성하도록 하는 옵션을 추가하여 해결함 (물론, stop 후 up 으로도 가능함)
docker compose up -d --no-deps --force-recreate frontend

echo "### Frontend deployment successful ###"