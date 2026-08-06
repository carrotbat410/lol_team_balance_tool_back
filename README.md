## 🔗 프로젝트 관련 링크
> <a href="https://lolcivilwarhelper.kro.kr/team-balancer" target="_blank">서비스 URL(운영중)</a>
- <a href="https://github.com/carrotbat410/lol_team_balance_tool_front" target="_blank">프론트엔드 Repo</a>

<br></br>

## 💻 프로젝트 소개
> 롤 내전 도우미 Ver.2 (BackEnd)
> 
> 롤 내전 도우미에서 밸런스 있는 내전 팀 결과를 생성해보세요!

<img width="1710" height="865" alt="스크린샷 2026-08-06 오후 1 45 39" src="https://github.com/user-attachments/assets/bd96829e-3325-43f2-9574-e53d474918eb" />

- 롤 내전 게임시, 균형있는 게임을 위해 티어 차이가 적은 두 팀을 보여주는 서비스입니다.
- 라이엇 API를 사용하여 유저 정보를 불러올 수 있습니다.
- 커뮤니티 기능(연습경기 상대 모집 / 자유 게시판 / 내전 클랜 홍보)이 추가 될 예정입니다.

## 📚 기술 스택

| 기술 | 설명 |
|---|---|
| `Java 17` | 백엔드 애플리케이션의 메인 개발 언어 |
| `Spring Boot 3.3.8` | REST API 서버 구성과 비즈니스 로직 처리 |
| `Gradle 8.11.1` | 의존성 관리와 빌드 자동화 |
| `Spring Data JPA` | MySQL 데이터 객체 매핑과 CRUD 처리 |
| `Spring Security` | 인증/인가와 관리자 권한 제어 |
| `JWT` | 서버 세션 없는 로그인 인증 방식 |
| `WebClient` | Riot Games API 호출 |
| `MySQL 8.0` | 사용자, 소환사, 방문자, 커뮤니티 데이터 저장 |
| `Docker`, `Docker Compose` | 백엔드, 프론트엔드, MySQL 컨테이너 통합 운영 |
| `Nginx` | HTTPS 처리와 Reverse Proxy |
| `GitHub Actions` | CI/CD 자동화 |
| `GHCR` | Docker 이미지 저장소 |
| `OCI Compute` | Ubuntu 기반 운영 서버 |
| `Let's Encrypt` | HTTPS 인증서 발급 |
| `Uptime Kuma` | 서버 상태 모니터링 |
| `Discord Webhook` | 장애/복구 알림 전송 |
| `Swagger / Springdoc OpenAPI` | API 명세 확인 |

## ⚙ 서비스 아키텍처
<img width="1800" height="1180" alt="lol-civilwar-helper-architecture" src="https://github.com/user-attachments/assets/1951d64a-8043-4f2a-893b-85834914c816" />

## 📁 ERD
<img width="944" height="872" alt="lol-civilwar-helper-erd" src="https://github.com/user-attachments/assets/6534b190-771f-45ea-895a-190d21a24d69" />





