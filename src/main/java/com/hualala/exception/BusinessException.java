package com.hualala.exception;

import lombok.Data;

@Data
public class BusinessException extends RuntimeException {

	private String code;
    private String msg;



    public BusinessException(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }


}
