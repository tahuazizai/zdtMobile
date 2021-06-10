package com.zdt.module.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @version: 1.00.00
 * @description: 跨域访问配置
 * @copyright: Copyright (c) 2021 立林科技 All Rights Reserved
 * @company: 厦门立林科技有限公司
 * @author: hj
 * @date: 2021-06-08 19:58
 */
@Configuration
public class CrosConfig implements WebMvcConfigurer {
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**").
                        //允许跨域的域名，可以用*表示允许任何域名使用
                                allowedOrigins("*").
                        //允许任何方法（post、get等）
                                allowedMethods("*").
                        //允许任何请求头
                                allowedHeaders("*").
                        //带上cookie信息
                                allowCredentials(true).
                        //maxAge(3600)表明在3600秒内，不需要再发送预检验请求，可以缓存该结果
                                exposedHeaders(HttpHeaders.SET_COOKIE).maxAge(3600L);
            }
        };
    }
}
