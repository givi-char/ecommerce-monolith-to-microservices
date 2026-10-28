package com.givi.ecommerce.userservice.config.properties;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Component
@ConfigurationProperties(prefix = "config.jwt")
@Validated
@Getter
@Setter
public class JwtProperties {

    @NotBlank
    private String accessSecret;

    @NotBlank
    private String refreshSecret;

    @Positive
    private long accessTokenExpiration;

    @Positive
    private long refreshTokenExpiration;
}