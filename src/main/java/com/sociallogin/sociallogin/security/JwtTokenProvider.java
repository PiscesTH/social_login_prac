package com.sociallogin.sociallogin.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sociallogin.sociallogin.common.AppProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.util.Date;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {
    //    @Value("${springboot.jwt.secret}")    생성자 이용 안할 때. final 없어야 함
//    private final String secret;    //암호화 할 때 사용하는 키 ?
//    private final String headerSchemeName;
//    private final String tokenType;
    private final AppProperties appProperties;  //위 내용 객체로 처리
    private final ObjectMapper om;
    private SecretKeySpec secretKeySpec;

/*    public JwtTokenProvider(@Value("${springboot.jwt.secret}") String secret,
                            @Value("${springboot.jwt.header-scheme-name}") String headerSchemeName,
                            @Value("${springboot.jwt.token-type}") String tokenType) {
        this.secret = secret;
        this.headerSchemeName = headerSchemeName;
        this.tokenType = tokenType;
    }*/

    @PostConstruct  //사용조건 : 빈등록 -> DI되고 나서 메서드 호출 하는 방법
    public void init() {
        this.secretKeySpec = new SecretKeySpec(appProperties.getJwt().getSecret().getBytes(),
                                                SignatureAlgorithm.HS256.getJcaName());
    }

    private String generateToken(MyPrincipal principal, long tokenValidMs) {
        return Jwts.builder()
                .claims(createClaims(principal))    //토큰에 담기는 정보
                .issuedAt(new Date(System.currentTimeMillis()))     //발행시간 설정
                .expiration(new Date(System.currentTimeMillis() + tokenValidMs))    //만료시간 설정
                .signWith(this.secretKeySpec)
                .compact();
    }

    public String generateAccessToken(MyPrincipal principal) {
        return generateToken(principal, appProperties.getJwt().getAccessTokenExpiry());
    }

    public String generateRefreshToken(MyPrincipal principal) {
        return generateToken(principal, appProperties.getJwt().getRefreshTokenExpiry());
    }

    private Claims createClaims(MyPrincipal principal) {
        try {
            String json = om.writeValueAsString(principal);
            return Jwts.claims()
                    .add("user", json)
                    .build();
        } catch (Exception e) {
            return null;
        }
    }

    private Claims getAllClaims(String token) { //Claims : key와 value 저장 가능
        return Jwts
                .parser()
                .verifyWith(secretKeySpec)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String resolveToken(HttpServletRequest req) {
        String auth = req.getHeader(appProperties.getJwt().getHeaderSchemeName());
        if (auth == null) {
            return null;
        }
        if (auth.startsWith(appProperties.getJwt().getTokenType())) {
            return auth.substring(appProperties.getJwt().getTokenType().length()).trim();
        }
        return null;
    }

    public boolean isValidateToken(String token) {
        try {
            return !getAllClaims(token).getExpiration().before(new Date());
            //만료시간이 현재시간보다 전이면 false, 후면 true
        } catch (Exception e) {
            return false;
        }
    }

    public Authentication getAuthentication(String token) {     //Authentication 에 담을 UsernamePasswordAuthenticationToken 값 얻을 때 사용
        UserDetails userDetails = getUserDetailsFromToken(token);
        return userDetails == null ?
                null : new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }

    public UserDetails getUserDetailsFromToken(String token) {
        try {
            Claims claims = getAllClaims(token);
            String json = (String) claims.get("user");
            MyPrincipal myPrincipal = om.readValue(json, MyPrincipal.class);
            return MyUserDetails.builder()
                    .myPrincipal(myPrincipal)
                    .build();
        } catch (Exception e) {
            return null;
        }
    }
}
