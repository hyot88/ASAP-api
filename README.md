# 🚀 ASAP-api - JWT 기반 RESTful API 서버

## 📋 목차

1. [프로젝트 개요](#-프로젝트-개요)
2. [시스템 아키텍처](#-시스템-아키텍처)
3. [기술 스택](#-기술-스택)
4. [인증 시스템](#-인증-시스템)
5. [API 명세](#-api-명세)
6. [도메인 모델](#-도메인-모델)
7. [실행 방법](#-실행-방법)

---

## 🎯 프로젝트 개요

### 시나리오

> "클라이언트 애플리케이션에서 JWT 토큰 기반으로 인증하여 사용자 정보를 관리하는 Stateless RESTful API 서버"

ASAP-api는 **ASAP 서비스의 API 전용 서버**입니다. 기존 세션 기반 인증을 JWT(JSON Web Token) 기반으로 전환하여, 다양한 클라이언트(웹, 모바일 앱 등)에서 통합된 API를 사용할 수 있도록 설계되었습니다.

### 핵심 설계 원칙

| 원칙 | 설명 | 적용 기술 |
|------|------|----------|
| **Stateless 인증** | 서버에 세션 저장 없이 토큰으로 인증 | JWT (jjwt 0.9.1) |
| **업체 기반 인증** | 클라이언트별 SecretKey 관리 | Vendor Enum |
| **RESTful API** | 표준화된 API 설계 | Spring Boot REST |
| **API 문서화** | JWT 인증 지원 Swagger UI | Springfox 2.9.2 |
| **보안** | Spring Security 기반 필터 체인 | JWTAuthorizationFilter |

### ASAP vs ASAP-api 비교

| 구분 | ASAP (v1) | ASAP-api (v2) |
|------|-----------|---------------|
| **인증 방식** | OAuth2 + Session | JWT Token |
| **상태 관리** | Stateful (세션) | Stateless (토큰) |
| **클라이언트** | 웹 브라우저 | 웹, 모바일 앱, 외부 서비스 |
| **템플릿 엔진** | Mustache | 없음 (순수 API) |
| **기능 범위** | 미션, 티어, 랭킹 | 사용자 관리 API |

---

## 🏗️ 시스템 아키텍처

### 전체 구조

```
┌─────────────────────────────────────────────────────────────────────┐
│                            Clients                                   │
│           (Web App / Mobile App / External Service)                 │
└───────────────────────────────┬─────────────────────────────────────┘
                                │
                                │ HTTP + JWT Token
                                ▼
┌─────────────────────────────────────────────────────────────────────┐
│                      NGINX (Reverse Proxy)                          │
│                    - Load Balancing                                 │
│                    - Blue-Green 배포                                │
└───────────────────────────────┬─────────────────────────────────────┘
                                │
              ┌─────────────────┴─────────────────┐
              ▼                                   ▼
    ┌──────────────────┐              ┌──────────────────┐
    │   Spring Boot    │              │   Spring Boot    │
    │  (real1:8081)    │              │  (real2:8082)    │
    │                  │              │                  │
    │ ┌──────────────┐ │              │ ┌──────────────┐ │
    │ │JWT Filter    │ │              │ │JWT Filter    │ │
    │ │  ↓           │ │              │ │  ↓           │ │
    │ │Controller    │ │              │ │Controller    │ │
    │ │  ↓           │ │              │ │  ↓           │ │
    │ │Service       │ │              │ │Service       │ │
    │ │  ↓           │ │              │ │  ↓           │ │
    │ │Repository    │ │              │ │Repository    │ │
    │ └──────────────┘ │              │ └──────────────┘ │
    └────────┬─────────┘              └────────┬─────────┘
             │                                  │
             └────────────────┬─────────────────┘
                              │
                              ▼
                    ┌──────────────────┐
                    │     MariaDB      │
                    │     (RDS)        │
                    └──────────────────┘
```

### JWT 인증 플로우

```
┌──────────┐                    ┌──────────────┐                    ┌──────────┐
│  Client  │                    │  API Server  │                    │    DB    │
└────┬─────┘                    └──────┬───────┘                    └────┬─────┘
     │                                 │                                 │
     │  1. Generate JWT (Client-side)  │                                 │
     │  ─────────────────────────────► │                                 │
     │                                 │                                 │
     │  2. API Request + JWT Token     │                                 │
     │  ─────────────────────────────► │                                 │
     │                                 │                                 │
     │                           ┌─────┴─────┐                           │
     │                           │JWT Filter │                           │
     │                           │           │                           │
     │                           │ 3. Token  │                           │
     │                           │  Parsing  │                           │
     │                           │     ↓     │                           │
     │                           │ 4. Vendor │                           │
     │                           │  Check    │                           │
     │                           │     ↓     │                           │
     │                           │ 5. Secret │                           │
     │                           │  Verify   │                           │
     │                           │     ↓     │                           │
     │                           │ 6. Expiry │                           │
     │                           │  Check    │                           │
     │                           └─────┬─────┘                           │
     │                                 │                                 │
     │                                 │  7. Query                       │
     │                                 │ ─────────────────────────────► │
     │                                 │                                 │
     │                                 │  8. Result                      │
     │                                 │ ◄───────────────────────────── │
     │                                 │                                 │
     │  9. API Response                │                                 │
     │  ◄───────────────────────────── │                                 │
     │                                 │                                 │
```

---

## 🛠️ 기술 스택

### Backend

| 기술 | 버전 | 용도 |
|------|------|------|
| **Java** | 11 | 메인 언어 |
| **Spring Boot** | 2.1.7 | 애플리케이션 프레임워크 |
| **Spring Security** | - | 인증/인가 필터 체인 |
| **Spring Data JPA** | - | ORM, 데이터 액세스 |
| **jjwt** | 0.9.1 | JWT 토큰 생성/검증 |
| **Gson** | 2.9.0 | JSON 파싱 |

### Database

| 기술 | 용도 |
|------|------|
| **H2** | 개발/테스트 환경 |
| **MariaDB** | 운영 환경 (AWS RDS) |

### Infra & DevOps

| 기술 | 용도 |
|------|------|
| **AWS EC2** | 애플리케이션 서버 |
| **AWS RDS** | 데이터베이스 서버 |
| **AWS S3** | 빌드 아티팩트 저장 |
| **AWS CodeDeploy** | 자동 배포 |
| **NGINX** | 리버스 프록시, 로드밸런싱 |
| **Travis CI** | CI/CD 파이프라인 |

### API Documentation

| 기술 | 버전 | 용도 |
|------|------|------|
| **Swagger** | 2.9.2 | API 문서화 (JWT 인증 지원) |

---

## 🔐 인증 시스템

### JWT 토큰 구조

```
Header.Payload.Signature
```

#### Header

```json
{
  "alg": "HS256",
  "typ": "JWT"
}
```

#### Payload (Claims)

```json
{
  "jti": "front",        // 업체 코드 (Vendor Code)
  "aud": "localhost",    // 대상 서버
  "iat": 1704067200000,  // 발급 시간 (Unix timestamp)
  "sub": "secretKey"     // 시크릿 키
}
```

### Vendor (업체) 관리

업체별로 고유한 Secret Key를 관리하여 API 접근을 제어합니다.

```java
public enum Vendor {
    // openssl rand -hex 16 로 생성된 SecretKey
    FRONT_SERVER("front", "7e8b6e0ec18ca17a9fc54a20848656d0", "Y"),
    UNKNOWN("0", "", "");

    private String code;       // 업체 코드
    private String secretKey;  // 비밀키 (HS256 서명용)
    private String use;        // 사용 여부 (Y/N)
}
```

### JWTAuthorizationFilter 검증 플로우

```java
@Override
protected void doFilterInternal(HttpServletRequest request, 
                                HttpServletResponse response, 
                                FilterChain chain) {
    // 1. 헤더 스키마 체크 (Bearer 토큰 확인)
    if (!checkJWTToken(request)) {
        return error(ResponseCode.TOKN_E001);  // "헤더 스키마 오류"
    }

    // 2. JWT Payload에서 업체 코드(jti) 추출
    String jti = extractJti(jwtToken);
    Vendor vendor = Vendor.find(jti);

    // 3. 업체 유효성 검증
    if (vendor == Vendor.UNKNOWN) {
        return error(ResponseCode.TOKN_E002);  // "정의되지 않은 업체"
    }

    // 4. 업체 사용 여부 확인
    if (!"Y".equalsIgnoreCase(vendor.getUse())) {
        return error(ResponseCode.TOKN_E003);  // "사용 불가능한 업체"
    }

    // 5. SecretKey로 JWT 서명 검증
    Claims claims = Jwts.parser()
            .setSigningKey(vendor.getSecretKey().getBytes())
            .parseClaimsJws(jwtToken)
            .getBody();

    // 6. 토큰 유효 기간 검증 (발급 후 10분)
    Date issuedAt = claims.getIssuedAt();
    Date expiration = addMinutes(issuedAt, 10);
    
    if (now.before(issuedAt) || now.after(expiration)) {
        return error(ResponseCode.TOKN_E005);  // "토큰 기간 만료"
    }

    // 7. Spring Security 인증 객체 등록
    setSpringAuthentication(claims);
    chain.doFilter(request, response);
}
```

### 토큰 유효 시간

| 항목 | 값 |
|------|-----|
| **토큰 유효 기간** | 발급 시점으로부터 10분 |
| **시간 오차 허용** | 5초 (서버 간 시간 차이 보정) |

### 인증 제외 경로

```java
web.ignoring().antMatchers(
    "/",               // 루트
    "/swagger-ui.html",// Swagger UI
    "/v2/api-docs",    // Swagger API Docs
    "/webjars/**",     // Swagger 리소스
    "/swagger-resources/**",
    "/favicon.ico",
    "/csrf",
    "/h2-console/**",  // H2 콘솔
    "/profile",        // 프로필 확인
    "/token"           // 토큰 발급 (테스트용)
);
```

---

## 📡 API 명세

### Token API

#### 토큰 생성 (테스트용)

```http
GET /token?code={업체코드}&secretKey={비밀키}
```

> ⚠️ **주의**: 이 API는 Swagger 테스트용입니다. 실제 운영 환경에서는 클라이언트에서 직접 JWT를 생성해야 합니다.

**Request Parameters**

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| code | String | Yes | 업체 코드 (예: "front") |
| secretKey | String | Yes | 비밀키 |

**Response**

```json
{
  "code": 0,
  "message": "OK",
  "data": {
    "user": "front",
    "token": "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
  }
}
```

---

### User API

#### 사용자 로그인

```http
POST /user
Authorization: Bearer {JWT_TOKEN}
Content-Type: application/json
```

**Request Body**

```json
{
  "email": "user@example.com",
  "registrationId": "google",
  "name": "홍길동"
}
```

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| email | String | Yes | 사용자 이메일 |
| registrationId | String | Yes | 로그인 수단 (google, naver, kakao) |
| name | String | Yes | 사용자 이름 |

**Response**

```json
{
  "code": 0,
  "message": "OK",
  "data": {
    "userId": {
      "email": "user@example.com",
      "registrationId": "google"
    },
    "name": "홍길동",
    "nickname": null
  }
}
```

---

#### 닉네임 체크/저장

```http
PATCH /user/nickname/{flag}
Authorization: Bearer {JWT_TOKEN}
Content-Type: application/json
```

**Path Parameters**

| Parameter | Type | Description |
|-----------|------|-------------|
| flag | int | 0: 유효성 체크만, 1: 유효성 체크 및 저장 |

**Request Body**

```json
{
  "email": "user@example.com",
  "registrationId": "google",
  "nickname": "멋진닉네임"
}
```

**Response**

```json
{
  "code": 0,
  "message": "OK",
  "data": null
}
```

**닉네임 규칙**

| 규칙 | 설명 |
|------|------|
| 길이 | 2자 이상 10자 이하 |
| 문자 | 한글, 영문, 숫자만 허용 |
| 중복 | 중복 닉네임 불가 |

---

### Response Code

#### 공통 코드

| Code | Message | 설명 |
|------|---------|------|
| 0 | OK | 성공 |
| 1000 | Internal Server Error | 서버 에러 |
| 1001 | 처리 중 에러가 발생하였습니다. | 처리 에러 |

#### 토큰 관련 코드

| Code | Message | 설명 |
|------|---------|------|
| 2001 | 헤더 스키마 오류 | Authorization 헤더 형식 오류 |
| 2002 | 정의되지 않은 업체 | 등록되지 않은 업체 코드 |
| 2003 | 사용 불가능한 업체 | 비활성화된 업체 |
| 2004 | 클레임 파싱 오류 | JWT Claims 파싱 실패 |
| 2005 | 토큰 기간 만료 | 토큰 유효 시간 초과 |

#### 닉네임 관련 코드

| Code | Message | 설명 |
|------|---------|------|
| 3001 | 2자 이상 10자 이하로 입력해주세요. | 닉네임 길이 오류 |
| 3002 | 한글,영문,숫자로 입력해주세요. | 닉네임 형식 오류 |
| 3003 | 중복된 닉네임입니다. | 닉네임 중복 |

---

## 📊 도메인 모델

### ERD 구조

```
┌─────────────────────────────┐
│           User              │
├─────────────────────────────┤
│ email (PK)          VARCHAR │
│ registration_id (PK) VARCHAR│
│ name                VARCHAR │
│ nickname            VARCHAR │
│ created_at         DATETIME │
│ modified_at        DATETIME │
└─────────────────────────────┘
```

### Entity

```java
@Entity
public class User extends BaseTimeEntity {

    @EmbeddedId
    private UserId userId;  // 복합키 (email + registrationId)

    @Column(nullable = false)
    private String name;

    @Column
    private String nickname;

    public User updateName(String name) {
        this.name = name;
        return this;
    }

    public User updateNickname(String nickname) {
        this.nickname = nickname;
        return this;
    }
}
```

### 복합키 (Composite Key)

```java
@Embeddable
public class UserId implements Serializable {

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String registrationId;  // google, naver, kakao
}
```

---

## 💻 실행 방법

### 로컬 개발 환경

```bash
# 1. 프로젝트 클론
git clone https://github.com/your-repo/ASAP-api.git
cd ASAP-api

# 2. 빌드 및 실행
./gradlew build
./gradlew bootRun

# 3. 접속
# Swagger UI: http://localhost:8080/swagger-ui.html
# H2 Console: http://localhost:8080/h2-console
```

### 테스트 실행

```bash
# 전체 테스트
./gradlew test

# 특정 테스트 클래스 실행
./gradlew test --tests "TokenControllerTest"
./gradlew test --tests "UserControllerTest"
```

### Swagger UI에서 JWT 인증 테스트

1. **토큰 발급**
   - `/token` API 호출
   - `code`: front
   - `secretKey`: 7e8b6e0ec18ca17a9fc54a20848656d0

2. **토큰 등록**
   - 우측 상단 "Authorize" 버튼 클릭
   - 발급받은 토큰 입력 (Bearer 포함)

3. **API 테스트**
   - 인증이 필요한 API 호출

### 클라이언트에서 JWT 생성 예시

#### JavaScript

```javascript
const jwt = require('jsonwebtoken');

const code = 'front';
const secretKey = '7e8b6e0ec18ca17a9fc54a20848656d0';

const token = jwt.sign(
  {
    jti: code,
    aud: 'api-server-url',
    iat: Date.now()
  },
  secretKey,
  {
    algorithm: 'HS256'
  }
);

// API 호출 시 헤더에 추가
fetch('/api/user', {
  headers: {
    'Authorization': `Bearer ${token}`,
    'Content-Type': 'application/json'
  },
  // ...
});
```

#### Java (Android/Backend)

```java
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

String code = "front";
String secretKey = "7e8b6e0ec18ca17a9fc54a20848656d0";

Map<String, Object> claims = new HashMap<>();
claims.put("jti", code);
claims.put("aud", "api-server-url");
claims.put("iat", System.currentTimeMillis());

String token = Jwts.builder()
        .setClaims(claims)
        .setIssuedAt(new Date())
        .signWith(SignatureAlgorithm.HS256, secretKey.getBytes())
        .compact();

// Bearer 토큰으로 사용
String bearerToken = "Bearer " + token;
```

---

## 🎓 키워드 체크리스트

- [x] JWT (JSON Web Token)
- [x] Stateless 인증
- [x] Spring Security Filter
- [x] Vendor 기반 인증
- [x] HS256 서명 알고리즘
- [x] Claims 파싱
- [x] 토큰 만료 처리
- [x] 복합키 (Composite Key)
- [x] @EmbeddedId
- [x] RESTful API
- [x] Swagger JWT 인증
- [x] Spring Data JPA
- [x] NGINX 리버스 프록시
- [x] AWS (EC2, RDS, S3, CodeDeploy)
- [x] Travis CI

---

## 📄 라이센스

MIT License
