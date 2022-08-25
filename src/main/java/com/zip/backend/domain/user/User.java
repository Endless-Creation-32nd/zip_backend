package com.zip.backend.domain.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor
@Entity
public class User  {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    // 카카오 로그인 때문에 email 값이 null 이 될 수 있음 (선택동의 사항이라)
    @Column
    private String email;

    @JsonIgnore
    @Column
    private String password;

    @NotNull
    @Enumerated(EnumType.STRING)
    private AuthProvider provider;

    @Column
    private String providerId;

    @Enumerated(EnumType.STRING)
    @Column (nullable = true) // local signup을 위해 true로 설정
    private Role role;

    @Builder
    public User(String name, String email , Role role, String password, AuthProvider provider, String providerId) {
        this.name = name;
        this.email = email;
        this.role = role;
        this.password = password;
        this.provider = provider;
        this.providerId = providerId;
    }

    public User update(String name, String email) {
        this.name = name;
        this.email = email;
        return this;
    }
}
