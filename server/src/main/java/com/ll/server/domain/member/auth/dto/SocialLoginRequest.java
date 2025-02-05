package com.ll.server.domain.member.auth.dto;

import com.ll.server.domain.member.enums.Provider;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SocialLoginRequest {
    @NotBlank(message = "소셜 타입은 필수입니다.")
    private Provider provider;

    @NotBlank(message = "Authorization Code 는 필수입니다.")
    private String authorizationCode;
}
