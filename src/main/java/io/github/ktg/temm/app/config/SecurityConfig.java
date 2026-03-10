package io.github.ktg.temm.app.config;

import io.github.ktg.temm.app.security.JwtAuthenticationFilter;
import io.github.ktg.temm.app.security.LoginCheckFilter;
import io.github.ktg.temm.app.security.LoginContextHolderFilter;
import io.github.ktg.temm.domain.provider.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tools.jackson.databind.ObjectMapper;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final TokenProvider tokenProvider;
    private final ObjectMapper objectMapper;
    private final String[] noAuthPathPatterns = new String[] {
        "/api/v1/auth/**", "/*.html", "/"
    };

    @Bean
    public FilterRegistrationBean<LoginContextHolderFilter> loginContextHolderFilter() {
        FilterRegistrationBean<LoginContextHolderFilter> bean = new FilterRegistrationBean<>();
        bean.setFilter(new LoginContextHolderFilter());
        bean.setOrder(1);
        bean.addUrlPatterns("/*");
        return bean;
    }


    @Bean
    public FilterRegistrationBean<JwtAuthenticationFilter> jwtAuthenticationFilter() {
        FilterRegistrationBean<JwtAuthenticationFilter> bean = new FilterRegistrationBean<>();
        bean.setFilter(new JwtAuthenticationFilter(tokenProvider, objectMapper));
        bean.setOrder(2);
        bean.addUrlPatterns("/*");
        return bean;
    }


    @Bean
    public FilterRegistrationBean<LoginCheckFilter> loginCheckFilter() {
        FilterRegistrationBean<LoginCheckFilter> bean = new FilterRegistrationBean<>();
        bean.setFilter(new LoginCheckFilter(noAuthPathPatterns, objectMapper));
        bean.setOrder(3);
        bean.addUrlPatterns("/*");
        return bean;
    }


}
