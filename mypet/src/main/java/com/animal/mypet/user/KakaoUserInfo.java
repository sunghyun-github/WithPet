package com.animal.mypet.user;

import java.util.Map;

import com.animal.mypet.social.OAuth2UserInfo;

public class KakaoUserInfo implements OAuth2UserInfo {

    private Map<String, Object> attributes;
    private Map<String, Object> kakaoAccount;

    public KakaoUserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
        this.kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
    }

    @Override
    public String getProvider() {
        return "kakao";
    }

    @Override
    public String getProviderId() {
        if (attributes.get("id") != null) {
            return attributes.get("id").toString();
        }
        return null;
    }

    @Override
    public String getName() {
        if (kakaoAccount != null && kakaoAccount.get("profile") != null) {
            Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");
            if (profile.get("nickname") != null) {
                return profile.get("nickname").toString();
            }
        }
        return null;
    }

    @Override
    public String getEmail() {
        if (kakaoAccount != null && kakaoAccount.get("email") != null) {
            return kakaoAccount.get("email").toString();
        }
        return null;
    }

    @Override
    public String getPhone() {
        // 카카오에서는 전화번호 정보를 제공하지 않을 수 있습니다.
        return null;
    }
}
