package com.fourtwod.web;

import com.fourtwod.service.user.UserService;
import com.fourtwod.web.dto.UserSaveRequestDto;
import com.fourtwod.web.handler.ApiResult;
import com.fourtwod.web.handler.ResponseCode;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@Api(tags = {"사용자 API"})
@RestController
public class UserController {

    private final UserService userService;

    @PatchMapping("/user/nickname")
    @ApiOperation(value = "닉네임 체크/저장", response = ApiResult.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userSaveRequestDto"
                    , value = "닉네임 체크/저장 DTO" +
                        "\nbSave: 유효성 체크(0), 유효성 체크 및 저장(1)" +
                        "\nemail: 이메일" +
                        "\nnickname: 닉네임" +
                        "\nregistrationId: 로그인 수단(google, naver, kakao)"
                    , required = true, dataType = "UserSaveRequestDto", paramType = "body")
    })
    public ApiResult checkOrUpdateNickName(@RequestBody UserSaveRequestDto userSaveRequestDto) throws Exception {
        if (userSaveRequestDto != null ) {
            ResponseCode responseCode = userService.checkOrUpdateNickName(userSaveRequestDto);
            return new ApiResult<>(responseCode);
        } else {
            throw new Exception();
        }
    }

    @DeleteMapping("/users")
    @ApiOperation(value = "[임시] 사용자 정보 모두 삭제", response = ApiResult.class)
    public ApiResult deleteUsers() throws Exception {
        return new ApiResult<>(userService.deleteAll());
    }
}
