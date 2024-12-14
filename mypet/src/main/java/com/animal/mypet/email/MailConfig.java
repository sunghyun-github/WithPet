package com.animal.mypet.email;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
public class MailConfig {

    @Bean
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();

        // SMTP 서버 설정 (Gmail)
        mailSender.setHost("smtp.gmail.com");
        mailSender.setPort(587);

        // Gmail 계정과 앱 비밀번호를 설정합니다. 여기서 앱 비밀번호를 사용하세요.
        mailSender.setUsername("tjdgus50998583@gmail.com");  // 본인 이메일
        mailSender.setPassword("mgyd augn grsk wxde");  // Gmail 앱 비밀번호 (2단계 인증 활성화 시 앱 비밀번호 필요)

        // 추가 프로퍼티 설정 (TLS 사용)
        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");  // STARTTLS 사용
        props.put("mail.smtp.timeout", "5000");  // 타임아웃 설정 (옵션)
        props.put("mail.smtp.connectiontimeout", "5000");  // 커넥션 타임아웃 (옵션)
        
        return mailSender;
    }
}
