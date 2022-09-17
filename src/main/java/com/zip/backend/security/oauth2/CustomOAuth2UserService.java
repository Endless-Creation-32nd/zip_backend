package com.zip.backend.security.oauth2;

import com.zip.backend.domain.user.AuthProvider;
import com.zip.backend.domain.user.Role;
import com.zip.backend.domain.user.User;
import com.zip.backend.domain.user.UserRepository;
import com.zip.backend.security.UserPrincipal;
import com.zip.backend.security.oauth2.dto.GoogleOAuth2UserInfo;
import com.zip.backend.security.oauth2.dto.KakaoOAuth2UserInfo;
import com.zip.backend.security.oauth2.dto.NaverOAuth2UserInfo;
import com.zip.backend.security.oauth2.dto.OAuth2UserInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;


@RequiredArgsConstructor
@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    private final UserRepository userRepository;

    // 구글로부터 받은 userRequest 데이터에 대한 후처리 되는 메소드
    // 로그인 후에 oAuth2UserRequest 에는 clientRegistration + access token 모두 저장된다
    // 함수 종료시 @AuthenticationPrincipal 어노테이션이 만들어진다
    @Override
    public OAuth2User loadUser(OAuth2UserRequest oAuth2UserRequest) throws OAuth2AuthenticationException {
        System.out.println(oAuth2UserRequest.getClientRegistration()); // registrationId 로 어떤 OAuth 로그인인지 알 수 있다
        System.out.println(oAuth2UserRequest.getAccessToken());
        System.out.println(oAuth2UserRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint());
        System.out.println(oAuth2UserRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName());
        // 구글 로그인 버튼 클릭 -> 구글 로그인 창 -> 로그인을 완료 -> code 를 리턴(OAuth client 라이브러리) -> Access Token 요청
        // 위 까지가 oAuth2UserRequest 정보 -> loadUser 메소드 호출 -> 구글로부터 회원 프로필 받아준다
        OAuth2User oAuth2User = super.loadUser(oAuth2UserRequest);
        System.out.println(oAuth2User.getAttributes());
        // 회원가입 강제로 진행할 예정..
        try {
            return processOAuth2User(oAuth2UserRequest, oAuth2User);
        } catch (AuthenticationException ex) {
            throw ex;
        } catch (Exception ex) {
            // Throwing an instance of AuthenticationException will trigger the OAuth2AuthenticationFailureHandler
            throw new InternalAuthenticationServiceException(ex.getMessage(), ex.getCause());
        }
    }

    // 사용자 정보 추출
    private OAuth2User processOAuth2User(OAuth2UserRequest oAuth2UserRequest, OAuth2User oAuth2User) {
        String oAuth2Provider = oAuth2UserRequest.getClientRegistration().getRegistrationId();
        Map<String, Object> attributes = oAuth2User.getAttributes();
        // 특정 OAuth2 공급자(구글,네이버,카카오 중 하나) 가져옴
        OAuth2UserInfo oAuth2UserInfo = determineProvider(oAuth2Provider, attributes);

        Optional<User> userOptional = userRepository.findByProviderAndProviderId(AuthProvider.valueOf(oAuth2UserInfo.getProvider()), oAuth2UserInfo.getProviderId());

        // DB에 계정이 없는 경우 -> 새로 생성
        User user;
        if(!userOptional.isPresent()){
            user = registerNewUser(oAuth2UserInfo);
        }
        // DB에 계정이 이미 존재하는 경우 -> 정보 update (name & email)
        else{
            User tempUser = userOptional.get();
            user = updateExistingUser(tempUser, oAuth2UserInfo);
        }
        return new UserPrincipal(user, attributes);
    }

    // 어떤 소셜 로그인인지 구분
    private OAuth2UserInfo determineProvider(String oAuth2Provider, Map<String, Object> attributes) {
        if(oAuth2Provider.equals("google")) return new GoogleOAuth2UserInfo(attributes);
        else if(oAuth2Provider.equals("naver")) return new NaverOAuth2UserInfo(attributes);
        else if(oAuth2Provider.equals("kakao")) return new KakaoOAuth2UserInfo(attributes);

        return null;
    }

    private User registerNewUser(OAuth2UserInfo oAuth2UserInfo) {
        User user=User.builder()
                .name(oAuth2UserInfo.getName())
                .email(oAuth2UserInfo.getEmail())
                .provider(AuthProvider.valueOf(oAuth2UserInfo.getProvider()))
                .providerId(oAuth2UserInfo.getProviderId())
                .role(Role.USER)
                .build();
        return userRepository.save(user);
    }

    private User updateExistingUser(User existingUser, OAuth2UserInfo oAuth2UserInfo) {
        User user = existingUser.update(oAuth2UserInfo.getName(), oAuth2UserInfo.getEmail());
        return userRepository.save(user);
    }
}
