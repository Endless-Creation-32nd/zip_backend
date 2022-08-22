package com.zip.backend.security.oauth2.user;

import java.util.Map;

public interface OAuth2UserInfo {

    String getName();

    String getEmail();

    String getProvider();

    String getProviderId();

}
