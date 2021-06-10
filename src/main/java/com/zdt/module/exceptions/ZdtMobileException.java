package com.zdt.module.exceptions;


import com.zdt.module.enums.ErrorCodeEnum;

import java.io.Serializable;

/**
 * @version: 1.00.00
 * @description: 协议转换异常
 * @copyright: Copyright (c) 2021 立林科技 All Rights Reserved
 * @company: 厦门立林科技有限公司
 * @author: hj
 * @date: 2021-05-17 18:34
 */
public class ZdtMobileException extends Exception implements Serializable {

    private static final long serialVersionUID = 2451837446265925557L;
    /**
     * 错误编码
     */
    private Integer errorCode;

    /**
     * 错误信息
     */
    private String errorMsg;

    public ZdtMobileException(Integer errorCode, String errorMsg) {
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }

    public ZdtMobileException(ErrorCodeEnum errorCodeEnum) {
        this(errorCodeEnum.getCode(), errorCodeEnum.getDesc());
    }

    public Integer getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(Integer errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }
}
