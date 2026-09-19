# E-Commerce

MSA 환경에서 재고 동시성 제어,
결제 중복 처리 방지, 레거시 시스템 장애 격리, 대용량 데이터 조회 성능 개선을
실제로 검증하고 개선한 프로젝트입니다.

<details>
<summary><span style="font-size: 1.5em; font-weight: bold; cursor: pointer;">
Demo</span></summary>
<br>
<span>인증</span>
<video src="https://github.com/user-attachments/assets/1789b2df-79ee-467b-b819-f5981ed4990f" controls width="100%"></video>
<br>

<span>쇼핑</span>
<video src="https://github.com/user-attachments/assets/fea2b296-5552-4616-998d-c3ef53b2ae60" controls width="100%"></video>
<br>

<span>구매</span>
<video src="https://github.com/user-attachments/assets/8a0c0f4d-a102-4a74-971d-48a44c4f816a" controls width="100%"></video>
<br>

<span>리뷰</span>
<video src="https://github.com/user-attachments/assets/d12fde47-f61c-45e2-a44d-16af650f592f" controls width="100%"></video>
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
→ 결제 방치/이탈 시 비동기 재고 복구<br>
→ 원자적 연산 기반 동시성 제어<br>
→ N + 1 문제 개선<br>
→ 리뷰서비스 API 성능 개선<br>
→ Circuit Breaker 장애 격리<br>
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
→ EC2 + Docker Compose(dev)와 EKS(prod) 두 환경을 병행 운영, 서비스 도메인은 EKS로 전환 완료<br>
→ EKS: VPC(퍼블릭/프라이빗/DB 서브넷 이중화), 관리형 DB(RDS/ElastiCache/MongoDB Atlas), RabbitMQ(StatefulSet)<br>
→ IRSA로 External Secrets Operator/ALB Controller에 최소 권한 부여, SSM Parameter Store를 K8s Secret으로 자동 동기화<br>
→ AWS Load Balancer Controller + Ingress로 외부 노출<br>
→ Terraform으로 인프라(VPC/EKS/RDS/IAM)와 클러스터 애드온(ESO, ALB Controller)까지 코드화, apply/destroy로 전체 환경 재현<br>
→ Caddy로 HTTPS 자동 처리(Let's Encrypt, dev), CloudFront+S3로 프론트엔드 정적 호스팅

</details>

<br>

<details>
<summary><span style="font-size: 1.5em; font-weight: bold; cursor: pointer;">EKS deploy details</span></summary>
<br>
→ MySQL/Redis는 데이터 유실 방지를 위해 관리형(RDS/ElastiCache)으로, MongoDB는 DocumentDB 대비 비용 효율을 고려해 Atlas(M0)로, RabbitMQ는 관리형(Amazon MQ) 대비 시간/비용 제약을 고려해 StatefulSet으로 — 데이터 중요도와 비용을 저울질해 저장소별로 다른 방식 선택<br>
→ 컨테이너 이름 기반(mysql, rabbitmq 등) 하드코딩된 연결 설정을, local/dev/prod 3단계 Spring Profile로 분리해 환경별 명시적 설정 강제<br>
→ Kubernetes가 Service 이름과 동일하게 자동 주입하는 환경변수(RABBITMQ_PORT 등)와의 충돌을 발견하고 명명 규칙으로 해결<br>
→ db.t3.micro의 낮은 max_connections를 여러 서비스가 동시에 소진하는 문제를 HikariCP 풀 크기 제한으로 완화<br>
→ ALB 헬스체크 기본 경로(/) 대신 /actuator/health를 지정해 Target unhealthy 문제 해결

</details>

<br>

<details>
<summary><span style="font-size: 1.5em; font-weight: bold; cursor: pointer;"> Future Work</span></summary>
<br>
→ product-service를 Spring Boot로 마이그레이션 (무중단 트래픽 전환)

</details>

