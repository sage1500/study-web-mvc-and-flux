package com.example.frontwebmvc.common.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.WebAttributes;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import lombok.extern.slf4j.Slf4j;

/**
 * ログインエラーコントローラ.
 */
@Controller
@RequestMapping("/loginerror")
@Slf4j
public class LoginErrorController {

    /**
     * 認証エラーの HTTPステータス.
     * 
     * ※Spring Security のデフォルト実装だと 200 OK. 適切なステータスがないがとりあえず 401 Unauthorized を設定。
     */
    private final HttpStatus RESPONSE_STATUS = HttpStatus.UNAUTHORIZED;

    /**
     * エラーハンドラ(HTML用).
     * 
     * @param request HTTP要求
     * @return ModelAndView
     */
    @RequestMapping(produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView errorHtml(HttpServletRequest request) {
        var model = getModel(request);
        return new ModelAndView("error", model, RESPONSE_STATUS);
    }

    /**
     * エラーハンドラ(非HTML用).
     * 
     * @param request HTTP要求
     * @return ResponseEntity
     */
    @RequestMapping
    public ResponseEntity<Map<String, Object>> error(HttpServletRequest request) {
        var model = getModel(request);
        return new ResponseEntity<>(model, RESPONSE_STATUS);
    }

    /**
     * Model取得.
     * 
     * @param request HTTP要求
     * @return Model
     */
    private Map<String, Object> getModel(HttpServletRequest request) {
        var exception = getException(request);
        var message = (exception == null) ? null : exception.getMessage();
        var path = request.getRequestURI();

        Map<String, Object> model = new HashMap<>();
        model.put("exception", exception);
        model.put("message", message);
        model.put("path", path);

        log.warn("error-model: {}", model);

        // メモ: KeyCloak の場合
        //
        // - ユーザが認可しなかった場合
        // -- message : [access_denied]
        //
        // - redirect_url に想定していない応答があった場合
        // -- message : [authorization_request_not_found]
        //
        // - userinfo 取得時に keycloak から 401応答
        // -- message : [invalid_user_info_response] An error occurred while attempting
        // to retrieve the UserInfo Resource: 401 Unauthorized:
        // [{"error":"invalid_token","error_description":"Token verification failed"}]

        return model;
    }

    /**
     * 例外取得.
     * 
     * request または session に格納されている認証例外を取得する。なお、認証例外の格納は、Spring Security の
     * SimpleUrlAuthenticationFailureHandler が実施する。
     * 
     * @param request HTTP要求
     * @return 認証例外
     */
    private Throwable getException(HttpServletRequest request) {
        var exception = (Throwable) request.getAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
        if (exception == null) {
            var session = request.getSession(false);
            if (session != null) {
                exception = (Throwable) session.getAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
            }
        }
        return exception;
    }
}
