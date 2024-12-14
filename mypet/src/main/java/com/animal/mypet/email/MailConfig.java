package com.animal.mypet.email;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.beans.factory.annotation.Value;

import java.util.Properties;

@Configuration
public class MailConfig {

    @Value("${MAIL_USERNAME}")
    private String username;

    @Value("${MAIL_PASSWORD}")
    private String password;

    @Bean
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();

        // SMTP 서버 설정 (Gmail)
        mailSender.setHost("smtp.gmail.com");
        mailSender.setPort(587);

        // 사용자 인증: Jenkins 환경 변수에서 값을 가져옴
        mailSender.setUsername(username); // Jenkins 환경 변수에서 이메일 가져오기
        mailSender.setPassword(password); // Jenkins 환경 변수에서 앱 비밀번호 가져오기

        // 추가 프로퍼티 설정 (TLS 사용)
        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        return mailSender;
    }
}
