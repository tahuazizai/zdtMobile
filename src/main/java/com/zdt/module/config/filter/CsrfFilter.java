package com.zdt.module.config.filter;

import cn.hutool.core.util.StrUtil;
import com.google.common.base.Strings;
import com.zdt.module.constants.CommonConstant;
import com.zdt.module.enums.ErrorCodeEnum;
import com.zdt.module.exceptions.ZdtMobileSelevetException;
import com.zdt.module.utils.RedisUtil;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @version: 1.00.00
 * @description: csrf过滤器
 * @copyright: Copyright (c) 2021 立林科技 All Rights Reserved
 * @company: 厦门立林科技有限公司
 * @author: hj
 * @date: 2021-06-10 16:55
 */
public class CsrfFilter implements Filter {
    /**
     * 排除链接
     */
    private List<String> excludes = new ArrayList<>();
    @Autowired
    private RedisUtil redisUtil;

    /**
     * xss过滤开关
     */
    private boolean enabled = false;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        if (matches(httpServletRequest)) {
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        } else {
            String csrfToken = httpServletRequest.getHeader(CommonConstant.CSRF_TOKEN);
            if (Strings.isNullOrEmpty(csrfToken)) {
                throw new ZdtMobileSelevetException(ErrorCodeEnum.TOKEN_IS_NULL_EXCEPTION);
            }
            Object token = redisUtil.get(CommonConstant.TOKEN_PREFIX + csrfToken);
            if (token == null) {
                throw new ZdtMobileSelevetException(ErrorCodeEnum.TOKEN_EXCEPTION);
            }
            redisUtil.expire(CommonConstant.TOKEN_PREFIX + csrfToken, CommonConstant.TOKEN_EXPIRE_TIME);
            filterChain.doFilter(servletRequest, servletResponse);
        }


    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        String tempExcludes = filterConfig.getInitParameter("excludes");
        String tempEnabled = filterConfig.getInitParameter("enabled");
        if (StrUtil.isNotEmpty(tempExcludes)) {
            String[] url = tempExcludes.split(",");
            Collections.addAll(excludes, url);
        }
        if (StrUtil.isNotEmpty(tempEnabled)) {
            enabled = Boolean.valueOf(tempEnabled);
        }
    }

    /**
     * 校验是否匹配路径
     *
     * @param request
     * @return
     */
    public Boolean matches(HttpServletRequest request) {
        if (!enabled) {
            return true;
        }
        if (CollectionUtils.isNotEmpty(excludes)) {
            for (String url : excludes) {
                AntPathRequestMatcher antPathRequestMatcher = new AntPathRequestMatcher(url);
                if (antPathRequestMatcher.matches(request)) {
                    return true;
                }
            }
        }
        return false;
    }
}
