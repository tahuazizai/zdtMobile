package com.zdt.module.config.filter;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import com.google.common.base.Strings;
import com.zdt.module.utils.EscapeUtil;
import org.apache.commons.text.StringEscapeUtils;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * @version: 1.00.00
 * @description: XssHttpServletRequestWrapper替换request
 * @copyright: Copyright (c) 2021 立林科技 All Rights Reserved
 * @company: 厦门立林科技有限公司
 * @author: hj
 * @date: 2021-06-10 16:19
 */
public class XssHttpServletRequestWrapper extends HttpServletRequestWrapper {

    public XssHttpServletRequestWrapper(HttpServletRequest request) {
        super(request);
    }

    @Override
    public String[] getParameterValues(String name) {
        String[] values = super.getParameterValues(name);
        if (values != null) {
            int length = values.length;
            String[] escapseValues = new String[length];
            for (int i = 0; i < length; i++) {
                // 防xss攻击和过滤前后空格
                escapseValues[i] = Jsoup.clean(values[i], Whitelist.relaxed()).trim();
            }
            return escapseValues;
        }
        return super.getParameterValues(name);
    }

    @Override
    public String getParameter(String name) {
        if (!Strings.isNullOrEmpty(name)) {
            return Jsoup.clean(super.getParameter(name), Whitelist.relaxed()).trim();
        }
        return super.getParameter(name);
    }

//    @Override
//    public ServletInputStream getInputStream() throws IOException {
//        // 非json类型，直接返回
//        if (!isJsonRequest()) {
//            return super.getInputStream();
//        }
//
//        // 为空，直接返回
//        String json = IoUtil.read(super.getInputStream(), "utf-8");
//        if (StrUtil.isEmpty(json)) {
//            return super.getInputStream();
//        }
//
//        // xss过滤
//        json = StringEscapeUtils.escapeHtml4(json);
//        final ByteArrayInputStream bis = new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8));
//        return new ServletInputStream() {
//            @Override
//            public boolean isFinished() {
//                return true;
//            }
//
//            @Override
//            public boolean isReady() {
//                return true;
//            }
//
//            @Override
//            public void setReadListener(ReadListener readListener) {
//            }
//
//            @Override
//            public int read() {
//                return bis.read();
//            }
//        };
//    }
//
//    /**
//     * 是否是Json请求
//     */
//    private boolean isJsonRequest() {
//        String header = super.getHeader(HttpHeaders.CONTENT_TYPE);
//        return MediaType.APPLICATION_JSON_VALUE.equalsIgnoreCase(header)
//                || MediaType.APPLICATION_JSON_UTF8_VALUE.equalsIgnoreCase(header);
//    }
}
