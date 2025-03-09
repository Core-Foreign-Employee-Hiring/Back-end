package com.core.foreign.common.config.security;

import com.core.foreign.common.config.jwt.JwtConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtConfig jwtConfig;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .cors(corsCustomizer -> corsCustomizer.configurationSource(request -> {
                    CorsConfiguration config = new CorsConfiguration();
                    config.setAllowedOrigins(Arrays.asList(
                            "https://www.forwork.co.kr",
                            "https://api.forwork.co.kr",
                            "http://localhost:3000",
                            "https://forwork-gules.vercel.app"
                    ));
                    config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH","DELETE", "OPTIONS"));
                    config.setAllowCredentials(true);
                    config.setAllowedHeaders(Arrays.asList("Authorization", "Authorization-Refresh","Content-Type", "X-Requested-With", "Accept", "Origin"));
                    config.setMaxAge(3600L);
                    config.addExposedHeader("Authorization");
                    config.addExposedHeader("Authorization-Refresh");
                    return config;
                }))
                .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(
                                "/api-doc", "/health", "/v3/api-docs/**",
                                "/swagger-resources/**","/swagger-ui/**",
                                "/h2-console/**"
                        ).permitAll() // 스웨거, H2, healthCheck 허가
                        .requestMatchers(
                                "/api/v1/member/employee-register", "/api/v1/member/employer-register",
                                "/api/v1/member/login","/api/v1/member/token-reissue","/api/v1/member/verify-email",
                                "/api/v1/member/verification-email-code","/api/v1/member/verify-userid","/api/v1/member/verify-phone",
                                "/api/v1/member/verification-phone-code", "/api/v1/member/find-user-id", "/api/v1/member/employer/company-validate"
                        ).permitAll() // 회원가입, 로그인, 토큰 재발급, 이메일 인증, 사업자등록 번호 인증 허가
                        .requestMatchers(
                                "/api/v1/recruit/search","/api/v1/recruit/premium/search","/api/v1/recruit/view", "/api/v1/recruit/general/jump", "/api/v1/recruit/premium/jump", "/api/v1/recruit/keyword/search"
                        ).permitAll() // 공고(프리미엄,일반/ 프리미엄) 전체 조회, 공고 검색, 공고 상세 조회, 프리미엄 공고 상단 점프 목록 조회, 일반 공고 점프 목록 조회 인증 허가
                        .requestMatchers(
                                HttpMethod.GET, "/api/v1/albareview/**", "/api/v1/albareview","/api/v1/albareview/comment","/api/v1/albareview/search"
                        ).permitAll() // 알바 후기 조회, 검색 관련 API
                        .anyRequest().authenticated()
                )
                .exceptionHandling(exceptionHandling ->
                        exceptionHandling.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)));

        http.addFilterBefore(jwtConfig.jwtAuthenticationProcessingFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(passwordEncoder());
        return new ProviderManager(provider);
    }
}
