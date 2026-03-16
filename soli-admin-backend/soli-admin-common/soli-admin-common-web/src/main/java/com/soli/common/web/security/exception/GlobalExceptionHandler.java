package com.soli.common.web.security.exception;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.soli.common.api.vo.ApiResponse;

/**
 * @author lizhengqiang
 * @since 2026-03-16 20:15
*/
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = Exception.class)
    public ApiResponse<String> exception(Exception e) {
        e.printStackTrace();
        return ApiResponse.fail(e.getMessage());
    }

}