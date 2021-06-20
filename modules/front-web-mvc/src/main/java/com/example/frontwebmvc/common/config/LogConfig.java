package com.example.frontwebmvc.common.config;

import com.example.frontwebmvc.common.filter.WebLoggingFilter;

import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LogConfig {
    
    @Bean
    public FilterRegistrationBean<?> loggingFilter(SecurityProperties secProp) {
        FilterRegistrationBean<?> bean = new FilterRegistrationBean<>(new WebLoggingFilter());
        bean.setOrder(secProp.getFilter().getOrder() - 1);
        return bean;
    }

}
