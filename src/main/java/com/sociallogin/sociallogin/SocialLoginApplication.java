package com.sociallogin.sociallogin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class SocialLoginApplication {

    public static void main(String[] args) {
        SpringApplication.run(SocialLoginApplication.class, args);
    }

}
