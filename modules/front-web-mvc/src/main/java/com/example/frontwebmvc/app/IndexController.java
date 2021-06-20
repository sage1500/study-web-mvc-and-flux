package com.example.frontwebmvc.app;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.web.WebAttributes;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/")
@Slf4j
public class IndexController {

    @GetMapping
    public String index() {
        log.info("index");
        return "index";
    }

    @GetMapping(params = "login-error")
    public String loginError(Model model, HttpServletRequest request) {
        log.info("login-error: {}", request.getAttribute(WebAttributes.AUTHENTICATION_EXCEPTION));
        log.info(" request: {}", request);

        log.info("1: {}", request.getContextPath());
        log.info("2: {}", request.getRequestURI());
        log.info("3: {}", request.getUserPrincipal());
        log.info("4: {}", request.getServerName());
        log.info("5: {}", request.getServletPath());

        var exception = (Exception) request.getAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
        exception = (exception != null) ? exception : (Exception) request.getSession().getAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);

        var message = (exception == null) ? null : exception.getMessage();

        model.addAttribute("exception", exception);
        model.addAttribute("message", message);
        model.addAttribute("path", request.getRequestURI());

        if (exception instanceof OAuth2AuthenticationException) {
            var oe = (OAuth2AuthenticationException) exception;
            var err = oe.getError();
            log.info("code={} uri={} desc={}", err.getErrorCode(), err.getUri(), err.getDescription());
        }

        // - ユーザが認可しなかった場合
        // - message : [access_denied]
        // - redirect_url に想定していない応答があった場合
        // - message : [authorization_request_not_found]
        // - userinfo 取得時に keycloak から 401応答
        // - message : [invalid_user_info_response] An error occurred while attempting
        // to retrieve the UserInfo Resource: 401 Unauthorized:
        // [{"error":"invalid_token","error_description":"Token verification failed"}]

        return "error";
    }
}
