package com.fourtwod.web;

import com.fourtwod.domain.user.User;
import com.fourtwod.service.user.UserService;
import com.fourtwod.web.dto.UserDto;
import com.fourtwod.web.handler.ApiResult;
import com.fourtwod.web.handler.ResponseCode;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@Api(tags = {"사용자 API"})
@RestController
public class UserController {

    private final UserService userService;

    @PostMapping("/user")
    @ApiOperation(value = "사용자 로그인", response = ApiResult.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userDto"
                , value = "사용자 DTO" +
                    "\nemail: 이메일" +
                    "\nregistrationId: 로그인 수단(google, naver, kakao)" +
                    "\nname: 이름"
                , required = true, dataType = "UserDto", paramType = "body")
    })
    public ApiResult login(@RequestBody UserDto userDto) throws Exception {
        if (userDto != null ) {
            User user = userService.login(userDto);
            return new ApiResult<>(user);
        } else {
            throw new Exception();
        }
    }

    @PatchMapping("/user/nickname/{flag}")
    @ApiOperation(value = "닉네임 체크/저장", response = ApiResult.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "flag", value = "유효성 체크(0), 유효성 체크 및 저장(1)", required = true
                    , dataType = "int", paramType = "path", example = "0")
            , @ApiImplicitParam(name = "userDto"
                    , value = "사용자 DTO" +
                        "\nemail: 이메일" +
                        "\nregistrationId: 로그인 수단(google, naver, kakao)" +
                        "\nnickname: 닉네임"
                    , required = true, dataType = "UserDto", paramType = "body")
    })
    public ApiResult checkOrUpdateNickName(@PathVariable int flag, @RequestBody UserDto userDto) throws Exception {
        if (userDto != null ) {
            ResponseCode responseCode = userService.checkOrUpdateNickName(flag, userDto);
            return new ApiResult<>(responseCode);
        } else {
            throw new Exception();
        }
    }

    @DeleteMapping("/users")
    @ApiOperation(value = "[임시] 사용자 정보 모두 삭제", response = ApiResult.class)
    public ApiResult deleteUsers() {
        return new ApiResult<>(userService.deleteAll());
    }
}
