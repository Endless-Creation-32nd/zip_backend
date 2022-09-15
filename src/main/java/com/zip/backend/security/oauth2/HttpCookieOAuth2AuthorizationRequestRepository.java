//package com.zip.backend.security.oauth2;
//
//import com.nimbusds.oauth2.sdk.util.StringUtils;
//import com.zip.backend.utils.CookieUtils;
//import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
//import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
//import org.springframework.stereotype.Component;
//
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//
//@Component
//public class HttpCookieOAuth2AuthorizationRequestRepository implements AuthorizationRequestRepository<OAuth2AuthorizationRequest> {
//
//    public static final String OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME = "oauth2_auth_request";
//    public static final String REDIRECT_URI_PARAM_COOKIE_NAME = "redirect_uri";
//    private static final int cookieExpireSeconds = 180; // 3분
//
//    @Override
//    public OAuth2AuthorizationRequest loadAuthorizationRequest(HttpServletRequest request) {
//        return CookieUtils.getCookie(request, OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME)
//                .map(cookie -> CookieUtils.deserialize(cookie, OAuth2AuthorizationRequest.class))
//                .orElse(null);
//        // Cookie[] 에 있는 cookie 객체들을 모두 deserialize 해서 byte 형태로 변환해라
//    }
//
//    @Override
//    public void saveAuthorizationRequest(OAuth2AuthorizationRequest authorizationRequest, HttpServletRequest request, HttpServletResponse response) {
//        if (authorizationRequest == null) {
//            CookieUtils.deleteCookie(request, response, OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME);
//            CookieUtils.deleteCookie(request, response, REDIRECT_URI_PARAM_COOKIE_NAME);
//            return;
//        }
//
//        CookieUtils.addCookie(response,OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME,
//                CookieUtils.serialize(authorizationRequest), cookieExpireSeconds);
//        String redirectUriAfterLogin = request.getParameter(REDIRECT_URI_PARAM_COOKIE_NAME);
//        if (StringUtils.isNotBlank(redirectUriAfterLogin)) {
//            CookieUtils.addCookie(response,REDIRECT_URI_PARAM_COOKIE_NAME,redirectUriAfterLogin,cookieExpireSeconds);
//        }
//    }
//
//    @Override
//    public OAuth2AuthorizationRequest removeAuthorizationRequest(HttpServletRequest request) {
//        return this.loadAuthorizationRequest(request);
//    }
//
//    public void removeAuthorizationRequestCookies(HttpServletRequest request, HttpServletResponse response) {
//        CookieUtils.deleteCookie(request, response, OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME);
//        CookieUtils.deleteCookie(request, response, REDIRECT_URI_PARAM_COOKIE_NAME);
//
//    }
//}
