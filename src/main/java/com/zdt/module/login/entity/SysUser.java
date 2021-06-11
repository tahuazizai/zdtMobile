package com.zdt.module.login.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @version: 1.00.00
 * @description: 用户实体
 * @copyright: Copyright (c) 2021 立林科技 All Rights Reserved
 * @company: 厦门立林科技有限公司
 * @author: hj
 * @date: 2021-06-11 9:10
 */
@Data
@ApiModel("用户")
public class SysUser implements Serializable {
    private static final long serialVersionUID = -7236267653852495956L;
    /**
     * 用户名
     */
    @ApiModelProperty(value = "用户名")
    private String userName;

    /**
     * 密码
     */
    @ApiModelProperty(value = "密码")
    private String password;
    /**
     * 主键
     */
    @ApiModelProperty(value = "主键")
    private String id;
}
