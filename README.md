> # ASAP-api
> ### 42D Project v2 - ASAP API 서버
> 운영 URL: [ASAP-api Swagger](http://ec2-52-78-162-180.ap-northeast-2.compute.amazonaws.com:8080/swagger-ui.html)

### 1. 사용 기술
  * Spring Boot 2.1.7
  * JAVA 11
  * JPA
  * JWT 0.9.1
  * Swagger 2.9.2
  * AWS (EC2, RDS)

### 2. 현재까지 구현된 기능 (진행중,,,)
* **JWT 토큰 인증**
    * JWT Filter 적용
    * GET - /token
        * Vendor Code와 Secret Key를 입력 받아서 JWT 토큰을 생성하는 API
        * Swagger 테스트할때만 사용될 예정
        * 토큰 생성은 API가 아닌 Front 서버에서 직접 만든 후, 요청 헤더에 담아 전송
* **사용자 API**
    * POST - /user
        * email, registrationId, name을 입력 받아서 유효성 체크 후, 사용자 테이블에 저장하는 API
        * ~~OAuth2 로그인 (google, naver, kakao)~~
            * 로그인, 콜백 URL 처리는 Front 서버에서 하기로 결정하여 삭제
            * 만들었던 OAuth2 로그인 소스는 [ASAP Project v1](https://github.com/hyot88/ASAP) 에서 관리
    * PATCH - /user/nickname/{flag}
        * email, registrationId, nickname을 입력 받아서 사용자 테이블에 닉네임을 업데이트하는 API
