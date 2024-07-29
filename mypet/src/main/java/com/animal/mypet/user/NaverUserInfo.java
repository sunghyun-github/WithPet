package com.animal.mypet.user;

import com.animal.mypet.social.OAuth2UserInfo;

import java.util.Map;

public class NaverUserInfo implements OAuth2UserInfo {
    private Map<String, Object> attributes;

    public NaverUserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    @Override
    public String getProvider() {
        return "naver";
    }

    @Override
    public String getEmail() {
        return (String) attributes.get("email");
    }

    @Override
    public String getName() {
        return (String) attributes.get("name");
    }

    @Override
    public String getPhone() {
        return (String) attributes.get("mobile");
    }

    @Override
    public String getProviderId() {
        return attributes.get("id") != null ? attributes.get("id").toString() : null;
    }
}
