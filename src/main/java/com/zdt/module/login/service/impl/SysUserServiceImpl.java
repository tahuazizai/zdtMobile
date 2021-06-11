package com.zdt.module.login.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zdt.module.login.entity.SysUser;
import com.zdt.module.login.mapper.SysUserMapper;
import com.zdt.module.login.service.SysUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @version: 1.00.00
 * @description: 用户服务实现
 * @copyright: Copyright (c) 2021 立林科技 All Rights Reserved
 * @company: 厦门立林科技有限公司
 * @author: hj
 * @date: 2021-06-11 9:14
 */
@Service
@Slf4j
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {
    @Override
    public SysUser querySysUser(SysUser sysUser) {
        return null;
    }
}
