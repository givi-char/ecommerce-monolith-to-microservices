package com.givi.ecommerce.userservice.auth.entity;

import com.givi.ecommerce.userservice.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "refresh_tokens")
@Getter
@Setter
@NoArgsConstructor
public class RefreshToken extends BaseEntity {

    @Column(
            name = "token",
            nullable = false,
            unique = true,
            length = 512
    )
    private String token;

    @Column(
            name = "username",
            nullable = false
    )
    private String username;

    @Column(
            name = "expires_at",
            nullable = false
    )
    private Instant expiresAt;
}