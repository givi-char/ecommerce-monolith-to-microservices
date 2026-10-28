package com.givi.ecommerce.userservice.mapper;

import com.givi.ecommerce.userservice.dto.UserResponse;
import com.givi.ecommerce.userservice.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserResponse toResponse(User user) {

        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getFullName(),
                user.getPhone(),
                user.getStatus(),
                user.getRoles(),
                user.getProvider(),
                user.getSellerStatus()
        );
    }
}