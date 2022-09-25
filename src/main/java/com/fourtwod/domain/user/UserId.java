package com.fourtwod.domain.user;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Data
@NoArgsConstructor
@Embeddable
public class UserId implements Serializable {

    @Column
    private String email;

    @Column
    private String registrationId;

    @Builder
    public UserId(String email, String registrationId) {
        this.email = email;
        this.registrationId = registrationId;
    }
}
