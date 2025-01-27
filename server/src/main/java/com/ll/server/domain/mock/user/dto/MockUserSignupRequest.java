package com.ll.server.domain.mock.user.dto;

import com.ll.server.domain.mock.user.MockRole;
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

    private MockRole role;

}
