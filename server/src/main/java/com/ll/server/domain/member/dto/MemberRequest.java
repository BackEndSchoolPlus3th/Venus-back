package com.ll.server.domain.member.dto;

import com.ll.server.domain.member.MemberRole;
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

//    @NotBlank
//    private String name;

    @NotBlank
    private String nickname;

    @NotBlank
    private String providerId;



}

