package com.fourtwod.config.auth;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.Getter;

@Getter
//TODO: Vendor는 별도로 관리가 필요함, application-real.properties로...
public enum Vendor {
    /**
     * openssl rand -hex 16
     */
    FRONT_SERVER("front", "7e8b6e0ec18ca17a9fc54a20848656d0", "Y"),

    UNKNOWN("0", "", "");

    private String code;
    private String secretKey;
    private String use;

    Vendor(String code, String secretKey, String use) {
        this.code = code;
        this.secretKey = secretKey;
        this.use = use;
    }

    private static final Map<String, Vendor> codes =
        Collections.unmodifiableMap(
            Stream.of(values())
                .collect(Collectors.toMap(Vendor::getCode, Function.identity()))
        );

    public static Vendor find(String code) {
        return Optional.ofNullable(codes.get(code)).orElse(UNKNOWN);
    }
}
