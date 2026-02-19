package com.pg.auth.service;

import com.pg.auth.dto.AuthResponse;
import com.pg.auth.dto.LoginRequest;
import com.pg.auth.dto.RegisterRequest;
import com.pg.auth.entity.RefreshToken;
import com.pg.auth.entity.User;
import com.pg.auth.repository.RefreshTokenRepository;
import com.pg.auth.repository.UserRepository;
import com.pg.auth.security.AppUserDetails;
import com.pg.auth.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private final UserRepository         userRepo;
    private final RefreshTokenRepository refreshRepo;
    private final PasswordEncoder        passwordEncoder;
    private final JwtUtils               jwtUtils;
    private final AuthenticationManager  authManager;

    public AuthResponse register(RegisterRequest request) {
        if (userRepo.existsByEmail(request.getEmail())) {
            throw new IllegalStateException("Email already registered: " + request.getEmail());
        }
        User user = User.builder()
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole() != null ? request.getRole() : User.Role.TENANT)
                .build();

        userRepo.save(user);
        return buildAuthResponse(user);
    }

    public AuthResponse login(LoginRequest request) {
        Authentication auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(), request.getPassword()));

        AppUserDetails userDetails = (AppUserDetails) auth.getPrincipal();
        User user = userDetails.getUser();

        // Revoke all existing refresh tokens for this user
        refreshRepo.revokeAllByUserId(user.getId());

        return buildAuthResponse(user);
    }

    public void logout(Long userId) {
        refreshRepo.revokeAllByUserId(userId);
    }

    private AuthResponse buildAuthResponse(User user) {
        AppUserDetails details = new AppUserDetails(user);
        String accessToken  = jwtUtils.generateToken(details);
        String refreshToken = UUID.randomUUID().toString();

        refreshRepo.save(RefreshToken.builder()
                .user(user)
                .token(refreshToken)
                .expiryDate(Instant.now().plusSeconds(604800L))
                .build());

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .userId(user.getId())
                .email(user.getEmail())
                .role(user.getRole().name())
                .build();
    }
}
