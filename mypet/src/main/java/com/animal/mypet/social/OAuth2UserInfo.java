package com.animal.mypet.social;



public interface OAuth2UserInfo {
    String getProvider();
    String getEmail();
    String getName();
    String getPhone();
    String getProviderId(); // providerId 추가
}