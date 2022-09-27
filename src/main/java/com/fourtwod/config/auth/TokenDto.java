package com.fourtwod.config.auth;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TokenDto {
    private String user;
    private String token;
}