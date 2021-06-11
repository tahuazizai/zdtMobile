package com.zdt.module.login.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zdt.module.login.entity.SysUser;

/**
 * @Version: 1.0
 * @Description: 用户服务接口
 * @copyright: Copyright (c) 2019 立林科技 All Rights Reserved
 * @company 厦门立林科技有限公司
 * @Author: hj
 * @date: 2021-06-11 09:13
 * @history:
 */
public interface SysUserService extends IService<SysUser> {
    SysUser querySysUser(SysUser sysUser);
}
