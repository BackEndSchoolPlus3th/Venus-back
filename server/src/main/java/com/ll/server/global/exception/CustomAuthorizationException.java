package com.ll.server.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CustomAuthorizationException extends RuntimeException {
    private final ErrorCode errorCode;
}
