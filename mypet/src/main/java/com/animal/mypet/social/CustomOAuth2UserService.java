package com.animal.mypet.social;


import java.util.HashSet;

import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.animal.mypet.user.KakaoUserInfo;
import com.animal.mypet.user.NaverUserInfo;
import com.animal.mypet.user.User;
import com.animal.mypet.user.UserRepository;

import jakarta.servlet.http.HttpSession;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private HttpSession httpSession;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        System.out.println("getClientRegistration: " + userRequest.getClientRegistration());
        System.out.println("getAccessToken: " + userRequest.getAccessToken().getTokenValue());
        System.out.println("getAttributes: " + super.loadUser(userRequest).getAttributes());

        OAuth2User oauth2User = super.loadUser(userRequest);
        OAuth2UserInfo oauth2UserInfo = null;
        String nameAttributeKey = null;

        if (userRequest.getClientRegistration().getRegistrationId().equals("naver")) {
        	System.out.println("네이버 유저입니다");
        	Map<String, Object> response = (Map<String, Object>) oauth2User.getAttributes().get("response");
            oauth2UserInfo = new NaverUserInfo(response);
            nameAttributeKey = "response"; // 네이버 사용자 식별자 키
            httpSession.setAttribute("naverAccessToken", userRequest.getAccessToken().getTokenValue());
        } else if (userRequest.getClientRegistration().getRegistrationId().equals("kakao")) {
            System.out.println("카카오 유저입니다");
            Map<String, Object> kakaoAccount = (Map<String, Object>) oauth2User.getAttributes().get("kakao_account");
            oauth2UserInfo = new KakaoUserInfo(oauth2User.getAttributes());
            String provider = oauth2UserInfo.getProvider();
            String username = oauth2UserInfo.getName();
            String providerId = oauth2UserInfo.getProviderId(); // 카카오의 경우 ID
            System.out.println("Provider: " + provider);
            System.out.println("Username: " + username);
            System.out.println("Provider ID: " + providerId);
            nameAttributeKey = "id"; // 카카오 사용자 식별자 키
            httpSession.setAttribute("kakaoAccessToken", userRequest.getAccessToken().getTokenValue());
        } else {
            throw new OAuth2AuthenticationException("지원하지 않는 로그인 서비스 입니다.");
        }

        if (oauth2UserInfo == null || oauth2UserInfo.getProviderId() == null) {
            throw new OAuth2AuthenticationException("OAuth2 사용자 정보가 없습니다.");
        }

        String provider = oauth2UserInfo.getProvider();
        String username = oauth2UserInfo.getName();
        String providerId = oauth2UserInfo.getProviderId(); // 카카오의 경우 ID
        String password = passwordEncoder.encode("password");
        String phone = oauth2UserInfo.getPhone();
        String email = oauth2UserInfo.getEmail();
        String role = "USER";
        String userId = provider + "_" + providerId; // 고유 식별자 생성

        System.out.println(username);
        System.out.println(password);
        System.out.println(phone);
        System.out.println(email);
        System.out.println(role);
        System.out.println(provider);

        // 기존 사용자 조회
        User userEntity = userRepository.findByUserEmailAndProvider(email, provider).orElse(null);
        if (userEntity == null) {
            userEntity = new User();
            userEntity.setUserId(userId);
            userEntity.setUserName(username);
            userEntity.setUserPassword(password);
            userEntity.setUserPhone(phone);
            userEntity.setUserEmail(email);
            userEntity.setUserRole(role);
            userEntity.setProvider(provider); 
            
            userRepository.save(userEntity);
        } else {
            // 소셜 로그인을 해서 가져온 정보(이메일값 + 제공자) 하나라도 다를 경우 새로운 유저라고 판단하여 DB에 저장
            userEntity.setUserId(userId);
            userEntity.setUserName(username);
            userEntity.setUserPassword(password);
            userEntity.setUserPhone(phone);
            userEntity.setUserRole(role);
            userRepository.save(userEntity);
        }

        httpSession.setAttribute("user", username);
        System.out.println(userRequest.getAccessToken().getTokenValue());

        Set<SimpleGrantedAuthority> authorities = new HashSet<>();
        authorities.add(new SimpleGrantedAuthority(role));
        return new DefaultOAuth2User(authorities, oauth2User.getAttributes(), nameAttributeKey);
    }
}
