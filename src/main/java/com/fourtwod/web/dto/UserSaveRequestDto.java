package com.fourtwod.web.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserSaveRequestDto {

    private String email;
    private String registrationId;
    private String nickname;
    private String bSave;

    @Builder
    public UserSaveRequestDto(String nickname) {
        this.nickname = nickname;
    }
}
