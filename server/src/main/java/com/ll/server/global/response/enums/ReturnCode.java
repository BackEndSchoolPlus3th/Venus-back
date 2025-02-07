package com.ll.server.global.response.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ReturnCode {
    SUCCESS("0000", "Success"),

    WRONG_PARAMETER("4000", "Wrong parameter"),
    NOT_FOUND_ENTITY("4001", "Not found entity"),
    ALREADY_EXIST("4002", "Already exist"),
    NOT_AUTHORIZED("4004", "Not authorized"),

    INTERNAL_ERROR("5000", "Unexpected internal error"),

    // AUTH

    INVALID_INPUT_VALUE("4000", "잘못된 입력 값입니다."),
    INTERNAL_SERVER_ERROR("5000", "서버 에러"),

    // User
    USER_NOT_FOUND("4001", "유저를 찾을 수 없습니다."),
    DUPLICATED_EMAIL("4002", "중복된 이메일입니다."),
    INVALID_PASSWORD("4000", "잘못된 비밀번호입니다."),

    // Authentication
    INVALID_REFRESH_TOKEN("4004", "유효하지 않은 Refresh Token 입니다."),
    UNAUTHORIZED("4004", "인증에 실패했습니다.");

    private final String returnCode;
    private final String returnMessage;
}