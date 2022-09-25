package com.fourtwod.web.handler;

import lombok.Getter;

@Getter
public enum ResponseCode {

    // 공통
    COMM_S000(0, "OK"),
    COMM_E000(1000, "Internal Server Error"),
    COMM_E001(1001, "처리 중 에러가 발생하였습니다."),

    // 토큰
    TOKN_E001(2001, "헤더 스키마 오류"),
    TOKN_E002(2002, "정의되지 않은 업체"),
    TOKN_E003(2003, "사용 불가능한 업체"),
    TOKN_E004(2004, "클레임 파싱 오류"),
    TOKN_E005(2005, "토큰 기간 만료"),

    // 닉네임
    NICK_E001(3001, "2자 이상 10자 이하로 입력해주세요."),
    NICK_E002(3002, "한글,영문,숫자로 입력해주세요."),
    NICK_E003(3003, "중복된 닉네임입니다.");

    private int code;
    private String message;

    ResponseCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
