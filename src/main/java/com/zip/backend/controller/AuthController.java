package com.zip.backend.controller;

import com.zip.backend.domain.user.AuthProvider;
import com.zip.backend.domain.user.User;
import com.zip.backend.domain.user.UserRepository;
import com.zip.backend.exception.BadRequestException;
import com.zip.backend.controller.dto.ApiResponse;
import com.zip.backend.controller.dto.AuthResponse;
import com.zip.backend.controller.dto.LoginRequest;
import com.zip.backend.controller.dto.SignUpRequest;
import com.zip.backend.security.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RequiredArgsConstructor
@RequestMapping("/auth")
@RestController
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;

    @PostMapping("/login")
    ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );
        // 실제 SecurityContext 에 authentication 정보를 등록
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = tokenProvider.createToken(authentication);
        // ResponseEntity 와 ok(200) status code 를 한 번에 보내는 코드
        return ResponseEntity.ok(new AuthResponse(token));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@RequestBody SignUpRequest signUpRequest) {
        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            throw new BadRequestException("이미 해당 이메일을 사용하고 있습니다.");
        }
        // 계정 생성 (local 로 생성 + DB 에 저장)
        // builder() 이후에 없는 필드들은 null 로 넘겨지게 된다.
        User result = userRepository.save(User.builder()
                .name(signUpRequest.getName())
                .email(signUpRequest.getEmail())
                .password(passwordEncoder.encode(signUpRequest.getPassword()))
                .provider(AuthProvider.local)
                .build());

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/user/me")
                .buildAndExpand(result.getId()).toUri();

        return ResponseEntity.created(location)
                .body(new ApiResponse(true, "성공적으로 계정 생성이 되었습니다."));
    }
}
