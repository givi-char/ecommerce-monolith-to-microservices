package com.givi.ecommerce.userservice.controller.seller;

import com.givi.ecommerce.userservice.dto.UserResponse;
import com.givi.ecommerce.userservice.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
public class SellerController {

    private final UserService userService;

    public SellerController(
            UserService userService
    ) {
        this.userService = userService;
    }

    @PostMapping("/{userId}/seller/apply")
    @PreAuthorize(
            "hasRole('USER') and " +
                    "@securityUtils.isUserId(#userId)"
    )
    public ResponseEntity<UserResponse> applyForSeller(
            @PathVariable Long userId
    ) {

        return ResponseEntity.ok(
                userService.applyForSeller(userId)
        );
    }

    @GetMapping("/seller/dashboard-test")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<String> sellerDashboardTest() {

        return ResponseEntity.ok(
                "Seller access granted"
        );
    }
}