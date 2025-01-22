package com.ll.server.global.response.exception;


import com.ll.server.global.response.enums.ReturnCode;

public class CustomRequestException extends CustomException {
    public CustomRequestException(ReturnCode returnCode) {
        super(returnCode);
    }
}