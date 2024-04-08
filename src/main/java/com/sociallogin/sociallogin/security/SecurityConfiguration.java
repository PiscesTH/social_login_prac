package com.sociallogin.sociallogin.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfiguration {    //인증 & 인가 담당

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
//    private final OAuth2AuthenticationFailureHandler failureHandler;
//    private final OAuth2AuthenticationRequestBasedOnCookieRepository requestCookieRepository;
//    private final OAuth2AuthenticationSuccessHandler successHandler;
//    private final CustomOAuth2UserService customOAuth2UserService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))    //설정 -> session 사용 x (로그인에 세션 안 써서)
                .httpBasic(http -> http.disable())  //설정 -> 시큐리티에서 서버사이드 렌더링 되는 로그인 화면 사용 x
                .formLogin(formLogin -> formLogin.disable())
                .csrf(csrf -> csrf.disable())   //설정 -> 기본적으로 스프링이 제공해주는 보안 기법 사용 x     cos, csrf 검색 참고
                .authorizeHttpRequests(author -> author.requestMatchers(
                                        "/api/feed",
                                        "/api/feed/comment",
                                        "/api/dm",
                                        "/api/dm/msg"
                                ).authenticated()
                                .requestMatchers(HttpMethod.POST,
                                        "/api/user/signout",
                                        "/api/user/follow")
                                .authenticated()
                                .requestMatchers(HttpMethod.GET, "/api/user").authenticated()
                                .requestMatchers(HttpMethod.PATCH, "/api/user/pic").authenticated()
                                //.requestMatchers(HttpMethod.GET, "/api/feed/fav").hasAnyRole("ADMIN")
                                //.requestMatchers(HttpMethod.GET, "/product/**").permitAll() //해당 주소값의 get요청만 허용
                                //.requestMatchers(HttpMethod.POST, "/product/**").permitAll() //해당 주소값의 post요청만 허용
                                //.requestMatchers("/todo-api").hasAnyRole("USER", "ADMIN")   //해당 요청 가능한 권한(역할) 지정
                                //.anyRequest().hasRole("ADMIN")  //이외의 모든 요청은 ADMIN만 가능
                                .anyRequest().permitAll()   //이외의 요청은 로그인 해야만(authenticated()) 사용 가능
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                //기존 필터(UsernamePasswordAuthenticationFilter.class) 전에 해당 필터(jwtAuthenticationFilter) 사용
                .exceptionHandling(except -> {  //이 세팅 안하면 whitelabel에러만 나옴
                    except.authenticationEntryPoint(new JwtAuthenticationEntryPoint())
                            .accessDeniedHandler(new JwtAccessDeniedHandler());
                })
//                .oauth2Login(oath2 -> oath2.authorizationEndpoint(auth -> auth.baseUri("/oauth2/authorization")
//                                        .authorizationRequestRepository(requestCookieRepository)
//                                ).redirectionEndpoint(redirection -> redirection.baseUri("/*/oauth2/code/*"))
//                                .userInfoEndpoint(userinfo -> userinfo.userService(customOAuth2UserService))
//                                .successHandler(successHandler)
//                                .failureHandler(failureHandler)
//                )
                .build();  //적는 순서 중요
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
