package com.animal.mypet.user;

import java.util.Map;

import com.animal.mypet.social.OAuth2UserInfo;


public class NaverUserInfo implements OAuth2UserInfo {
    private Map<String, Object> attributes;
//    private String id;
    
    public NaverUserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
//        this.id = id;
    }
    
    @Override
    public String getProvider() {
        return "naver";
    }

    @Override
    public String getName() {
        return (String) attributes.get("name");
    }

    @Override
    public String getEmail() {
        return (String) attributes.get("email");
    }

    @Override
    public String getPhone() {
    	return (String) attributes.get("mobile");
    }
}
