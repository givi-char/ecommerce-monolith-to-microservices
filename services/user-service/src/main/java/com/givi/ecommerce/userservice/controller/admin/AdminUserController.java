package com.givi.ecommerce.userservice.controller.admin;

import com.givi.ecommerce.userservice.dto.UserResponse;
import com.givi.ecommerce.userservice.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/users")
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserController {

    private final UserService userService;

    public AdminUserController(
            UserService userService
    ) {
        this.userService = userService;
    }

    @GetMapping("/paged")
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
}