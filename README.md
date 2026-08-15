# E-Commerce

**대규모 트래픽 환경에서 발생하는 실전 문제(동시성, 장애 전파, 데이터 정합성)를
직접 재현하고 해결한 백엔드 마이크로서비스 프로젝트입니다.**

7개 서비스로 구성된 MSA 환경에서, 재고 동시성 제어,
결제 중복 처리 방지, 레거시 시스템 장애 격리, 대용량 데이터 조회 성능 개선을
실제로 구현하고 수치로 검증했습니다.

## Demo
(작성 예정)

## Tech Stack

### Backend
- Spring Boot 3.x(Java 17), Spring Cloud Gateway
- Spring Data JPA / MongoDB / Redis
- MySQL, Redis, MongoDB
- PHP 8.3 (상품/재고 서비스)
- Resilience4j (Circuit Breaker, Bulkhead)

### Infra / DevOps
- AWS EC2, S3, CloudFront, ECR, SSM Parameter Store
- Docker, Docker Compose, Terraform (IaC)
- GitHub Actions (CI/CD)
- Caddy (HTTPS, 인증서 자동 발급/갱신)

### Frontend
- React, Vite

## Architecture

(다이어그램 자리 - 추가 예정)

## Services

| 서비스 | 역할 | DB |
|---|---|---|
| Gateway | JWT 인증, 라우팅, CORS | - |
| User Service | 회원가입/로그인, 토큰 발급 | MySQL |
| Product Service (PHP) | 상품/재고 관리 | MySQL |
| Order Service | 주문 생성, Saga 보상 트랜잭션 | MySQL |
| Payment Service | 토스페이먼츠 연동, Idempotent Receiver | MySQL |
| Cart Service | 장바구니 | Redis |
| Review Service | 리뷰, 좋아요, 평균 별점 | MongoDB |

## Why PHP?

실무 환경에서는 레거시 시스템과 신규 마이크로서비스가 함께 운영되는 경우가 흔합니다.
이런 이종 기술 스택 환경에서의 서비스 간 통신, 장애 격리(Circuit Breaker/Bulkhead),
인증 정보 전파를 직접 다뤄보기 위해 상품/재고 서비스를 의도적으로 PHP로 구현했습니다.
향후 이 서비스를 Spring Boot로 전환하는 Strangler Fig 마이그레이션을 계획하고 있습니다.

## Key Technical Highlights

### 성능 (Performance)
- (작성 예정)

### 신뢰성 (Reliability)
- **장애 격리**: Resilience4j Circuit Breaker + Bulkhead로 PHP 레거시 서비스 장애가
  Order Service 전체로 전파되지 않도록 격리
- **Saga 패턴 + 보상 트랜잭션**: Order Service의 다중 상품 재고 차감 중 일부 실패 시
  이미 성공한 항목을 역순으로 복구
- **Idempotent Receiver**: Payment Service, paymentKey 기반으로 결제 중복 승인 방지
  (애플리케이션 체크 + DB unique 제약 이중 방어)
- **원자적 연산 기반 동시성 제어**: 재고 차감(조건부 UPDATE), 장바구니 수량(Redis HINCRBY)
  — read-modify-write 패턴의 lost update 문제 회피
- 클라이언트 입력 검증 취약점 발견 및 수정 (주문 생성 시 가격 조작 방지)
- (향후 과제: 결제 타임아웃 자동 취소, Payment-Order 간 최종 정합성 보장(Outbox))

### 대용량 데이터 처리
- (작성 예정)

## Troubleshooting


- (작성 예정)

## CI/CD

- GitHub Actions 기반 파이프라인, 변경된 서비스만 선택적으로 빌드/배포
- SSM Parameter Store로 프로덕션 시크릿 관리 (프로세스 변수 사용)
- Frontend는 push 시 자동 배포(S3+CloudFront), Backend는 수동 트리거(Continuous Delivery)

## Infrastructure

- EC2 단일 인스턴스 + Docker Compose로 전체 서비스 운영
- Caddy로 HTTPS 자동 처리(Let's Encrypt), CloudFront+S3로 프론트엔드 정적 호스팅
- Terraform으로 인프라 전체 코드화

## Future Work

- product-service를 Spring Boot로 마이그레이션 (무중단 트래픽 전환)
- EKS 전환
- 비동기 메시징 도입 검토