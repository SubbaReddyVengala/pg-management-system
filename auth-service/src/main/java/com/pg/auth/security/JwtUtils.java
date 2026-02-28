package com.pg.auth.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.security.Key;
import java.util.*;

@Component
@Slf4j
public class JwtUtils {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration-ms}")
    private long expirationMs;

    public String generateToken(AppUserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role",  userDetails.getRole().name());   // getRole()  works now
        claims.put("email", userDetails.getUsername());

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(String.valueOf(userDetails.getId()))  // getId() works now
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractUserId(String token) {
        return extractAllClaims(token).getSubject();
    }

    public String extractRole(String token) {
        return extractAllClaims(token).get("role", String.class);
    }

    public String extractEmail(String token) {
        return extractAllClaims(token).get("email", String.class);
    }

    public boolean isTokenValid(String token) {
        try {
            extractAllClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            log.warn("Invalid JWT token: {}", e.getMessage());
            return false;
        }
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
    // Validates existing token and issues a new one with fresh 24hr expiry
// Called by the refresh endpoint in AuthController
    public String refreshToken(String oldToken) {
        try {
            // Re-use your existing extractAllClaims() method
            Claims claims = extractAllClaims(oldToken);

            // Build new token with same subject/role/email but fresh expiry
            return Jwts.builder()
                    .setSubject(claims.getSubject())
                    .claim("role",  claims.get("role",  String.class))
                    .claim("email", claims.get("email", String.class))
                    .setIssuedAt(new Date())
                    .setExpiration(new Date(
                            System.currentTimeMillis() + 86400000L)) // 24 hours
                    .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                    .compact();

        } catch (JwtException | IllegalArgumentException e) {
            throw new RuntimeException(
                    "Token is invalid or expired. Please login again.");
        }
    }

}
