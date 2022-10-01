package com.fourtwod.web;

import com.fourtwod.config.auth.TokenDto;
import com.fourtwod.web.handler.ApiResult;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@Api(tags = {"토큰 API"})
//@ApiIgnore
public class TokenController {

    @ApiOperation(value = "토큰 생성", notes = "토큰을 생성합니다.\nSwagger 테스트용일뿐 API 요청시에는, Client에서 토큰 생성 하는 것을 원칙으로 합니다.", response = ApiResult.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "code", value = "업체 코드", required = true, dataType = "string", paramType = "query", defaultValue = "front")
            , @ApiImplicitParam(name = "secretKey", value = "비밀키", required = true, dataType = "string", paramType = "query", defaultValue = "7e8b6e0ec18ca17a9fc54a20848656d0")
    })
    @GetMapping("/token")
    public ApiResult token(@RequestParam("code") String code, @RequestParam("secretKey") String secretKey) {
        return new ApiResult<>(
            TokenDto.builder()
                .user(code)
                .token(getJWTToken(code, secretKey))
                .build()
        );
    }

    private String getJWTToken(String id, String secretKey) {
        Map<String, Object> headers = new HashMap<>();
        headers.put("alg", "HS256");
        headers.put("typ", "JWT");

        Map<String, Object> payloads = new HashMap<>();
        payloads.put("jti", id);
        payloads.put("aud", "localhost");
        payloads.put("iat", System.currentTimeMillis());

        return "Bearer " + Jwts.builder()
                .setHeader(headers)
                .setClaims(payloads)
                .setIssuedAt(new Date())
                .setSubject(secretKey)
                .signWith(SignatureAlgorithm.HS256, secretKey.getBytes())
                .compact();
    }
}
