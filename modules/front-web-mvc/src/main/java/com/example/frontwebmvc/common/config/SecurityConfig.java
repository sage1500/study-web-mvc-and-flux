package com.example.frontwebmvc.common.config;

import java.util.function.Consumer;

import com.example.frontwebmvc.common.filter.WebLiteExceptionLoggingFilter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.client.oidc.web.logout.OidcClientInitiatedLogoutSuccessHandler;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.web.access.ExceptionTranslationFilter;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private final ClientRegistrationRepository clientRegistrationRepository;

    @Value("${server.servlet.session.cookie.name:JSESSIONID}")
    private String sessionCookieName;

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        // ログ
        http.addFilterAfter(new WebLiteExceptionLoggingFilter(), ExceptionTranslationFilter.class);

        // 認可設定
        // @formatter:off
        http.authorizeRequests()
            .antMatchers("/").permitAll()
            .antMatchers("/loginerror").permitAll()
            .antMatchers("/manage/**").permitAll()
            .anyRequest().authenticated();        
        // @formatter:on

        // OAuth2 ログイン
        // @formatter:off
        http.oauth2Login(oauth2 -> oauth2
            .failureHandler(new SimpleUrlAuthenticationFailureHandler("/loginerror"))
            .authorizationEndpoint(authorization -> authorization
                .authorizationRequestResolver(authorizationRequestResolver(clientRegistrationRepository))));
        // @formatter:on

        // ログアウト
        http.logout(logout -> {
            logout.deleteCookies(sessionCookieName);
            logout.logoutSuccessHandler(oidcLogoutSuccessHandler());
        });
    }

    private LogoutSuccessHandler oidcLogoutSuccessHandler() {
        OidcClientInitiatedLogoutSuccessHandler logoutSuccessHandler = new OidcClientInitiatedLogoutSuccessHandler(
                this.clientRegistrationRepository);
        logoutSuccessHandler.setPostLogoutRedirectUri("{baseUrl}/");
        return logoutSuccessHandler;
    }

    private OAuth2AuthorizationRequestResolver authorizationRequestResolver(
            ClientRegistrationRepository clientRegistrationRepository) {
        DefaultOAuth2AuthorizationRequestResolver authorizationRequestResolver = new DefaultOAuth2AuthorizationRequestResolver(
                clientRegistrationRepository, "/oauth2/authorization");
        authorizationRequestResolver.setAuthorizationRequestCustomizer(authorizationRequestCustomizer());
        return authorizationRequestResolver;
    }

    private Consumer<OAuth2AuthorizationRequest.Builder> authorizationRequestCustomizer() {
        return customizer -> customizer.additionalParameters(params -> {
            var locale = LocaleContextHolder.getLocale();
            log.debug("locale {}", locale.getLanguage());
            params.put("ui_locales", locale.getLanguage());
        });
    }
}
