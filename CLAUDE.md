# Claude Harness

이 저장소는 롤 내전 도우미 백엔드입니다.

## 먼저 읽기
- 프로젝트 개요: @README.md
- 빌드/의존성: @build.gradle
- 애플리케이션 설정: @src/main/resources/application.properties

## 작업 원칙
- Spring Boot 3.3 / Java 17 기준을 유지합니다.
- DB 스키마를 추정하지 말고 실제 엔티티/컨트롤러/설정 파일을 확인합니다.
- 보안 관련 값은 코드에 직접 추가하지 않고 환경변수 또는 설정 파일 흐름을 따릅니다.
- 전체 구조를 정리하기보다 관련 API 경로와 서비스만 정확히 수정합니다.

## 빠른 워크플로우
1. 관련 컨트롤러와 서비스 먼저 확인
2. 필요한 DTO / 엔티티 / 설정만 수정
3. 수정 후 최소 검증 실행
4. API 경로가 프론트 nginx 프록시(`/api`)와 맞는지 확인

## 검증 핵심
- 빌드: `./gradlew build`
- 테스트만 실행: `./gradlew test`
- 빠른 실행 확인: `./gradlew bootRun`

테스트가 무거우면 관련 패키지 변경 시 우선 `build` 또는 `test`만 선택적으로 실행합니다.

## 도메인 용어
- 소환사(`summoners`): 유저가 저장한 롤 프로필 정보
- 사용자(`users`): 로그인 계정 정보
- 팀 밸런서: 참가자 리스트를 밸런스 있게 양 팀으로 나누는 기능
- Riot API: 유저 정보 조회에 사용하는 외부 API
- `/api`: 프론트 nginx가 이 백엔드로 넘기는 기본 접두사

## 자주 하는 판단
- 단순 로직/DTO 수정: `./gradlew build`
- 보안/설정/API 경로 변경: `./gradlew test` 또는 관련 엔드포인트 수동 호출
- 설정 변경 시 `application.properties`와 배포 환경변수 흐름을 같이 확인

## 가비지 컬렉션
- `build/`, 임시 스크립트, 로컬 IDE 파일은 목적이 없으면 남기지 않습니다.
- `.env`, 로컬 백업 파일, 임시 schema dump는 필요한 경우만 유지합니다.

## 세부 규칙
- 워크플로우: @.claude/rules/workflow.md
- 검증: @.claude/rules/verification.md
- 용어: @.claude/rules/domain.md
- 정리 기준: @.claude/rules/garbage-collection.md
