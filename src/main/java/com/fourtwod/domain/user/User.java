package com.fourtwod.domain.user;

import com.fourtwod.domain.BaseTimeEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor
@Entity
public class User extends BaseTimeEntity {

    @EmbeddedId
    private UserId userId;

    @Column(nullable = false)
    private String name;

    @Column
    private String nickname;

    @Builder
    public User(UserId userId, String name, String nickname) {
        this.userId = userId;
        this.name = name;
        this.nickname = nickname;
    }

    public User updateName(String name) {
        this.name = name;

        return this;
    }

    public User updateNickname(String nickname) {
        this.nickname = nickname;

        return this;
    }
}
