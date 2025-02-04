package com.ll.server.domain.member.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@Data
public class MemberLogin {

    @NotBlank
    private String email;

    @NotBlank
    private String password;


}