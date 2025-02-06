package com.ll.server.global.response.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ReturnCode {
    SUCCESS("0000", "Success"),
    SUCCESS_ADMIN("0001", "Success By Admin"),
    WRONG_PARAMETER("4000", "Wrong parameter"),
    NOT_FOUND_ENTITY("4001", "Not found entity"),
    ALREADY_EXIST("4002", "Already exist"),
    NOT_AUTHORIZED("4004", "Not authorized"),

    INTERNAL_ERROR("5000", "Unexpected internal error");

    private final String returnCode;
    private final String returnMessage;
}