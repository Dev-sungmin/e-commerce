# E-Commerce

MSA 환경에서 재고 동시성 제어,
결제 중복 처리 방지, 레거시 시스템 장애 격리, 대용량 데이터 조회 성능 개선을
실제로 검증하고 개선한 프로젝트입니다.

<details>
<summary><span style="font-size: 1.5em; font-weight: bold; cursor: pointer;">
Demo</span></summary>
<br>
<span>인증</span>
<video src="https://github.com/user-attachments/assets/6bd3b957-45db-47fe-9394-4aa2c93666d4" controls width="100%"></video>
<br>

<span>쇼핑</span>
<video src="https://github.com/user-attachments/assets/64743da2-2ed5-47e5-a7d3-7a70ca8ca084" controls width="100%"></video>
<br>

<span>구매</span>
<video src="https://github.com/user-attachments/assets/7a9155da-8909-4f6b-9baa-9d9b9b1b4348" controls width="100%"></video>
<br>

<span>리뷰</span>
<video src="https://github.com/user-attachments/assets/ba7dd3b0-af44-4519-a8f0-5d14817be23f" controls width="100%"></video>
<br>

</details>

<br>

<details>
<summary><span style="font-size: 1.5em; font-weight: bold; cursor: pointer;">
Services</span></summary>
<br>

| 서비스 | 역할 | DB |
|---|---|---|
| Gateway | JWT 인증, 라우팅, CORS | - |
| User Service | 회원가입/로그인, 토큰 발급 | MySQL |
| Product Service (PHP) | 상품/재고 관리 | MySQL |
| Order Service | 주문 생성, Saga 보상 트랜잭션 | MySQL |
| Payment Service | 토스페이먼츠 연동, Idempotent Receiver | MySQL |
| Cart Service | 장바구니 | Redis |
| Review Service | 리뷰, 좋아요, 평균 별점 | MongoDB |

</details>

<br>

<details>
<summary><span style="font-size: 1.5em; font-weight: bold; cursor: pointer;">
Tech Stack</span></summary>

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

</details>

<br>

<details>
<summary><span style="font-size: 1.5em; font-weight: bold; cursor: pointer;">
Why PHP?</span></summary>
<br>
이종 기술 스택 환경에서의 서비스 간 통신, 장애 격리(Circuit Breaker/Bulkhead),
인증 정보 전파를 직접 다뤄보기 위해 상품/재고 서비스를 의도적으로 PHP로 구현했습니다.
향후 이 서비스를 Spring Boot로 전환하는 Strangler Fig 마이그레이션을 계획하고 있습니다.

</details>

<br>

<details>
<summary><span style="font-size: 1.5em; font-weight: bold; cursor: pointer;">Key Technical Highlights</span></summary>
<br>
→ 결제 방지/이탈 시 비동기 재고 복구<br>
→ N + 1 문제 개선<br>
→ 리뷰서비스 API 성능 개선<br>
→ Circuit Breaker 장애 격리<br>
→ 원자적 연산 기반 동시성 제어<br>
→ 가격 조작 취약점 문제 개선<br>

</details>

<br>

<details>
<summary><span style="font-size: 1.5em; font-weight: bold; cursor: pointer;">CI/CD</span></summary>
<br>
→ GitHub Actions 기반 파이프라인, 변경된 서비스만 선택적으로 빌드/배포<br>
→ SSM Parameter Store로 프로덕션 시크릿 관리 (프로세스 변수 사용)<br>
→ Frontend는 push 시 자동 배포(S3+CloudFront), Backend는 수동 트리거(Continuous Delivery)

</details>

<br>

<details>
<summary><span style="font-size: 1.5em; font-weight: bold; cursor: pointer;">Infrastructure</span></summary>
<br>
→ EC2 단일 인스턴스 + Docker Compose로 전체 서비스 임시 운영<br>
→ Caddy로 HTTPS 자동 처리(Let's Encrypt), CloudFront+S3로 프론트엔드 정적 호스팅<br>
→ Terraform으로 인프라 전체 코드화

</details>

<br>

<details>
<summary><span style="font-size: 1.5em; font-weight: bold; cursor: pointer;"> Future Work</span></summary>
<br>
→ product-service를 Spring Boot로 마이그레이션 (무중단 트래픽 전환)<br>
→ EKS 전환

</details>

