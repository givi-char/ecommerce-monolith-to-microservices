package com.givi.ecommerce.userservice.dto;

import com.givi.ecommerce.userservice.enums.UserStatus;
import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateUserRequest {

    @Email(message = "Email must be valid")
    private String email;

    private String fullName;

    private String phone;

    private UserStatus status;
}