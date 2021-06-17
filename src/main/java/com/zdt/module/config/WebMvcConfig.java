package com.zdt.module.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

/**
 * @version: 1.00.00
 * @description: json对象脚本攻击拦截
 * @copyright: Copyright (c) 2021 立林科技 All Rights Reserved
 * @company: 厦门立林科技有限公司
 * @author: hj
 * @date: 2021-06-17 19:16
 */
@Configuration
public class WebMvcConfig extends WebMvcConfigurationSupport {
    @Override
    protected void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        ListIterator<HttpMessageConverter<?>> listIterator = converters.listIterator();
        while(listIterator.hasNext()) {
            HttpMessageConverter<?> next = listIterator.next();
            if(next instanceof MappingJackson2HttpMessageConverter) {
                listIterator.remove();
                break;
            }
        }
        converters.add(getMappingJackson2HttpMessageConverter());
    }


    public MappingJackson2HttpMessageConverter  getMappingJackson2HttpMessageConverter() {
        //解析器
        ObjectMapper objectMapper = Jackson2ObjectMapperBuilder.json().applicationContext(this.getApplicationContext()).build();
        //注册xss解析器
        SimpleModule xssModule = new SimpleModule("XssStringJsonSerializer");
        xssModule.addDeserializer(String.class, new XssStringJsonSerializer(String.class));
        objectMapper.registerModule(xssModule);
        MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter = new MappingJackson2HttpMessageConverter();
        mappingJackson2HttpMessageConverter.setObjectMapper(objectMapper);
        List<MediaType> list = new ArrayList<>();
        list.add(MediaType.APPLICATION_JSON_UTF8);
        mappingJackson2HttpMessageConverter.setSupportedMediaTypes(list);
        return mappingJackson2HttpMessageConverter;
    }
}
