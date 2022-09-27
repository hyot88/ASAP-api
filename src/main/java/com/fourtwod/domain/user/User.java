package com.fourtwod.domain.user;

import com.fourtwod.domain.BaseTimeEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class User extends BaseTimeEntity {

    @EmbeddedId
    private UserId userId;

    @Column(nullable = false)
    private String name;

    @Column
    private String nickname;

    public User updateName(String name) {
        this.name = name;

        return this;
    }

    public User updateNickname(String nickname) {
        this.nickname = nickname;

        return this;
    }
}
