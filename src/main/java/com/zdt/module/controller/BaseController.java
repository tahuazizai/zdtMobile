package com.zdt.module.controller;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zdt.module.constants.CommonConstant;
import com.zdt.module.login.entity.Account;
import com.zdt.module.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;

/**
 * @version: 1.00.00
 * @description: 基础Controller
 * @copyright: Copyright (c) 2021 立林科技 All Rights Reserved
 * @company: 厦门立林科技有限公司
 * @author: hj
 * @date: 2021-06-11 8:54
 */
public class BaseController<T, R extends IService<T>> {

    @Autowired
    private R iService;

    @Autowired
    protected RedisUtil redisUtil;

    @Autowired
    protected HttpServletRequest request;

    /**
     * 获取用户信息
     *
     * @return
     */
    protected Account getUserInfo() {
        String csrfToken = request.getHeader(CommonConstant.CSRF_TOKEN);
        Account sysUser = (Account) redisUtil.get(CommonConstant.TOKEN_PREFIX + csrfToken);
        return sysUser;
    }

}
