package com.ll.server.domain.mock.user.dto;

import com.ll.server.domain.mock.user.entity.MockUser;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Builder
@Getter
public class MockUserDTO {
    private Long id;
    private String email;
    private String nickname;

    public MockUserDTO(MockUser user){
        id=user.getId();
        email=user.getEmail();
        nickname=user.getNickname();
    }

}
