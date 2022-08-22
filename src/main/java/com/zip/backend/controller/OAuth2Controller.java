package com.zip.backend.controller;

import com.zip.backend.security.UserPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OAuth2Controller {

    @GetMapping("/hello")
    public @ResponseBody String getHello(){
        return "Hello!";
    }

    // DI(의존성 주입) + @AuthenticationPrincipal 을 통해 세션 정보 접근 , UserDetails 를 UserPrincipal 로 구현 가능
    @GetMapping("/test/login")
    public @ResponseBody String testLogin(Authentication authentication, @AuthenticationPrincipal UserDetails userDetails) {
        System.out.println("/test/login ===========");
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        System.out.println("authentication: " + userPrincipal.getUser());

        System.out.println(userDetails.getUsername());
        return "세션 정보 확인하기";
    }

    @GetMapping("/test/oauth/login")
    public @ResponseBody String testOAuthLogin(Authentication authentication, @AuthenticationPrincipal UserPrincipal oauth) {
        System.out.println("/test/oauth/login ===========");
        // (oAuth2User 로 casting 필요한듯)
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        System.out.println("authentication: "+oAuth2User.getAttributes());
        System.out.println("authentication: "+oauth.getAttributes());

        return "OAuth 세션 정보 확인하기";
    }

}
