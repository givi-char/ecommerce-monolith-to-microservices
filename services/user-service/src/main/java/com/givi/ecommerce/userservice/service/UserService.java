package com.givi.ecommerce.userservice.service;

import com.givi.ecommerce.userservice.dto.CreateUserRequest;
import com.givi.ecommerce.userservice.dto.UpdateUserRequest;
import com.givi.ecommerce.userservice.dto.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {

    Page<UserResponse> getAllUsers(Pageable pageable);

    UserResponse getUserById(Long id);

    UserResponse createUser(CreateUserRequest request);

    UserResponse updateUser(Long id, UpdateUserRequest request);

    void deleteUser(Long id);
}