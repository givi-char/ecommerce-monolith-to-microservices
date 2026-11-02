package com.givi.ecommerce.userservice.service.impl;

import com.givi.ecommerce.userservice.dto.CreateUserRequest;
import com.givi.ecommerce.userservice.dto.UpdateUserRequest;
import com.givi.ecommerce.userservice.dto.UserResponse;
import com.givi.ecommerce.userservice.entity.User;
import com.givi.ecommerce.userservice.enums.Role;
import com.givi.ecommerce.userservice.enums.SellerStatus;
import com.givi.ecommerce.userservice.enums.UserStatus;
import com.givi.ecommerce.userservice.exception.DuplicateResourceException;
import com.givi.ecommerce.userservice.exception.ResourceNotFoundException;
import com.givi.ecommerce.userservice.mapper.UserMapper;
import com.givi.ecommerce.userservice.repository.UserRepository;
import com.givi.ecommerce.userservice.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(
            UserRepository userRepository,
            UserMapper userMapper,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Page<UserResponse> getAllUsers(
            Pageable pageable
    ) {

        return userRepository
                .findAll(pageable)
                .map(userMapper::toResponse);
    }

    @Override
    public UserResponse getUserById(
            Long id
    ) {

        User user = getUserOrThrow(id);

        return userMapper.toResponse(user);
    }

    @Override
    public UserResponse createUser(
            CreateUserRequest request
    ) {

        if (userRepository.existsByEmail(
                request.getEmail()
        )) {

            throw new DuplicateResourceException(
                    "User with email "
                            + request.getEmail()
                            + " already exists"
            );
        }

        User user = new User();

        user.setEmail(request.getEmail());

        user.setPasswordHash(
                passwordEncoder.encode(
                        request.getPassword()
                )
        );

        user.setFullName(
                request.getFullName()
        );

        user.setPhone(
                request.getPhone()
        );

        user.setProvider(
                request.getProvider()
        );

        user.setStatus(
                UserStatus.ACTIVE
        );

        user.setSellerStatus(
                SellerStatus.NOT_A_SELLER
        );

        user.setFailedLoginAttempts(0);

        HashSet<Role> roles =
                new HashSet<>();

        roles.add(
                Role.ROLE_USER
        );

        user.setRoles(roles);

        User savedUser =
                userRepository.save(user);

        return userMapper.toResponse(
                savedUser
        );
    }

    @Override
    public UserResponse updateUser(
            Long id,
            UpdateUserRequest request
    ) {

        User user =
                getUserOrThrow(id);

        if (request.getEmail() != null
                && !request.getEmail()
                .equals(user.getEmail())) {

            if (userRepository
                    .existsByEmail(
                            request.getEmail()
                    )) {

                throw new DuplicateResourceException(
                        "User with email "
                                + request.getEmail()
                                + " already exists"
                );
            }

            user.setEmail(
                    request.getEmail()
            );
        }

        if (request.getFullName() != null) {

            user.setFullName(
                    request.getFullName()
            );
        }

        if (request.getPhone() != null) {

            user.setPhone(
                    request.getPhone()
            );
        }

        if (request.getStatus() != null) {

            user.setStatus(
                    request.getStatus()
            );
        }

        User updatedUser =
                userRepository.save(user);

        return userMapper.toResponse(
                updatedUser
        );
    }

    @Override
    public void deleteUser(
            Long id
    ) {

        User user =
                getUserOrThrow(id);

        userRepository.delete(user);
    }

    @Override
    @Transactional
    public UserResponse applyForSeller(
            Long userId
    ) {

        User user =
                getUserOrThrow(userId);

        if (user.getStatus()
                != UserStatus.ACTIVE) {

            throw new IllegalStateException(
                    "User account is not active"
            );
        }

        SellerStatus currentStatus =
                user.getSellerStatus();

        if (currentStatus
                == SellerStatus.REQUESTED) {

            throw new IllegalStateException(
                    "Seller application is already pending"
            );
        }

        if (currentStatus
                == SellerStatus.APPROVED) {

            throw new IllegalStateException(
                    "User is already an approved seller"
            );
        }

        if (currentStatus
                == SellerStatus.SUSPENDED) {

            throw new IllegalStateException(
                    "Suspended seller cannot submit a new application"
            );
        }

        user.setSellerStatus(
                SellerStatus.REQUESTED
        );

        user.setSellerRequestedAt(
                LocalDateTime.now()
        );

        user.setSellerApprovedAt(null);
        user.setSellerRejectionReason(null);
        user.setApprovedByAdmin(null);

        User updatedUser =
                userRepository.save(user);

        return userMapper.toResponse(
                updatedUser
        );
    }

    @Override
    @Transactional
    public UserResponse approveSellerApplication(
            Long userId
    ) {

        User user =
                getUserOrThrow(userId);

        if (user.getSellerStatus()
                != SellerStatus.REQUESTED) {

            throw new IllegalStateException(
                    "User does not have a pending seller application"
            );
        }

        User admin =
                getCurrentAuthenticatedUser();

        user.setSellerStatus(
                SellerStatus.APPROVED
        );

        user.setSellerApprovedAt(
                LocalDateTime.now()
        );

        user.setSellerRejectionReason(null);
        user.setApprovedByAdmin(admin);

        user.getRoles().add(
                Role.ROLE_SELLER
        );

        User updatedUser =
                userRepository.save(user);

        return userMapper.toResponse(
                updatedUser
        );
    }

    @Override
    @Transactional
    public UserResponse rejectSellerApplication(
            Long userId,
            String rejectionReason
    ) {

        User user =
                getUserOrThrow(userId);

        if (user.getSellerStatus()
                != SellerStatus.REQUESTED) {

            throw new IllegalStateException(
                    "User seller status must be REQUESTED to reject"
            );
        }

        User admin =
                getCurrentAuthenticatedUser();

        user.setSellerStatus(
                SellerStatus.REJECTED
        );

        user.setSellerRejectionReason(
                rejectionReason
        );

        user.setApprovedByAdmin(admin);

        user.getRoles().remove(
                Role.ROLE_SELLER
        );

        User updatedUser =
                userRepository.save(user);

        return userMapper.toResponse(
                updatedUser
        );
    }

    @Override
    @Transactional
    public UserResponse suspendSeller(
            Long userId,
            String suspensionReason
    ) {

        User user =
                getUserOrThrow(userId);

        if (user.getSellerStatus()
                != SellerStatus.APPROVED) {

            throw new IllegalStateException(
                    "User must have APPROVED seller status to suspend"
            );
        }

        User admin =
                getCurrentAuthenticatedUser();

        user.setSellerStatus(
                SellerStatus.SUSPENDED
        );

        user.setSellerRejectionReason(
                suspensionReason
        );

        user.setApprovedByAdmin(admin);

        user.getRoles().remove(
                Role.ROLE_SELLER
        );

        User updatedUser =
                userRepository.save(user);

        return userMapper.toResponse(
                updatedUser
        );
    }

    @Override
    @Transactional
    public UserResponse activateSuspendedSeller(
            Long userId
    ) {

        User user =
                getUserOrThrow(userId);

        if (user.getSellerStatus()
                != SellerStatus.SUSPENDED) {

            throw new IllegalStateException(
                    "User must have SUSPENDED seller status to activate"
            );
        }

        User admin =
                getCurrentAuthenticatedUser();

        user.setSellerStatus(
                SellerStatus.APPROVED
        );

        user.setSellerRejectionReason(null);

        user.setApprovedByAdmin(admin);

        user.getRoles().add(
                Role.ROLE_SELLER
        );

        User updatedUser =
                userRepository.save(user);

        return userMapper.toResponse(
                updatedUser
        );
    }

    private User getUserOrThrow(
            Long userId
    ) {

        return userRepository
                .findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found with id: "
                                        + userId
                        )
                );
    }

    private User getCurrentAuthenticatedUser() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()) {

            throw new IllegalStateException(
                    "Admin authentication is required"
            );
        }

        String email =
                authentication.getName();

        return userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Authenticated user not found"
                        )
                );
    }
}