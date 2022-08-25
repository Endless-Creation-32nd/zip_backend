package com.zip.backend.security;

import com.zip.backend.domain.user.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Getter
public class UserPrincipal implements OAuth2User, UserDetails {

    private User user;
    // Collection<? extends 클래스 이름> -> 컬렉션 안에 클래스 이름을 상속하는 모든 클래스의 인스턴스가 들어갈 수 있다
    private Collection<? extends GrantedAuthority> authorities;
    private Map<String,Object> attributes;

    // 일반 로그인 시 사용
    public UserPrincipal(User user) {
        this.user = user;
        authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
    }

    // OAuth 2.0 로그인 시 사용
    // attributes 정보를 통해 user 정보 생성함
    public UserPrincipal(User user, Map<String, Object> attributes) {
        this.user = user;
        this.attributes = attributes;
        List<GrantedAuthority> authority = Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
        authorities = authority;
    }


    // UserDetails interface override
    // getAuthorities() 와 getPassword() 는 구현하지 않아도 되는듯 ??


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    // 일반적으로 아이디 return
    @Override
    public  String getUsername() {
        return user.getEmail();
    }

    @Override
    public String getPassword() { return user.getPassword(); }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    // OAuth2User interface override

    // User 의 id
    @Override
    public String getName() {
        return String.valueOf(user.getId());
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }


}
