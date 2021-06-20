package com.example.frontwebmvc.common.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@NoArgsConstructor
@Slf4j
public class WebLoggingFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        var path = req.getServletPath();

        // ログ除外
        if (path.startsWith("/css/") || path.startsWith("/js/")) {
            chain.doFilter(request, response);
            return;
        }

        log.info("[WEB] Request: {} {}", req.getMethod(), req.getRequestURI());

        try {
            chain.doFilter(request, response);
        } catch (IOException | ServletException e) {
            log.warn("[WEB] Exception: {}", e.getMessage());
            throw e;
        } catch (RuntimeException e) {
            log.warn("[WEB] Exception: {}", e.getMessage());
            throw e;
        }

        HttpServletResponse rsp = (HttpServletResponse) response;
        var location = rsp.getHeader("location");
        log.info("[WEB] Response: code={} location={}", rsp.getStatus(), location);
    }
}
