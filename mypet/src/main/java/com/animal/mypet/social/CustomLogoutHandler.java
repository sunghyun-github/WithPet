package com.animal.mypet.social;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import reactor.core.publisher.Mono;

@Component
public class CustomLogoutHandler implements LogoutHandler {

    private final WebClient webClient;

    @Value("${naver.client.id}")
    private String naverClientId;

    @Value("${naver.client.secret}")
    private String naverClientSecret;

    public CustomLogoutHandler(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        String kakaoAccessToken = (String) request.getSession().getAttribute("kakaoAccessToken");
        String naverAccessToken = (String) request.getSession().getAttribute("naverAccessToken");

        System.out.println("핸들러");
        System.out.println("카카오 토큰: " + kakaoAccessToken);
        System.out.println("네이버 토큰: " + naverAccessToken);
        
        if (kakaoAccessToken != null) {
            // 카카오 로그아웃 처리
            webClient.post()
                .uri("https://kapi.kakao.com/v1/user/logout")
                .header("Authorization", "Bearer " + kakaoAccessToken)
                .retrieve()
                .toBodilessEntity()
                .block();
        }

        if (naverAccessToken != null) {
            // 네이버 로그아웃 처리
            webClient.post()
                .uri(uriBuilder -> uriBuilder
                    .scheme("https")
                    .host("nid.naver.com")
                    .path("/oauth2.0/token")
                    .queryParam("grant_type", "delete")
                    .queryParam("client_id", naverClientId)
                    .queryParam("client_secret", naverClientSecret)
                    .queryParam("access_token", naverAccessToken)
                    .queryParam("service_provider", "NAVER")
                    .build())
                .retrieve()
                .onStatus(
                    status -> status.is4xxClientError(),
                    clientResponse -> {
                        System.err.println("네이버 로그아웃 실패: " + clientResponse.statusCode());
                        return clientResponse.createException().flatMap(Mono::error);
                    })
                .toBodilessEntity()
                .block();
        }

        // 세션 무효화
        request.getSession().invalidate();
    }
}
