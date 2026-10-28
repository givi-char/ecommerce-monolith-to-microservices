package com.givi.ecommerce.userservice.dto;

import com.givi.ecommerce.userservice.enums.AuthProvider;
import com.givi.ecommerce.userservice.enums.Role;
import com.givi.ecommerce.userservice.enums.SellerStatus;
import com.givi.ecommerce.userservice.enums.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {

    private Long id;

    private String email;

    private String fullName;

    private String phone;

    private UserStatus status;

    private Set<Role> roles;

    private AuthProvider provider;

    private SellerStatus sellerStatus;
}