package com.ll.server.global.config;

import feign.RequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

public class FeignConfig {

    @Value("${api.bearer.token}")
    private String token;

    private static final int MONTHLY_TOKEN_LIMIT = 300; // 토큰 하나당 300회 사용 가능

    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> {
            requestTemplate.header("Authorization", "Bearer " + token);
        };
    }
}