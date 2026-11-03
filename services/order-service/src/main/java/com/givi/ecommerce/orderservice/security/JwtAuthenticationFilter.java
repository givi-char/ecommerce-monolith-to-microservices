package com.givi.ecommerce.orderservice.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthenticationFilter
        extends OncePerRequestFilter {

    private final OrderJwtUtil orderJwtUtil;

    public JwtAuthenticationFilter(
            OrderJwtUtil orderJwtUtil
    ) {
        this.orderJwtUtil = orderJwtUtil;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String authorizationHeader =
                request.getHeader(
                        "Authorization"
                );

        if (authorizationHeader == null
                || !authorizationHeader.startsWith(
                "Bearer "
        )) {

            filterChain.doFilter(
                    request,
                    response
            );

            return;
        }

        String token =
                authorizationHeader.substring(7);

        if (!orderJwtUtil.validateToken(token)) {

            filterChain.doFilter(
                    request,
                    response
            );

            return;
        }

        Long userId =
                orderJwtUtil.getUserId(token);

        String email =
                orderJwtUtil.getEmail(token);

        List<SimpleGrantedAuthority> authorities =
                orderJwtUtil
                        .getRoles(token)
                        .stream()
                        .map(SimpleGrantedAuthority::new)
                        .toList();

        OrderUserPrincipal principal =
                new OrderUserPrincipal(
                        userId,
                        email
                );

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        principal,
                        null,
                        authorities
                );

        SecurityContextHolder
                .getContext()
                .setAuthentication(authentication);

        filterChain.doFilter(
                request,
                response
        );
    }
}