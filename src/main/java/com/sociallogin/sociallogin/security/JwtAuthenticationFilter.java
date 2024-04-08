package com.sociallogin.sociallogin.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter { //서블릿 이전에 무조건 지나게 되는 필터
    //로그인 한 사용자와 로그인 안 한 사용자 구분 ?
    //필터는 한 번만 거침(실행)

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String token = jwtTokenProvider.resolveToken(request);
        log.info("token : {}",token);

        if (token != null && jwtTokenProvider.isValidateToken(token)){
            Authentication auth = jwtTokenProvider.getAuthentication(token);
//            UsernamePasswordAuthenticationToken auth = (UsernamePasswordAuthenticationToken) jwtTokenProvider.getAuthentication(token);
            if (auth != null) {
                SecurityContextHolder.getContext().setAuthentication(auth); //사용자가 동시에 접속해도 다른 context 사용하게 됨.
                //로그인 여부를 확인하는 방법 -> Authentication 에 값이 있는지 없는지 확인하여 판단. null이 아니면 로그인 된 걸로 처리하는 중.
                //판단은 시큐리티가 함.
            }
        }
        filterChain.doFilter(request, response);    //다음 필터에 request와 resposne 전달
    }
}
