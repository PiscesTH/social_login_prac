package com.sociallogin.sociallogin.common;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

@Getter
@ConfigurationProperties(prefix = "app")    //프로그램 시작 클래스에 @ConfigurationPropertiesScan 필요
public class AppProperties {    //yml에 작성한 property 값 가져오는 클래스

    private final Jwt jwt = new Jwt();
    private final Oauth2 oauth2 = new Oauth2();

    @Getter
    @Setter
    public static class Jwt {
        private String secret;
        private String headerSchemeName;
        private String tokenType;
        private long accessTokenExpiry;
        private long refreshTokenExpiry;
        private int refreshCookieMaxAge;

        public void setRefreshTokenExpiry(long refreshTokenExpiry) {
            this.refreshTokenExpiry = refreshTokenExpiry;
            refreshCookieMaxAge = (int) refreshTokenExpiry / 1000;
        }
    }

    @Getter
    public static final class Oauth2 {   //final : 상속 불가능
        private List<String> authorizedRedirectUris = new ArrayList<>();
    }
}
