package com.example.frontwebmvc.common.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class WebLiteExceptionLoggingFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        try {
            chain.doFilter(request, response);
        } catch (IOException | ServletException e) {
            log.warn("[WEB][PRE] Exception: {}", e.getMessage());
            throw e;
        } catch (RuntimeException e) {
            log.warn("[WEB][PRE] Exception: {}", e.getMessage());
            throw e;
        }
    }

}
