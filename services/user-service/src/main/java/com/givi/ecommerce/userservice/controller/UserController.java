package com.givi.ecommerce.userservice.controller;

import com.givi.ecommerce.userservice.dto.CreateUserRequest;
import com.givi.ecommerce.userservice.dto.UpdateUserRequest;
import com.givi.ecommerce.userservice.dto.UserResponse;
import com.givi.ecommerce.userservice.service.UserService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    public UserController(
            UserService userService
    ) {
        this.userService = userService;
    }

    @GetMapping("/paged")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<UserResponse>> getUsersPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {

        Pageable pageable =
                PageRequest.of(page, size);

        return ResponseEntity.ok(
                userService.getAllUsers(pageable)
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize(
            "isAuthenticated() and " +
                    "(@securityUtils.isUserId(#id) or hasRole('ADMIN'))"
    )
    public ResponseEntity<UserResponse> getUserById(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                userService.getUserById(id)
        );
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> createUser(
            @Valid @RequestBody CreateUserRequest request
    ) {

        return ResponseEntity.ok(
                userService.createUser(request)
        );
    }

    @PutMapping("/{id}")
    @PreAuthorize(
            "isAuthenticated() and " +
                    "(@securityUtils.isUserId(#id) or hasRole('ADMIN'))"
    )
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserRequest request
    ) {

        return ResponseEntity.ok(
                userService.updateUser(id, request)
        );
    }

    @DeleteMapping("/{id}")
    @PreAuthorize(
            "isAuthenticated() and " +
                    "(@securityUtils.isUserId(#id) or hasRole('ADMIN'))"
    )
    public ResponseEntity<Void> deleteUser(
            @PathVariable Long id
    ) {

        userService.deleteUser(id);

        return ResponseEntity.noContent().build();
    }
}