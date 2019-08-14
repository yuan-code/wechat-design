package com.hualala.exception;

import com.hualala.common.ResultCode;
import lombok.Data;

@Data
public class BusinessException extends RuntimeException {

	private String code;
    private String msg;



    public BusinessException(String code, String msg) {
        super(msg);
        this.code = code;
        this.msg = msg;
    }

    public BusinessException(ResultCode resultCode) {
        super(resultCode.getMsg());
        this.code = resultCode.getCode();
        this.msg = resultCode.getMsg();
    }



}
