package com.soli.common.api.exception;

/**
 * 业务异常类
 *
 * @author lizhengqiang
 * @since 2026-03-09 21:42
*/
public class BusinessException extends RuntimeException {

    public BusinessException(String message) {
        super(message);
    }

}