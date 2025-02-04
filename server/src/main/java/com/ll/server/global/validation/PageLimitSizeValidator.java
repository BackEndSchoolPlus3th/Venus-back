package com.ll.server.global.validation;

import com.ll.server.global.response.enums.ReturnCode;
import com.ll.server.global.response.exception.CustomRequestException;

public class PageLimitSizeValidator {
    public static void validateSize(int page, int limit, int maxLimitSize) {
        if (page < 0 || limit <= 0 || limit > maxLimitSize) {
            throw new CustomRequestException(ReturnCode.WRONG_PARAMETER);
        }
    }
}
