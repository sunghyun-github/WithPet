package com.animal.mypet;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.header.writers.frameoptions.XFrameOptionsHeaderWriter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf
                .ignoringRequestMatchers("/mail/**")  // /mail/** 경로에 대해 CSRF 보호를 비활성화
            )
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/qna_question/create").hasAuthority("USER")  // USER만 질문 등록 가능
                .requestMatchers("/qna_answer/**").hasAnyAuthority("ADMIN", "PET_MANAGER")  // ADMIN 또는 PET_MANAGER만 답글 등록 가능
                .requestMatchers("/**").permitAll()  // 모든 다른 요청은 허용
                .requestMatchers("/user/findId").permitAll()  // 아이디 찾기 요청 허용
                .requestMatchers("/user/idFound").permitAll()  // 아이디 찾기 결과 페이지 허용
                .requestMatchers("/css/**", "/js/**", "/img/**").permitAll()  // 모든 사용자가 인증(로그인) 없이 해당 경로에 접근할 수 있도록 설정
                .requestMatchers("/", "/members/**", "/item/**", "/images/**", "/mail/**").permitAll()  // 추가된 경로 설정
            )
            .headers(headers -> headers
                .addHeaderWriter(new XFrameOptionsHeaderWriter(
                    XFrameOptionsHeaderWriter.XFrameOptionsMode.SAMEORIGIN)))
            .formLogin(formLogin -> formLogin
                .loginPage("/user/login")
                .defaultSuccessUrl("/"))
            .logout(logout -> logout
                .logoutRequestMatcher(new AntPathRequestMatcher("/user/logout"))
                .logoutSuccessUrl("/")
                .invalidateHttpSession(true))
            .exceptionHandling(exceptionHandling -> exceptionHandling
                .accessDeniedPage("/access_denied")  // 접근 거부 페이지
            );

        return http.build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}
