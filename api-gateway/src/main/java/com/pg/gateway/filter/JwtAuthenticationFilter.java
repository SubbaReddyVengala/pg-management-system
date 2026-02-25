package com.pg.gateway.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.List;

@Slf4j
@Component
public class JwtAuthenticationFilter
        extends AbstractGatewayFilterFactory<JwtAuthenticationFilter.Config> {

    @Value("${jwt.secret}")
    private String jwtSecret;

    private static final List<String> PUBLIC_ENDPOINTS = List.of(
            "/api/auth/register",
            "/api/auth/login",
            "/actuator",
            "/eureka"
    );

    public JwtAuthenticationFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            String path = request.getURI().getPath();

            if (isPublicEndpoint(path)) {
                return chain.filter(exchange);
            }

            if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                log.warn("Missing Authorization header for path: {}", path);
                return unauthorizedResponse(exchange, "Missing Authorization header");
            }

            String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return unauthorizedResponse(exchange, "Invalid Authorization header format");
            }

            String token = authHeader.substring(7);
            try {
                Claims claims = validateToken(token);
                ServerHttpRequest modifiedRequest = request.mutate()
                        .header("X-User-Id",    claims.getSubject())
                        .header("X-User-Role",  claims.get("role", String.class))
                        .header("X-User-Email", claims.get("email", String.class))
                        .build();
                return chain.filter(exchange.mutate().request(modifiedRequest).build());
            } catch (JwtException e) {
                log.error("JWT validation failed: {}", e.getMessage());
                return unauthorizedResponse(exchange, "Invalid or expired token");
            }
        };
    }

    private Claims validateToken(String token) {
        // BASE64 decoded key — matches Auth Service exactly
        Key signingKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
        return Jwts.parserBuilder()          // <-- parserBuilder() for jjwt 0.11.5
                .setSigningKey(signingKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private boolean isPublicEndpoint(String path) {
        return PUBLIC_ENDPOINTS.stream().anyMatch(path::startsWith);
    }

    private Mono<Void> unauthorizedResponse(ServerWebExchange exchange, String message) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        exchange.getResponse().getHeaders().add("Content-Type", "application/json");
        var body = exchange.getResponse().bufferFactory()
                .wrap(("{\"errorCode\":\"UNAUTHORIZED\",\"message\":\"" + message + "\"}")
                        .getBytes(StandardCharsets.UTF_8));
        return exchange.getResponse().writeWith(Mono.just(body));
    }

    public static class Config {
    }
}