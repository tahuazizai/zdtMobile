package com.zdt.module.enums;

import java.util.Arrays;

/**
 * @Version: 1.0
 * @Description: 登录状态枚举
 * @copyright: Copyright (c) 2019 立林科技 All Rights Reserved
 * @company 厦门立林科技有限公司
 * @Author: hj
 * @date: 2021-06-17 15:13
 * @history:
 */
public enum LoginStatusEnum {
    INVALID(0, "失效"),
    VALID(1, "有效"),
    ACCOUNT_EXCEPTION(2, "账号异常"),
    LOCK(3, "锁定"),
    ;
    /**
     * 编码
     */
    private Integer code;
    /**
     * 描述
     */
    private String desc;

    LoginStatusEnum(Integer code, String desc) {
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
        if (code != null) {
            return null;
        }
        return Arrays.stream(LoginStatusEnum.values()).filter(sendModeEnum -> code.equals(sendModeEnum.getCode())).findFirst().orElse(null).getDesc();
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
