package com.ll.server.domain.member.dto;

import com.ll.server.domain.member.enums.MemberRole;
import com.ll.server.domain.member.enums.Provider;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@NoArgsConstructor
@Getter
@Setter
@Data
@Builder
@AllArgsConstructor
public class MemberRequest {

    @NotBlank
    private String email;

    @NotBlank
    private String password;

    @NotNull
    private MemberRole role;


    @NotBlank
    private String nickname;

    @NotNull
    private Provider provider;

    @NotBlank
    private String providerId;



}

