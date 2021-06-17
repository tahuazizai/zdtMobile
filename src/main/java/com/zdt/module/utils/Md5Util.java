package com.zdt.module.utils;

import jodd.util.PropertiesUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.MessageDigest;

/**
 * @version: 1.00.00
 * @description: md5加密
 * @copyright: Copyright (c) 2021 立林科技 All Rights Reserved
 * @company: 厦门立林科技有限公司
 * @author: hj
 * @date: 2021-06-11 15:55
 */
@Component
public class Md5Util {
    @Value("password.salt")
    private String salt;

    private static final String hexDigits[] = {"0", "1", "2", "3", "4", "5",
            "6", "7", "8", "9", "a", "b", "c", "d", "e", "f"};

    /**
     * 这里主要是遍历8个byte，转化为16位进制的字符，即0-F
     * @param b
     * @return
     */
    private static String byteArrayToHexString(byte b[]) {
        StringBuffer resultSb = new StringBuffer();
        for (int i = 0; i < b.length; i++)
            resultSb.append(byteToHexString(b[i]));

        return resultSb.toString();
    }


    /**
     * 这里是针对单个byte，256的byte通过16拆分为d1和d2
     * @param b
     * @return
     */
    private static String byteToHexString(byte b) {
        int n = b;
        if (n < 0)
            n += 256;
        int d1 = n / 16;
        int d2 = n % 16;
        return hexDigits[d1] + hexDigits[d2];
    }

    /**
     * 返回大写MD5
     *
     * @param origin
     * @param charsetname
     * @return
     */
    private String MD5Encode(String origin, String charsetname) throws Exception {
        String resultString = origin;
        MessageDigest md = MessageDigest.getInstance("MD5");
        if (charsetname == null || "".equals(charsetname))
            resultString = byteArrayToHexString(md.digest(resultString.getBytes()));
        else {
            resultString = byteArrayToHexString(md.digest(resultString.getBytes(charsetname)));
        }
        return resultString.toUpperCase();
    }

    public String MD5EncodeUtf8(String origin, String salt) throws Exception {
        origin = origin + salt;
        return MD5Encode(origin, "utf-8");
    }
}
