package com.zip.backend.security.oauth2.dto;

import java.util.Map;

public class KakaoOAuth2UserInfo implements OAuth2UserInfo {
    private Map<String, Object> attributes;

    public KakaoOAuth2UserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    @Override
    public String getName() {
        Map<String, Object> userInfo=(Map<String,Object>)attributes.get("kakao_account");
        Map<String, Object> profile = (Map<String, Object>)userInfo.get("profile");
        return (String) profile.get("nickname");
    }

    @Override
    public String getEmail() {
        Map<String, Object> userInfo=(Map<String,Object>)attributes.get("kakao_account");
        return (String) userInfo.get("email");
    }

    @Override
    public String getProvider() {
        return "kakao";
    }

    @Override
    public String getProviderId() {
        // 카카오는 Long type으로 응답하기에 변환이 다르다
        return Long.toString((Long)attributes.get("id"));
    }
}
