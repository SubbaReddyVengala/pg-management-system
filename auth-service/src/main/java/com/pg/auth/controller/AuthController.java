package com.pg.auth.controller;

import com.pg.auth.dto.AuthResponse;
import com.pg.auth.dto.LoginRequest;
import com.pg.auth.dto.RegisterRequest;
import com.pg.auth.security.JwtUtils;
import com.pg.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication")
public class AuthController {

    private final AuthService authService;
    private final JwtUtils jwtUtils;
    @PostMapping("/register")
    @Operation(summary = "Register a new user")
    public ResponseEntity<AuthResponse> register(
            @Valid @RequestBody RegisterRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(authService.register(request));
    }

    @PostMapping("/login")
    @Operation(summary = "Login and receive JWT tokens")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/logout")
    @Operation(summary = "Logout and revoke refresh token")
    public ResponseEntity<Void> logout(
            @RequestHeader("X-User-Id") Long userId) {
        authService.logout(userId);
        return ResponseEntity.noContent().build();
    }
    // GET /api/auth/me — returns current logged-in user info
// Angular calls this on app startup to restore session after page refresh
// The API Gateway injects X-User-* headers from the JWT token automatically
    @GetMapping("/me")
    public ResponseEntity<Map<String, String>> getCurrentUser(
            @RequestHeader(value = "X-User-Id",    required = false) String userId,
            @RequestHeader(value = "X-User-Role",  required = false) String role,
            @RequestHeader(value = "X-User-Email", required = false) String email) {

        return ResponseEntity.ok(Map.of(
                "userId", userId != null ? userId : "",
                "role",   role   != null ? role   : "",
                "email",  email  != null ? email  : ""
        ));
    }
    // POST /api/auth/refresh — issues new token without requiring password
// Angular HTTP interceptor calls this automatically before token expires
    @PostMapping("/refresh")
    public ResponseEntity<Map<String, String>> refresh(
            @RequestHeader("Authorization") String authHeader) {

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid Authorization header"));
        }

        String oldToken = authHeader.substring(7);
        String newToken = jwtUtils.refreshToken(oldToken);

        return ResponseEntity.ok(Map.of(
                "accessToken", newToken,
                "tokenType",   "Bearer"
        ));
    }


}
