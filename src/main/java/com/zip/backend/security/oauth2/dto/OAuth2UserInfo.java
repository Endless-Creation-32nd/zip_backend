package com.zip.backend.security.oauth2.dto;

public interface OAuth2UserInfo {

    String getName();

    String getEmail();

    String getProvider();

    String getProviderId();

}
