package com.ll.server.global.response.exception;

import com.ll.server.global.response.enums.ReturnCode;
import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {

  private ReturnCode returnCode;
  private String returnMessage;

  public CustomException(ReturnCode returnCode) {
    this.returnCode = returnCode;
    this.returnMessage = returnCode.getReturnMessage();
  }
}
