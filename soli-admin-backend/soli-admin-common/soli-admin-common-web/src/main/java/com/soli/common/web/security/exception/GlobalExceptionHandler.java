package com.soli.common.web.security.exception;

import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.soli.common.api.vo.Result;

/**
 * @author lizhengqiang
 * @since 2026-03-16 20:15
*/
@RestControllerAdvice
@Hidden
public class GlobalExceptionHandler {

    @ExceptionHandler(value = Exception.class)
    public Result<String> exception(Exception e) {
        e.printStackTrace();
        return Result.fail(e.getMessage());
    }

}