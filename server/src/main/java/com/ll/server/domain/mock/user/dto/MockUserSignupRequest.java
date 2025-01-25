package com.ll.server.domain.mock.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class MockUserSignupRequest {
    private String email;
    private String password;
    private String nickname;

}
