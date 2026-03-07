package com.college.academix.controller;

import com.college.academix.dto.*;
import com.college.academix.model.User;
import com.college.academix.repository.UserRepository;
import com.college.academix.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Login and password management")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Operation(summary = "Login", description = "Authenticate with email and password to receive a JWT token")
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success("Login successful", response));
    }

    @Operation(summary = "Change Password", description = "Change the current user's password (requires authentication)")
    @PostMapping("/change-password")
    public ResponseEntity<ApiResponse<String>> changePassword(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody ChangePasswordRequest request) {
        String result = authService.changePassword(userDetails.getUsername(), request);
        return ResponseEntity.ok(ApiResponse.success(result));
    }
}
