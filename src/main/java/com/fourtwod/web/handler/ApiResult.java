package com.fourtwod.web.handler;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApiResult<T> {

    private int code;
    private String message;
    private T data;

    public ApiResult(T data) {
        this.message = "OK";
        this.data = data;
    }

    public ApiResult(ResponseCode code) {
        this.code = code.getCode();
        this.message = code.getMessage();
    }
}
