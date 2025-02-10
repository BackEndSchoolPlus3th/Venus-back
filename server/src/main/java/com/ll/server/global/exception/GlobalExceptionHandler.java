package com.ll.server.global.exception;

import com.ll.server.global.response.enums.ReturnCode;
import com.ll.server.global.response.exception.CustomException;
import com.ll.server.global.response.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    /*
     * Developer Custom Exception: 직접 정의한 RestApiException 에러 클래스에 대한 예외 처리
     */
    @ExceptionHandler(CustomException.class)
    protected ResponseEntity<Object>  handleCustomException(CustomException ex) {
        ReturnCode errorCode = ex.getReturnCode();
        return handleExceptionInternal(errorCode);
    };

    protected ResponseEntity<Object> handleExceptionInternal(ReturnCode errorCode) {
        return ResponseEntity.badRequest().body(ApiResponse.of(errorCode));
    }

    // handleExceptionInternal() 메소드를 오버라이딩해 응답 커스터마이징
//    private ApiResponse<?> handleExceptionInternal(ReturnCode errorCode) {
//        return ApiResponse.of(errorCode);
//    }
}
