
package com.smartacademic.controller;

import com.smartacademic.common.ApiResponse;
import com.smartacademic.dto.ChangePasswordRequest;
import com.smartacademic.dto.LoginRequest;
import com.smartacademic.dto.LoginResponse;
import com.smartacademic.service.AuthService;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(
            @Valid @RequestBody LoginRequest request) {

        LoginResponse response = authService.login(request);

        return ResponseEntity.ok(
                ApiResponse.success("Login successful", response));
    }

    @PostMapping("/change-password")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @Valid @RequestBody ChangePasswordRequest request,
            Authentication authentication) {

        authService.changePassword(
                authentication.getName(), request);

        return ResponseEntity.ok(
                ApiResponse.success("Password changed successfully", null));
    }
}
