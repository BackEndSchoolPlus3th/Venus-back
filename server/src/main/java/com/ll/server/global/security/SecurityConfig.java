package com.ll.server.global.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // CSRF 비활성화 (H2 콘솔에 대해서만)
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/h2-console/**")
                        .ignoringRequestMatchers("/api/v1/members/**")// H2 콘솔을 제외한 모든 경로는 CSRF 보호 적용
                )
                // H2 콘솔 접근 허용
                .authorizeHttpRequests((authorizeHttpRequests) -> authorizeHttpRequests
                        .requestMatchers("/h2-console/**").permitAll()  // H2 콘솔 경로에 대한 접근을 허용
                        .requestMatchers("/**").permitAll()  // 그 외 모든 경로도 허용 (예시)
                )
                // iframe을 통한 접근 허용 (H2 콘솔은 iframe을 사용하기 때문에 필요)
                .headers(headers -> headers
                        .addHeaderWriter((request, response) -> response.setHeader("X-Frame-Options", "SAMEORIGIN"))
                )
                // 추가적인 보안 설정
                .authorizeHttpRequests((authorizeHttpRequests) -> authorizeHttpRequests
                        .anyRequest().authenticated()  // 나머지 요청은 인증된 사용자만 접근 가능
                );

        return http.build();
    }
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
