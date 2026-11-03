package com.givi.ecommerce.userservice.controller.admin;

import com.givi.ecommerce.userservice.dto.UserResponse;
import com.givi.ecommerce.userservice.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

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
    public ResponseEntity<Page<UserResponse>>
    getUsersPaged(
            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "20")
            int size
    ) {

        Pageable pageable =
                PageRequest.of(page, size);

        return ResponseEntity.ok(
                userService.getAllUsers(pageable)
        );
    }

    @GetMapping("/sellers/pending")
    public ResponseEntity<Page<UserResponse>>
    getPendingSellerApplications(
            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "20")
            int size
    ) {

        Pageable pageable =
                PageRequest.of(page, size);

        return ResponseEntity.ok(
                userService
                        .getPendingSellerApplications(
                                pageable
                        )
        );
    }

    @PutMapping("/{userId}/activate")
    public ResponseEntity<UserResponse>
    activateUser(
            @PathVariable Long userId
    ) {

        return ResponseEntity.ok(
                userService.activateUser(
                        userId
                )
        );
    }

    @PutMapping("/{userId}/deactivate")
    public ResponseEntity<UserResponse>
    deactivateUser(
            @PathVariable Long userId
    ) {

        return ResponseEntity.ok(
                userService.deactivateUser(
                        userId
                )
        );
    }

    @PutMapping("/{userId}/unlock")
    public ResponseEntity<UserResponse>
    unlockUser(
            @PathVariable Long userId
    ) {

        return ResponseEntity.ok(
                userService.unlockUser(
                        userId
                )
        );
    }

    @PutMapping("/{userId}/seller/approve")
    public ResponseEntity<UserResponse>
    approveSeller(
            @PathVariable Long userId
    ) {

        return ResponseEntity.ok(
                userService
                        .approveSellerApplication(
                                userId
                        )
        );
    }

    @PutMapping("/{userId}/seller/reject")
    public ResponseEntity<UserResponse>
    rejectSeller(
            @PathVariable Long userId,
            @RequestBody Map<String, String> body
    ) {

        String reason =
                body.get("reason");

        return ResponseEntity.ok(
                userService
                        .rejectSellerApplication(
                                userId,
                                reason
                        )
        );
    }

    @PutMapping("/{userId}/seller/suspend")
    public ResponseEntity<UserResponse>
    suspendSeller(
            @PathVariable Long userId,
            @RequestBody Map<String, String> body
    ) {

        String reason =
                body.get("reason");

        return ResponseEntity.ok(
                userService.suspendSeller(
                        userId,
                        reason
                )
        );
    }

    @PutMapping("/{userId}/seller/reactivate")
    public ResponseEntity<UserResponse>
    reactivateSeller(
            @PathVariable Long userId
    ) {

        return ResponseEntity.ok(
                userService
                        .activateSuspendedSeller(
                                userId
                        )
        );
    }
}