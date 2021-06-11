package com.zdt.module.login.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.google.common.base.Strings;
import com.zdt.module.constants.CommonConstant;
import com.zdt.module.controller.BaseController;
import com.zdt.module.enums.ErrorCodeEnum;
import com.zdt.module.login.entity.SysUser;
import com.zdt.module.login.service.SysUserService;
import com.zdt.module.utils.CodeUtil;
import com.zdt.module.utils.JWTUtil;
import com.zdt.module.utils.Md5Util;
import com.zdt.module.utils.RSAEncryptUtil;
import com.zdt.module.vo.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.RenderedImage;
import java.io.OutputStream;
import java.util.Map;

/**
 * @version: 1.00.00
 * @description: 登录控制器
 * @copyright: Copyright (c) 2021 立林科技 All Rights Reserved
 * @company: 厦门立林科技有限公司
 * @author: hj
 * @date: 2021-06-11 9:33
 */
@Api("main")
@RestController
@RequestMapping("/main")
@Slf4j
public class LoginController extends BaseController<SysUser, SysUserService> {
    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private Md5Util md5Util;

    /**
     * 登录
     *
     * @param userName
     * @param password
     * @return
     */
    @ApiOperation("登录")
    @GetMapping("/login")
    public Result<?> login(@ApiParam("用户名") @RequestParam String userName, @ApiParam("密码") @RequestParam String password, @ApiParam("验证码") @RequestParam String code) throws Exception {
        String redisCode = (String) redisUtil.get(CommonConstant.CODE_PREFIX + userName);
        if (Strings.isNullOrEmpty(redisCode)) {
            return Result.error(ErrorCodeEnum.CODE_INVALID_EXCEPTION);
        }
        if (!code.equals(redisCode)) {
            return Result.error(ErrorCodeEnum.CODE_ERROR_EXCEPTION);
        }
        SysUser sysUser = (SysUser) redisUtil.get(CommonConstant.USER_ID_PREFIX + userName);
        if (sysUser == null) {
            LambdaQueryWrapper<SysUser> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(SysUser::getUserName, userName);
            sysUser = this.sysUserService.getOne(lambdaQueryWrapper);
            if (sysUser == null) {
                return Result.error(ErrorCodeEnum.USER_NOT_EXIST_EXCEPTION);
            } else {
                String privateKey = (String) redisUtil.get(CommonConstant.PRIVATE_KEY_PREFIX + userName);
                if (Strings.isNullOrEmpty(privateKey)) {
                    return Result.error(ErrorCodeEnum.USER_PASSWORD_INVALID_EXCEPTION);
                }
                String rsaPassword = RSAEncryptUtil.decrypt(password, privateKey);
                String md5Password = md5Util.MD5EncodeUtf8(rsaPassword);
                if (!md5Password.equals(sysUser.getPassword())) {
                    return Result.error(ErrorCodeEnum.USER_PASSWORD_ERROR_EXCEPTION);
                }

            }
        }
        String token = JWTUtil.createToken(sysUser.getId(), userName);
        redisUtil.set(CommonConstant.USER_ID_PREFIX + userName, sysUser, CommonConstant.TOKEN_EXPIRE_TIME);
        redisUtil.set(CommonConstant.CSRF_TOKEN + token, sysUser, CommonConstant.TOKEN_EXPIRE_TIME);
        return Result.OK(token);
    }

    /**
     * 获取rsa公钥
     *
     * @return
     */
    @ApiOperation("获取rsa公钥")
    @GetMapping("/getRsaPublicKey")
    public Result<?> getRsaPublicKey(@ApiParam("用户名") @RequestParam String userName) throws Exception {
        Map<Integer, String> keyMap = RSAEncryptUtil.genKeyPair();
        redisUtil.set(CommonConstant.PRIVATE_KEY_PREFIX + userName, keyMap.get(1), CommonConstant.TOKEN_EXPIRE_TIME);
        return Result.OK(keyMap.get(0));
    }

    /**
     * 退出
     *
     * @param userName
     * @return
     */
    @ApiOperation("退出")
    @GetMapping("/getRsaPublicKey")
    public Result<?> loginOut(@ApiParam("用户名") @RequestParam String userName) {
        String csrfToken = request.getHeader(CommonConstant.CSRF_TOKEN);
        redisUtil.del(CommonConstant.USER_ID_PREFIX + userName, CommonConstant.CSRF_TOKEN + csrfToken);
        return Result.OK(Boolean.TRUE);
    }

    /**
     * @param userName
     * @return
     */
    @ApiOperation("获取验证码")
    @GetMapping("/getCode")
    public void getCode(@ApiParam("用户名") @RequestParam String userName, HttpServletResponse resp) throws Exception {
        Map<String, Object> map = CodeUtil.generateCodeAndPic();
        redisUtil.set(CommonConstant.CODE_PREFIX + userName, map.get("code"), CommonConstant.CODE_EXPIRE_TIME);
        // 禁止图像缓存。
        resp.setHeader("Pragma", "no-cache");
        resp.setHeader("Cache-Control", "no-cache");
        resp.setDateHeader("Expires", -1);
        resp.setContentType("image/jpeg");
        OutputStream out = resp.getOutputStream();
        ImageIO.write((RenderedImage) map.get("codePic"), "jpeg", out);
    }

    /**
     * 注册
     *
     * @param sysUser
     * @param code
     * @return
     */
    @ApiOperation("注册")
    @PostMapping("/register")
    public Result<?> register(@RequestBody SysUser sysUser, @ApiParam("验证码") @RequestParam String code) throws Exception {
        String redisCode = (String) redisUtil.get(CommonConstant.CODE_PREFIX + sysUser.getUserName());
        if (Strings.isNullOrEmpty(redisCode)) {
            return Result.error(ErrorCodeEnum.CODE_INVALID_EXCEPTION);
        }
        if (!code.equals(redisCode)) {
            return Result.error(ErrorCodeEnum.CODE_ERROR_EXCEPTION);
        }
        String privateKey = (String) redisUtil.get(CommonConstant.PRIVATE_KEY_PREFIX + sysUser.getUserName());
        if (Strings.isNullOrEmpty(privateKey)) {
            return Result.error(ErrorCodeEnum.USER_PASSWORD_INVALID_EXCEPTION);
        }
        String rsaPassword = RSAEncryptUtil.decrypt(sysUser.getPassword(), privateKey);
        String md5Password = md5Util.MD5EncodeUtf8(rsaPassword);
        sysUser.setPassword(md5Password);
        this.sysUserService.save(sysUser);
        return Result.OK(Boolean.TRUE);
    }
}
