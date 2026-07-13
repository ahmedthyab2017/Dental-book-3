package com.dantal.auth.controller;

import com.dantal.auth.dto.ChangePasswordRequest;
import com.dantal.auth.dto.ForgotPasswordRequest;
import com.dantal.auth.dto.LoginRequest;
import com.dantal.auth.dto.LogoutRequest;
import com.dantal.auth.dto.RefreshRequest;
import com.dantal.auth.dto.RegisterRequest;
import com.dantal.auth.dto.ResetPasswordRequest;
import com.dantal.auth.dto.TokenPairResponse;
import com.dantal.auth.dto.UserResponse;
import com.dantal.auth.service.AuthService;
import com.dantal.common.dto.ApiResponse;
import com.dantal.security.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.dantal.common.web.RequestHeaders;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Auth endpoints that issue tokens return the raw frontend contract
 * {@code { tokens: { access, refresh } }} — not wrapped in ApiResponse.
 */
@RestController
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Registration, login, and session management")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @Operation(summary = "Register a new clinic and owner account")
    public ResponseEntity<TokenPairResponse> register(HttpServletRequest httpRequest,
                                                      @Valid @RequestBody RegisterRequest request) {
        request.setDeviceId(RequestHeaders.deviceId(httpRequest, request.getDeviceId()));
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(request));
    }

    @PostMapping("/login")
    @Operation(summary = "Authenticate and issue a token pair")
    public TokenPairResponse login(HttpServletRequest httpRequest, @Valid @RequestBody LoginRequest request) {
        request.setDeviceId(RequestHeaders.deviceId(httpRequest, request.getDeviceId()));
        return authService.login(request);
    }

    @PostMapping("/refresh")
    @Operation(summary = "Rotate a refresh token for a new token pair")
    public TokenPairResponse refresh(HttpServletRequest httpRequest, @Valid @RequestBody RefreshRequest request) {
        request.setDeviceId(RequestHeaders.deviceId(httpRequest, request.getDeviceId()));
        return authService.refresh(request);
    }

    @PostMapping("/logout")
    @Operation(summary = "Revoke a refresh token")
    public ResponseEntity<Void> logout(@Valid @RequestBody LogoutRequest request) {
        authService.logout(request);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Get the currently authenticated user")
    public ApiResponse<UserResponse> me(@AuthenticationPrincipal UserPrincipal principal) {
        return ApiResponse.ok(authService.getCurrentUser(principal.getId()));
    }

    @PostMapping("/change-password")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Change the current user's password")
    public ApiResponse<Void> changePassword(@AuthenticationPrincipal UserPrincipal principal,
                                             @Valid @RequestBody ChangePasswordRequest request) {
        authService.changePassword(principal.getId(), request);
        return ApiResponse.ok("Password changed successfully", null);
    }

    @PostMapping("/forgot-password")
    @Operation(summary = "Request a password reset token")
    public ApiResponse<Void> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        authService.forgotPassword(request);
        return ApiResponse.ok("If the email exists, a reset link has been sent", null);
    }

    @PostMapping("/reset-password")
    @Operation(summary = "Reset password using a reset token")
    public ApiResponse<Void> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request);
        return ApiResponse.ok("Password reset successful", null);
    }
}
