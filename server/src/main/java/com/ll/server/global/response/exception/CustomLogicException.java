package com.ll.server.global.response.exception;


import com.ll.server.global.response.enums.ReturnCode;

public class CustomLogicException extends CustomException{
  public CustomLogicException(ReturnCode returnCode) {
    super(returnCode);
  }
}
