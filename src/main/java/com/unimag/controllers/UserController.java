package com.unimag.controllers;

import com.unimag.DTO.AuthDTO.*;
import com.unimag.DTO.UserDTO.*;
import com.unimag.entities.Enums.StatusUser;
import com.unimag.security.service.AuthService;
import com.unimag.security.service.CustomUserDetails;
import com.unimag.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Validated
@Slf4j
public class UserController {

    private final UserService userService;
    private final AuthService authService;

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<userResponse> getCurrentUser(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        log.debug("Getting current user profile for: {}", userDetails.getUsername());
        userResponse response = userService.getById(userDetails.getId());
        return ResponseEntity.ok(response);
    }


    @PutMapping("/update-me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<userResponse> updateCurrentUser(
            Authentication authentication,
            @Valid @RequestBody UserSelfUpdateRequest request
    ) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long userId = userDetails.getId();

        log.info("User {} updating their own profile", userId);

        userResponse updated = userService.update(request,userId);
        return ResponseEntity.ok(updated);
    }


    @PatchMapping("/me/password")
    public ResponseEntity<MessageResponse> changeOwnPassword(
            @Valid @RequestBody ChangePasswordRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        log.info("User {} changing password", userDetails.getUsername());
        MessageResponse response = authService.changePassword(userDetails.getUsername(), request);
        return ResponseEntity.ok(response);
    }


    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<userResponse> create(@Valid @RequestBody userCreateRequest request) {
        log.info("Admin creating new user: {}", request.role());
        userResponse response = userService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


//    @PutMapping("/update/{id}")
//    @PreAuthorize("hasAnyRole('ADMIN', 'DISPATCHER')")
//    public ResponseEntity<userResponse> update(
//            @PathVariable Long id,
//            @Valid @RequestBody userUpdateRequest request) {
//        log.info("Updating user ID: {}", id);
//        userResponse response = userService.update(id, request);
//        return  ResponseEntity.ok(response);
//    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('DISPATCHER') or #id == authentication.principal.id")
    public ResponseEntity<userResponse> getUserById(@PathVariable Long id) {
        log.debug("Getting user by ID: {}", id);
        userResponse response = userService.getById(id);
        return ResponseEntity.ok(response);
    }


    @GetMapping("/email/{email}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DISPATCHER')")
    public ResponseEntity<Boolean> getUserByEmail(@PathVariable String email) {
        log.debug("Getting user by email: {}", email);
        boolean response = userService.getByEmail(email);
        return ResponseEntity.ok(response);
    }


    @GetMapping("/phone/{phone}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DISPATCHER')")
    public ResponseEntity<Boolean> getUserByPhone(@PathVariable String phone) {
        log.debug("Getting user by phone: {}", phone);
        boolean response = userService.getByPhone(phone);
        return ResponseEntity.ok(response);
    }


    @GetMapping("/all-users")
    @PreAuthorize("hasAnyRole('ADMIN', 'DISPATCHER')")
    public ResponseEntity<List<userResponse>> getAllUsers(Pageable pageable) {
        log.debug("Getting all users");
        List<userResponse> response = (List<userResponse>) userService.getAll(pageable);
        return ResponseEntity.ok(response);
    }


    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        log.info("Admin deleting user: {}", id);
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }


    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'DISPATCHER')")
    public ResponseEntity<userResponse> changeUserStatus(
            @PathVariable Long id,
            @RequestParam StatusUser status) {
        log.info("Changing status for user ID: {} to {}", id, status);
        userResponse response = userService.changeStatus(id, status);
        return ResponseEntity.ok(response);
    }


    @GetMapping("/exists/email/{email}")
    public ResponseEntity<Boolean> existsByEmail(@PathVariable String email) {
        log.debug("Checking if email exists: {}", email);
        boolean exists = userService.getByEmail(email);
        return ResponseEntity.ok(exists);
    }


    @GetMapping("/exists/phone/{phone}")
    public ResponseEntity<Boolean> getByPhone(@PathVariable String phone) {
        log.debug("Checking if phone exists: {}", phone);
        boolean exists = userService.getByPhone(phone);
        return ResponseEntity.ok(exists);
    }

    @PostMapping("/check")
    public ResponseEntity<UserAvailabilityResponse> checkAvailability(
            @Valid @RequestBody UserCheckRequest request) {
        log.debug("Checking availability for email: {} and phone: {}",
                request.email(), request.phone());

        boolean emailAvailable = !userService.getByEmail(request.email());
        boolean phoneAvailable = !userService.getByPhone(request.phone());

        UserAvailabilityResponse response = new UserAvailabilityResponse(emailAvailable, phoneAvailable);
        return ResponseEntity.ok(response);
    }


    @PutMapping("/me/complete")
    public ResponseEntity<userResponse> updateOwnProfileComplete(
            @Valid @RequestBody UserSelfUpdateRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        log.info("User {} updating complete profile", userDetails.getUsername());

        userResponse response = userService.update(request, userDetails.getId());
        return ResponseEntity.ok(response);
    }
}
