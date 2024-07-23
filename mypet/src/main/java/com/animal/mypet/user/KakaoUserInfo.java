package com.animal.mypet.user;

import java.util.Map;

import com.animal.mypet.social.OAuth2UserInfo;


public class KakaoUserInfo implements OAuth2UserInfo {
    private Map<String, Object> attributes;

    public KakaoUserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    @Override
    public String getProvider() {
        return "kakao";
    }

    @Override
    public String getName() {
        Map<String, Object> profile = (Map<String, Object>) attributes.get("profile");
        return (String) profile.getOrDefault("nickname", "Unknown");
    }

    @Override
    public String getEmail() {
        return (String) attributes.getOrDefault("email", "unknown@example.com");
    }
    
    @Override
    public String getPhone() {
        return "0"; // 카카오는 전화번호를 제공하지 않으므로 기본값으로 설정합니다.
    }
}
