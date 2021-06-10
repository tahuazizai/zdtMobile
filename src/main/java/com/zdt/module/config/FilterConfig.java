package com.zdt.module.config;

import cn.hutool.core.util.StrUtil;
import com.zdt.module.config.filter.CsrfFilter;
import com.zdt.module.config.filter.XssFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import javax.servlet.DispatcherType;
import java.util.HashMap;
import java.util.Map;

/**
 * @version: 1.00.00
 * @description: 过滤器配置
 * @copyright: Copyright (c) 2021 立林科技 All Rights Reserved
 * @company: 厦门立林科技有限公司
 * @author: hj
 * @date: 2021-06-10 16:46
 */
@Configuration
public class FilterConfig {
    @Value("${xss.enabled}")
    private String enabled;

    @Value("${xss.excludes}")
    private String excludes;

    @Value("${xss.urlPatterns}")
    private String urlPatterns;

    @Value("${csrf.enabled}")
    private String csrfEnabled;

    @Value("${csrf.excludes}")
    private String csrfExcludes;

    @Value("${csrf.urlPatterns}")
    private String csrfUrlPatterns;
    @Bean
    @Order(2)
    public FilterRegistrationBean xssFilterRegistration() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setDispatcherTypes(DispatcherType.REQUEST);
        registration.setFilter(new XssFilter());
        //添加过滤路径
        registration.addUrlPatterns(StrUtil.split(urlPatterns, ","));
        registration.setName("xssFilter");
        //设置初始化参数
        Map<String, String> initParameters = new HashMap<String, String>();
        initParameters.put("excludes", excludes);
        initParameters.put("enabled", enabled);
        registration.setInitParameters(initParameters);
        return registration;
    }
    @Bean
    @Order(1)
    public FilterRegistrationBean csrfFilterRegistration() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(new CsrfFilter());
        //添加过滤路径
        registration.addUrlPatterns(StrUtil.split(csrfUrlPatterns, ","));
        registration.setName("csrfFilter");
        //设置初始化参数
        Map<String, String> initParameters = new HashMap<String, String>();
        initParameters.put("excludes", csrfExcludes);
        initParameters.put("enabled", csrfEnabled);
        registration.setInitParameters(initParameters);
        return registration;
    }
}
