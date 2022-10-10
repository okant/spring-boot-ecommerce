package com.luv2code.ecommerce.config;

import com.okta.spring.boot.oauth.Okta;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.accept.ContentNegotiationStrategy;
import org.springframework.web.accept.HeaderContentNegotiationStrategy;

@Configuration
public class SecurityConfiguration {

    @Value("${spring.data.rest.base-path}")
    private String basePath;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception{
        httpSecurity.authorizeRequests(config ->
                config
                        .antMatchers(basePath + "/orders/**")
                        .authenticated()).oauth2ResourceServer().jwt();

        httpSecurity.cors();

        httpSecurity.setSharedObject(ContentNegotiationStrategy.class, new HeaderContentNegotiationStrategy());

        Okta.configureResourceServer401ResponseBody(httpSecurity);

        httpSecurity.csrf().disable();

        return httpSecurity.build();
    }
}
