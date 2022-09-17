package com.zip.backend.controller;

import com.zip.backend.controller.dto.ApiResponse;
import com.zip.backend.controller.dto.AuthResponse;
import com.zip.backend.controller.dto.LoginRequest;
import com.zip.backend.controller.dto.SignUpRequest;
import com.zip.backend.domain.user.AuthProvider;
import com.zip.backend.domain.user.Role;
import com.zip.backend.domain.user.User;
import com.zip.backend.domain.user.UserRepository;
import com.zip.backend.security.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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

@RequiredArgsConstructor
@RequestMapping("/auth")
@RestController
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;

    @PostMapping("/login")
    ResponseEntity<AuthResponse> authenticateUser(@RequestBody LoginRequest loginRequest) {
        // 강제로 authentication 객체를 만드는 과정
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
        // ResponseEntity 에 씌워서 보내면 Json 형태로 받음
        return ResponseEntity.ok(new AuthResponse(token));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@RequestBody SignUpRequest signUpRequest) {
        if (userRepository.existsByProviderAndEmail(AuthProvider.local, signUpRequest.getEmail())) {
            throw new RuntimeException("이미 해당 이메일을 사용하고 있습니다.");
//            return ResponseEntity.ok(new EmailDuplicatedException("이미 해당 이메일을 사용하고 있습니다."));
        }
        // 계정 생성 (local 로 생성 + DB 에 저장)
        // builder() 이후에 없는 필드들은 null 로 넘겨지게 된다.
        User localUserInfo = User.builder()
                .name(signUpRequest.getName())
                .email(signUpRequest.getEmail())
                .password(passwordEncoder.encode(signUpRequest.getPassword()))
                .provider(AuthProvider.local)
                .role(Role.USER)
                .build();
        userRepository.save(localUserInfo);
        

        // 굳이 URI 를 /user/me 쪽으로 보내줄 필요 없어서 위 과정 생략함
        return new ResponseEntity<>(new ApiResponse(true, "성공적으로 계정 생성이 되었습니다"), HttpStatus.CREATED);
    }


}
