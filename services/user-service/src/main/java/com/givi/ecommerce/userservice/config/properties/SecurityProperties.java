package com.givi.ecommerce.userservice.config.properties;

import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "config.security")
@Getter
@Setter
public class SecurityProperties {

    @Positive
    private int maxFailedAttempts;

    @Positive
    private int lockoutDurationMinutes;
}