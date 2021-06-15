package com.zdt.module.enums;

import java.util.Arrays;

/**
 * @Version: 1.0
 * @Description: 错误编码枚举
 * @copyright: Copyright (c) 2019 立林科技 All Rights Reserved
 * @company 厦门立林科技有限公司
 * @Author: hj
 * @date: 2021-05-06 16:17
 * @history:
 */
public enum ErrorCodeEnum {
    SYSTEM_EXCEPTION(1001, "系统异常"),
    TOKEN_EXCEPTION(1002, "TOKEN失效"),
    TOKEN_IS_NULL_EXCEPTION(1003, "TOKEN为null"),
    USER_NOT_EXIST_EXCEPTION(1004, "用户不存在"),
    USER_PASSWORD_INVALID_EXCEPTION(1005, "用户密码失效"),
    USER_PASSWORD_ERROR_EXCEPTION(1006, "用户密码错误"),
    CODE_INVALID_EXCEPTION(1007, "验证码无效"),
    CODE_ERROR_EXCEPTION(1008, "验证码错误"),
    FIND_NO_RECORD_EXCEPTION(1009, "没有对应记录"),
    ;
    /**
     * 编码
     */
    private Integer code;
    /**
     * 描述
     */
    private String desc;

    ErrorCodeEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    /**
     * @Description:根据编码获取描述信息
     * @Author: huangjin@2021/5/6 16:26
     * @Param: [code]
     * @return: java.lang.String
     * @throws:
     **/
    public static String getByCode(Integer code) {
        if (code == null) {
            return null;
        }
        return Arrays.stream(ErrorCodeEnum.values()).filter(sendModeEnum -> code.equals(sendModeEnum.getCode())).findFirst().orElse(null).getDesc();
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
