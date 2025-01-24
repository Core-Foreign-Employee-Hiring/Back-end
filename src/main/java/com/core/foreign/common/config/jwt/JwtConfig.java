package com.core.foreign.common.config.jwt;

import com.core.foreign.api.member.jwt.filter.JwtAuthenticationProcessingFilter;
import com.core.foreign.api.member.jwt.service.JwtService;
import com.core.foreign.api.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class JwtConfig {

    private final JwtService jwtService;
    private final MemberRepository memberRepository;

    @Bean
    public JwtAuthenticationProcessingFilter jwtAuthenticationProcessingFilter() {
        return new JwtAuthenticationProcessingFilter(jwtService, memberRepository);
    }
}