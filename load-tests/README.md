# Dev 커뮤니티 공개 GET 부하테스트

Dev 서버의 공개 커뮤니티 읽기 경로만 k6로 호출합니다. 기본 대상은 `http://140.245.70.133`이며 `140.245.70.133`, `localhost`, `127.0.0.1` 외의 호스트는 실행 스크립트가 거부합니다. 운영 도메인 허용 우회 옵션은 없습니다.

## 1. 합성 데이터 준비

`seed-community.sql`은 MySQL 8에서 집합 기반으로 RECRUIT 4,000건, NOTICE 1,000건과 게시글당 댓글 5건(총 25,000건)을 생성합니다. 기존 합성 데이터가 있으면 먼저 지운 뒤 다시 만들므로 재실행할 수 있습니다. 게시글 제목 `[K6-DEV]`와 작성자 `k6_loadtest_` 접두사가 모두 일치하는 데이터만 대상으로 하며 `community_settings`와 일반 데이터는 변경하지 않습니다.

DB 비밀번호를 명령행이나 셸 기록에 직접 쓰지 마세요. 아래 방식은 서버의 MySQL 컨테이너에 이미 설정된 `MYSQL_USER`, `MYSQL_PASSWORD`, `MYSQL_DATABASE` 환경변수를 사용하고 SQL만 SSH 표준 입력으로 전달합니다. 실제 SSH 대상과 컨테이너 이름만 바꾸세요.

```bash
ssh <dev-ssh-host> \
  'docker exec -i <mysql-container> sh -lc '\''exec mysql --user="$MYSQL_USER" --password="$MYSQL_PASSWORD" "$MYSQL_DATABASE"'\''' \
  < load-tests/seed/seed-community.sql
```

출력에서 RECRUIT 4,000건, NOTICE 1,000건, 댓글 25,000건인지 확인합니다. 커뮤니티 공개 설정은 seed가 변경하지 않으므로 Dev 관리 설정에서 사용자 공개 상태여야 합니다.

## 2. 실행

k6를 설치한 뒤 저장소 루트에서 실행합니다. 인자를 생략하면 `load` 프로필입니다.

```bash
./load-tests/run.sh load
./load-tests/run.sh smoke
./load-tests/run.sh stress
```

로컬 서버는 포트를 포함할 수 있습니다.

```bash
BASE_URL=http://localhost:8080 ./load-tests/run.sh smoke
```

프로필은 다음과 같습니다.

- `smoke`: 1 VU로 30초
- `load`: 1분 동안 10 VU까지 증가, 즉시 30 VU로 전환해 3분 유지, 1분 동안 0 VU로 감소
- `stress`: 1분 25 VU, 2분 50 VU, 2분 100 VU, 1분 0 VU 단계

각 반복은 실제 읽기 트래픽을 가정해 공개 설정 10%, RECRUIT 목록 30%, NOTICE 목록 10%, 게시글 상세 30%, 댓글 목록 20% 중 하나를 GET으로 조회합니다. Riot API, 인증, 관리자, 쓰기 경로는 호출하지 않습니다. 단, 현재 서버 구현상 게시글 상세 GET은 `view_count`를 증가시키므로 DB 쓰기 부하가 함께 발생합니다.

## 3. 결과 해석

테스트 성공 기준은 다음 세 가지입니다.

- `http_req_failed`: 1% 미만
- `http_req_duration`의 p95: 1,000ms 미만
- `checks`: 99% 초과

k6 종료 요약에서 threshold가 모두 통과했는지 확인합니다. setup이 공개 설정 또는 `[K6-DEV]`/`k6_loadtest_` 이중 표식 데이터를 찾지 못하면 부하를 시작하지 않고 실패합니다. 실패율과 checks는 HTTP 오류를, p95는 대부분의 요청이 체감할 지연 수준을 판단하는 데 사용합니다.

`stress`는 최대 100 VU와 상세 조회의 `view_count` 갱신을 동반합니다. Dev 환경의 DB·애플리케이션 여유 용량과 모니터링을 확인하고, 다른 검증 작업과 겹치지 않는 시간에만 실행하세요.

## 4. 정리

정리는 댓글을 먼저 삭제한 뒤 게시글을 삭제하며, 전후 합성 데이터 수를 출력합니다. seed와 동일하게 이중 표식 게시글에 연결되고 합성 작성자 표식이 있는 댓글 및 게시글만 삭제합니다.

```bash
ssh <dev-ssh-host> \
  'docker exec -i <mysql-container> sh -lc '\''exec mysql --user="$MYSQL_USER" --password="$MYSQL_PASSWORD" "$MYSQL_DATABASE"'\''' \
  < load-tests/seed/cleanup-community.sql
```

실행 로그, README, SQL, 셸 스크립트에 비밀번호·토큰·SSH 개인키 등 비밀값을 기록하거나 커밋하지 마세요.
