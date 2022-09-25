package com.fourtwod.config.auth;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fourtwod.web.handler.ResponseCode;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

@Slf4j
@Builder
public class JWTAuthorizationFilter extends OncePerRequestFilter {

    private final String HEADER = "Authorization";
    private final String BEARER = "Bearer ";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        // 헤더 스키마 체크
        if (!checkJWTToken(request)) {
            outPrintln(response, ResponseCode.TOKN_E001);
            return;
        }

        // 헤더 jwt 토큰의 header 영역 디코딩
        String jwtToken = request.getHeader(HEADER).replace(BEARER, "");
        byte[] headerBytes = jwtToken.split("\\.")[1].getBytes();
        Base64.Decoder decoder = Base64.getDecoder();
        byte[] decodedBytes = decoder.decode(headerBytes);

        // 업체코드 가져오기
        ObjectMapper mapper = new ObjectMapper();
        Map<String, String> map = mapper.readValue(new String(decodedBytes), new TypeReference<Map<String, String>>(){});
        String jti = map.get("jti");

        Vendor vendor = Vendor.find(jti);
        
        // 정의되지않은 업체
        if (vendor == Vendor.UNKNOWN) {
            outPrintln(response, ResponseCode.TOKN_E002);
            return;
        }

        // 사용여부
        if (!"Y".equalsIgnoreCase(vendor.getUse())) {
            outPrintln(response, ResponseCode.TOKN_E003);
            return;
        }

        String secretKey = vendor.getSecretKey();

        // 헤더 jwt 토큰 파싱
        Claims claims = Jwts.parser().setSigningKey(secretKey.getBytes()).parseClaimsJws(jwtToken).getBody();

        // 클레임 파싱 오류 체크
        if (claims == null) {
            outPrintln(response, ResponseCode.TOKN_E004);
            return;
        }

        Date dtCurrent = new Date(System.currentTimeMillis()); // 현재시간
        // 토큰 생성시간, 서버간 시간차가 있을 수 있기에 5초간 텀을 둔다
        Date dtIssuedAt = new Date(Objects.requireNonNull(claims).getIssuedAt().getTime() - 5000);
        Calendar cal = Calendar.getInstance();
        cal.setTime(dtIssuedAt);
        cal.add(Calendar.MINUTE, 10);
        Date dtExpiration = cal.getTime();  // 토큰 만료시간

        // 생성일 체크
        if (dtIssuedAt.after(dtCurrent)) {
            outPrintln(response, ResponseCode.TOKN_E005);
            return;
        }

        // 만료일 체크
        if (dtExpiration.before(dtCurrent)) {
            outPrintln(response, ResponseCode.TOKN_E005);
            return;
        }

        // audience url 체크
//        if(!API_URL.equals(claims.getAudience())) {
//            outPrintln(response, "대상 경로 체크");
//            return;
//        }

        setSpringAuthentication(claims);
        chain.doFilter(request, response);
    }

    private void outPrintln(HttpServletResponse response, ResponseCode responseCode) {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("code", responseCode.getCode());
            jsonObject.put("message", responseCode.getMessage());
            SecurityContextHolder.clearContext();
            response.getWriter().println(jsonObject);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean checkJWTToken(HttpServletRequest request) {
        String authenticationHeader = request.getHeader(HEADER);
        return authenticationHeader != null && authenticationHeader.startsWith(BEARER);
    }

    private void setSpringAuthentication(Claims claims) {
        Set<GrantedAuthority> grantedAuthorities = new HashSet<>();
        grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_USER")); // 고정
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(claims.getId(), null, grantedAuthorities);
        
        // 인증객체 등록
        SecurityContextHolder.getContext().setAuthentication(auth);
    }
}
