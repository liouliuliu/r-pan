package com.liuhf.pan.core.exception;

/**
 * @author: lhf
 * @date: 2023/12/5 21:42
 * @description 技术组件层面的异常对象
 */
public class RPanFrameworkException extends RuntimeException {

    public RPanFrameworkException(String message) {
        super(message);
    }

}