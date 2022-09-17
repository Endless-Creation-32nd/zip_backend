package com.zip.backend.security.oauth2.dto;

import java.util.Map;

public class NaverOAuth2UserInfo implements OAuth2UserInfo{
    private Map<String,Object> attributes;

    public NaverOAuth2UserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    @Override
    public String getName() {
        Map<String, Object> userInfo = (Map<String, Object>) attributes.get("response");
        return (String) userInfo.get("name");
    }

    @Override
    public String getEmail() {
        Map<String, Object> userInfo = (Map<String, Object>) attributes.get("response");
        return (String) userInfo.get("email");
    }

    @Override
    public String getProvider() {
        return "naver";
    }

    @Override
    public String getProviderId() {
        Map<String, Object> userInfo = (Map<String, Object>) attributes.get("response");
        return (String) userInfo.get("id");
    }
}
