package com.liuhf.pan.core.exception;

import com.liuhf.pan.core.response.ResponseCode;

/**
 * @author: lhf
 * @date: 2023/11/29 22:01
 * @description 自定义全局业务异常类
 */
public class RPanBusinessException extends RuntimeException{


    /**
     * 错误码
     */
    private Integer code;

    /**
     * 错误信息
     */
    private String message;

    public RPanBusinessException(ResponseCode responseCode) {
        this.code = responseCode.getCode();
        this.message = responseCode.getDesc();
    }

    public RPanBusinessException(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public RPanBusinessException(String message) {
        this.code = ResponseCode.ERROR_PARAM.getCode();
        this.message = message;
    }

    public RPanBusinessException() {
        this.code = ResponseCode.ERROR_PARAM.getCode();
        this.message = ResponseCode.ERROR_PARAM.getDesc();
    }

}
