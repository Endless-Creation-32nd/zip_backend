package com.zip.backend.config;

import com.zip.backend.domain.user.Role;
import com.zip.backend.security.CustomUserDetailsService;
import com.zip.backend.security.TokenAuthenticationFilter;
import com.zip.backend.security.oauth2.CustomOAuth2UserService;
import com.zip.backend.security.oauth2.OAuth2AuthenticationSuccessHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true, jsr250Enabled = true, prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final CustomUserDetailsService customUserDetailsService;

    private final CustomOAuth2UserService customOAuth2UserService;

    private final OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;
//    private final OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler;

//    private final HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository;

    @Bean
    public TokenAuthenticationFilter tokenAuthenticationFilter() {
        return new TokenAuthenticationFilter();
    }
    // default 로 spring OAuth2 는 HttpSessionOAuth2AuthorizationRequestRepository 를 사용
    // but, JWT 를 사용하기 때문에 SESSION 에 저장할 필요가 없어서 Authorization Request 를 Based64 encoded cookie 에 저장
//    @Bean
//    public HttpCookieOAuth2AuthorizationRequestRepository cookieAuthorizationRequestRepository() {
//        return new HttpCookieOAuth2AuthorizationRequestRepository();
//    }

    // Authorization 에 사용할 userDetailsService와 passworkd Encoder 정의
    @Override
    public void configure(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
        authenticationManagerBuilder.userDetailsService(customUserDetailsService).passwordEncoder(passwordEncoder());
    }

    // SecurityConfig 에서 사용할 password encoder 를 BCryptPasswordEncoder 로 정의
    // spring container 에 등록됨
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /*
    AuthenticationManager 를 외부에서 사용하기 위해 AuthenticationManager Bean 을 통해
    @Autowired 가 아닌 @Bean 설정으로 Spring Security 밖으로 Authentication 추출
     */
    @Bean(BeanIds.AUTHENTICATION_MANAGER)
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                // 기본 로그인 창 비활성화
                .httpBasic().disable()
                // 로그인폼 비활성화
                .formLogin().disable()
                // CORS 허용
                .cors()
                .and()
                // JWT 로 인증하기에 sessionCreationPolicy 를 STATELESS 로 설정 (session 비활성화)
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                // CSRF 비활성화
                .csrf().disable()

                .authorizeRequests()
                .antMatchers("/").permitAll()
                        .antMatchers("/auth/**","/oauth2/**").permitAll()
                        .antMatchers("/api/**").hasAnyRole(Role.GUEST.name(),Role.USER.name(),Role.ADMIN.name())
                        .anyRequest().authenticated()
                .and()
                    .oauth2Login()
                        .authorizationEndpoint()
                // 클라이언트 처음 로그인 시도 URI
//                        .baseUri( "/oauth2/authorization")

//                .authorizationRequestRepository(cookieAuthorizationRequestRepository())
                .and()
                    .userInfoEndpoint()
                        .userService(customOAuth2UserService)
                .and()
                    .successHandler(oAuth2AuthenticationSuccessHandler);
//                    .failureHandler(oAuth2AuthenticationFailureHandler);


        // UsernamePasswordAuthenticationFilter 앞에 custom 필터 추가!
        // 로그인 필터 돌기전에 Token 필터 돌아가게끔
        http.addFilterBefore(tokenAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

    }
}
