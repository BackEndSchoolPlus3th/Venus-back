package com.ll.server.domain.member.dto;

import com.ll.server.domain.member.MemberRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@Data
public class MemberRequest {

    @NotBlank
    private String email;

    @NotBlank
    private String password;

    @NotNull
    private MemberRole role;

    @NotBlank
    private String name;

    @NotBlank
    private String nickname;

    @NotBlank
    private String providerId;



}

