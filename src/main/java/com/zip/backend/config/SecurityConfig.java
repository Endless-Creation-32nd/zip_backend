package com.zip.backend.config;

import com.zip.backend.domain.user.Role;
import com.zip.backend.security.CustomUserDetailsService;
import com.zip.backend.security.TokenAuthenticationFilter;
import com.zip.backend.security.oauth2.CustomOAuth2UserService;
import com.zip.backend.security.oauth2.HttpCookieOAuth2AuthorizationRequestRepository;
import com.zip.backend.security.oauth2.OAuth2AuthenticationFailureHandler;
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
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true, jsr250Enabled = true, prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final CustomUserDetailsService customUserDetailsService;

    private final CustomOAuth2UserService customOAuth2UserService;

    private final OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;
    private final OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler;

    private final HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository;

    @Bean
    public TokenAuthenticationFilter tokenAuthenticationFilter() {
        return new TokenAuthenticationFilter();
    }
    // default??? spring OAuth2 ??? HttpSessionOAuth2AuthorizationRequestRepository ??? ??????
    // but, JWT ??? ???????????? ????????? SESSION ??? ????????? ????????? ????????? Authorization Request ??? Based64 encoded cookie ??? ??????
    @Bean
    public HttpCookieOAuth2AuthorizationRequestRepository cookieAuthorizationRequestRepository() {
        return new HttpCookieOAuth2AuthorizationRequestRepository();
    }

    // Authorization ??? ????????? userDetailsService??? passworkd Encoder ??????
    @Override
    public void configure(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
        authenticationManagerBuilder.userDetailsService(customUserDetailsService).passwordEncoder(passwordEncoder());
    }
    // SecurtiyConfig ?????? ????????? password encoder??? BCrptPasswordEncoder ??? ??????
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /*
    AuthenticationManager ??? ???????????? ???????????? ?????? AuthenticationManager Bean ??? ??????
    @Autowired ??? ?????? @Bean ???????????? Spring Security ????????? Authentication ??????
     */
    @Bean(BeanIds.AUTHENTICATION_MANAGER)
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                // CORS ??????
                .cors()
                .and()
                // ????????? ???????????? ?????? sessionCreationPolicy??? STATELESS??? ?????? (Session ????????????)
                    .sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                // CSRF ????????????
                    .csrf().disable()
                // ???????????? ????????????
                    .formLogin().disable()
                // ?????? ????????? ??? ????????????
                    .httpBasic().disable()
                    .authorizeRequests()
                        .antMatchers("/").permitAll()
                        .antMatchers("api/**").hasAnyRole(Role.GUEST.name(),Role.USER.name(),Role.ADMIN.name())
                        .antMatchers("/auth/**","/oauth2/**").permitAll()
                        .anyRequest().authenticated()
                .and()
                    .oauth2Login()
                        .authorizationEndpoint()
                // ??????????????? ?????? ????????? ?????? URI
                        .baseUri("/oauth2/authorization")

                .authorizationRequestRepository(cookieAuthorizationRequestRepository())
                .and()
                    .userInfoEndpoint()
                        .userService(customOAuth2UserService)
                .and()
                    .successHandler(oAuth2AuthenticationSuccessHandler)
                    .failureHandler(oAuth2AuthenticationFailureHandler);

        // Add our custom Token based authentication filter
        // UsernamePasswordAuthenticationFilter ?????? custom ?????? ??????!
        http.addFilterBefore(tokenAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
    }
}
