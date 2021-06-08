package com.zdt.module;

import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;

/**
 * @version: 1.00.00
 * @description: jasypt加密和解密工具
 * @copyright: Copyright (c) 2021 立林科技 All Rights Reserved
 * @company: 厦门立林科技有限公司
 * @author: hj
 * @date: 2021-05-08 15:39
 */
public class JasyptUtil {

    /**
     * 加密
     *
     * @param password 加密秘钥
     * @param value 需要加密的密码
     * @return
     */
    public static String encyptPwd(String password, String value) {
        PooledPBEStringEncryptor encryptor = configEncryptor(password);
        String result = encryptor.encrypt(value);
        return result;
    }

    /**
     * 解密
     *
     * @param password 解密秘钥
     * @param value 需要解密的密码
     * @return
     */
    public static String decyptPwd(String password, String value) {
        PooledPBEStringEncryptor encryptor = configEncryptor(password);
        String result = encryptor.decrypt(value);
        return result;
    }

    /**
     * 配置
     *
     * @param password
     * @return
     */
    private static PooledPBEStringEncryptor configEncryptor(String password) {
        PooledPBEStringEncryptor encryptor = new PooledPBEStringEncryptor();
        SimpleStringPBEConfig config = new SimpleStringPBEConfig();
        config.setPassword(password);
        config.setAlgorithm("PBEWithMD5AndDES");
        config.setKeyObtentionIterations("1000");
        config.setPoolSize("1");
        config.setProviderName("SunJCE");
        config.setSaltGeneratorClassName("org.jasypt.salt.RandomSaltGenerator");
        config.setStringOutputType("base64");
        encryptor.setConfig(config);
        return encryptor;
    }

    public static void main(String[] args) {
        // 加密
        String encPwd1 = encyptPwd("zdt", "leelenmanage123QAZ");

        String encPwd3 = encyptPwd("zdt", "Leelen@123");

        String encPwd4 = encyptPwd("zdt", "leelendb");

        // 加密
        String encPwd2 = decyptPwd("zdt", encPwd1);

        System.out.println(encPwd1);
        System.out.println(encPwd2);
        System.out.println(encPwd3);
        System.out.println(encPwd4);
    }
}
