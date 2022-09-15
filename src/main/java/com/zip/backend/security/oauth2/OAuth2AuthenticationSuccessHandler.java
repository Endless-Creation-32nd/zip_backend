package com.zip.backend.security.oauth2;

import com.zip.backend.config.AppProperties;
import com.zip.backend.exception.BadRequestException;
import com.zip.backend.security.TokenProvider;
//import com.zip.backend.utils.CookieUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.util.Optional;

//import static com.zip.backend.security.oauth2.HttpCookieOAuth2AuthorizationRequestRepository.REDIRECT_URI_PARAM_COOKIE_NAME;

@Component
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private TokenProvider tokenProvider;
    private AppProperties appProperties;
//    private HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository;

    @Autowired
    public OAuth2AuthenticationSuccessHandler(TokenProvider tokenProvider, AppProperties appProperties) {
        this.tokenProvider = tokenProvider;
        this.appProperties = appProperties;
//        this.httpCookieOAuth2AuthorizationRequestRepository = httpCookieOAuth2AuthorizationRequestRepository;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        String token = tokenProvider.createToken(authentication);
        String targetUrl=UriComponentsBuilder.fromUriString("/oauth2/success")
                .queryParam("token", token)
                .build().toUriString();
//        String targetUrl = determineTargetUrl(request, response, authentication);
//        if (response.isCommitted()) {
//            logger.debug("응답이 이미 커밋되었습니다. " + targetUrl + "로 리다이렉션을 할 수 없습니다");
//            return;
//        }
//        clearAuthenticationAttributes(request, response); // attribute 도 지우고 cookie 도 지우기
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }


//    protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
//        Optional<String> redirectUri = CookieUtils.getCookie(request, REDIRECT_URI_PARAM_COOKIE_NAME).map(Cookie::getValue);
//
//        if (redirectUri.isPresent() && !isAuthorizedRedirectUri(redirectUri.get())) {
//            throw new BadRequestException("승인되지 않은 리디렉션 URI 가 있어 인증을 진행할 수 없습니다.");
//        }
//
//        String targetUrl = redirectUri.orElse(getDefaultTargetUrl());
//        String token = tokenProvider.createToken(authentication);
//
//        return UriComponentsBuilder.fromUriString(targetUrl).path("/oauth2/success")
//                .queryParam("token", token)
//                .build().toUriString();
//    }

//    protected void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
//        super.clearAuthenticationAttributes(request);
//
//        httpCookieOAuth2AuthorizationRequestRepository.removeAuthorizationRequestCookies(request,response);
//
//    }
//
//    private boolean isAuthorizedRedirectUri(String uri) {
//        URI clientRedirectUri = URI.create(uri);
//
//        return appProperties.getOauth2().getAuthorizedRedirectUris()
//                .stream()
//                .anyMatch(authorizedRedirectUri ->{
//                   // Only validate host and port. Let the Clients use different paths if they want to
//                    URI authorizedURI = URI.create(authorizedRedirectUri);
//                    if(authorizedURI.getHost().equalsIgnoreCase(clientRedirectUri.getHost())
//                            && authorizedURI.getPort() == clientRedirectUri.getPort()) return true;
//                    else return false;
//                });
//    }
}
