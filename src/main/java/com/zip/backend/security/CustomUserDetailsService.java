package com.zip.backend.security;


import com.zip.backend.domain.user.User;
import com.zip.backend.domain.user.UserRepository;
import com.zip.backend.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class CustomUserDetailsService implements UserDetailsService {
    @Autowired
    UserRepository userRepository;

    // email 을 입력받아서 Repository 에 있으면 UserPrincipal 객체의 형태로 반환
    // 없으면 예외처리
    // 시큐리티 session(내부 Authentication(내부 UserDetails))
    // 함수 종료시 @AuthenticationPrincipal 어노테이션이 만들어진다
    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() ->
                        new UsernameNotFoundException("User not found with email :" + email));
        return new UserPrincipal(user);
    }

    @Transactional
    public UserDetails loadUserById(Long id) {
        User user=userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User","id",id));
        return new UserPrincipal(user);
    }

}
