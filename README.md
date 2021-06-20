# study-web-mvc-and-flux

## Filter

### mvc
- Filter を直接 Bean定義すると、Spring Security の内部のフィルターとして取り込まれる。
    - これを嫌う場合は、`FilterRegistrationBean` 経由でフィルターを定義する。
    - ログ出力用のフィルターとして登録する場合は、 sleuth よりも後で、Spring Security より前に登録したい。
      - Spring Security のフィルターの Order は、`SecurityProperties.getFilter().getOrder()` で取得できるので、これの値に 1引いた値をフィルターの Order に設定するとよい。  
      実装例)
        ```java
        @Bean
        public FilterRegistrationBean<?> loggingFilter(SecurityProperties secProp) {
            FilterRegistrationBean<?> bean = new FilterRegistrationBean<>(new WebLoggingFilter());
            bean.setOrder(secProp.getFilter().getOrder() - 1);
            return bean;
        }
        ```
- Spring Security の内部のフィルター の `ExceptionTranslationFilter` は認証系の例外を locationヘッダ付きのレスポンスに変換するため、
  ログ出力用にフィルタするのであれば、`ExceptionTranslationFilter` の後に例外だけのログ出力するフィルターを仕込んだ方がよい。  
  実装例)
    ```java
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.addFilterAfter(new WebLoggingExceptionFilter(), ExceptionTranslationFilter.class);
        // ...
    }
    ```

## Session Cookie

### mvc

- ログアウト時の設定で `deleteCookies()` で Cookie を消そうとした場合、Cookie の Path を `ContextPath + "/"` であるものとして削除しようとする。
- 一方、ContextPath を設定した場合の Session Cookie の Path はデフォルトでは、`ContextPath` になっている。
- そのため、`deleteCookies()` で Session Cookie を消そうとしてもブラウザから消えない。
- よって、ContextPath を設定した場合は、Session Cookie の Path をデフォルトのままとせず、`ContextPath + /` にしておく。

実装例) application.yml  
```yml
server.servlet:
  context-path: /app
  session.cookie:
    name: SID
    path: ${server.servlet.context-path}/
```

実装例) java  
```java
@Value("${server.servlet.session.cookie.name:JSESSIONID}")
private String sessionCookieName;

@Override
protected void configure(HttpSecurity http) throws Exception {
    // ...
        
    // ログアウト
    http.logout(logout -> {
        logout.deleteCookies(sessionCookieName);
    });
}
```