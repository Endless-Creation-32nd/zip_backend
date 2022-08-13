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

    private Long id;
    private String email;
    private String password;
    // Collection<? extends 클래스 이름> -> 컬렉션 안에 클래스 이름을 상속하는 모든 클래스의 인스턴스가 들어갈 수 있다
    private Collection<? extends GrantedAuthority> authorities;
    private Map<String,Object> attributes;

    public UserPrincipal(Long id, String email, String password, Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.authorities = authorities;
    }

    public static UserPrincipal create(User user) {
        List<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));

        // User 객체에서 id, email, password, authorities 만 가져옴
        return new UserPrincipal(
                user.getId(),
                user.getEmail(),
                user.getPassword(),
                authorities
        );
    }

    public static UserPrincipal create(User user, Map<String, Object> attributes) {
        UserPrincipal userPrincipal = UserPrincipal.create(user);
        userPrincipal.setAttributes(attributes);
        return userPrincipal;
    }

    // UserDetails interface override
    // getAuthorities() 와 getPassword() 는 구현하지 않아도 되는듯 ??

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public  String getUsername() {
        return email;
    }

    @Override
    public String getPassword() { return password; }

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

    @Override
    public String getName() {
        return String.valueOf(id);
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }



    public void setAttributes(Map<String, Object> attributes) {
        this.attributes = attributes;
    }
}
