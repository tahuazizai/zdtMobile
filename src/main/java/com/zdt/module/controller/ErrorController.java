package com.zdt.module.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/error")
public class ErrorController {
    /**
     * 过滤器异常处理
     * @param request
     */
    @RequestMapping("//exthrow")
    public void retThrow(HttpServletRequest request) throws Exception {
        throw ((Exception) request.getAttribute("filter.error"));
    }
}
