package com.taskmanagement.userservice.application.utils;

import com.taskmanagement.userservice.infrastructure.security.CustomUserDetails;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.crypto.SecretKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

/**
 * JwtUtil: Class handle all logic relative JWT
 * - Generate token (access and refresh)
 * - Validate token
 * - Extract claims from token
 */
@Slf4j
@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private Long expiration;

    @Value("${jwt.refresh-expiration}")
    private Long refreshExpiration;

    public String generateToken(CustomUserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userDetails.getId().toString());
        claims.put("email", userDetails.getEmail());

        claims.put(
            "roles",
            userDetails
                .getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList())
        );

        claims.put("tokenType","ACCESS");

        return createToken(claims, userDetails.getUsername(), expiration);
    }

    public String generateRefreshToken(CustomUserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userDetails.getId().toString());
        claims.put("email", userDetails.getEmail());

        claims.put("tokenType", "REFRESH");
        return createToken(
            claims,
            userDetails.getUsername(),
            refreshExpiration
        );
    }

    private String createToken(
        Map<String, Object> claims,
        String subject,
        Long expiration
    ) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        return Jwts.builder()
            .claims(claims)
            .subject(subject)
            .issuedAt(now)
            .expiration(expiryDate)
            .signWith(getSigningKey())
            .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token);
            return true;
        } catch (SecurityException ex) {
            log.error("Invalid JWT signature: {}", ex.getMessage());
        } catch (MalformedJwtException ex) {
            log.error("Invalid JWT token: {}", ex.getMessage());
        } catch (ExpiredJwtException ex) {
            log.error("Expired JWT token: {}", ex.getMessage());
        } catch (UnsupportedJwtException ex) {
            log.error("Unsupported JWT token: {}", ex.getMessage());
        } catch (IllegalArgumentException ex) {
            log.error("JWT claims string is empty: {}", ex.getMessage());
        }
        return false;
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (
            (username.equals(userDetails.getUsername())) &&
            !isTokenExpired(token)
        );
    }

    public String extractUsername(String token) {
        return extractClaims(token, Claims::getSubject);
    }

    public UUID extractUserId(String token) {
        String userIdStr = extractClaims(token, claims ->
            claims.get("userId", String.class)
        );
        return UUID.fromString(userIdStr);
    }

    public Date extractExpiration(String token) {
        return extractClaims(token, Claims::getExpiration);
    }

    public List<String> extractRole(String token) {
        return extractClaims(token, claims -> {
            List<?> roles = claims.get("roles", List.class);
            if (roles == null) {
                return Collections.emptyList();
            }
            return roles
                .stream()
                .map(Object::toString)
                .collect(Collectors.toList());
        });
    }

    public String extractEmail(String token) {
        return extractClaims(token, claims ->
            claims.get("email", String.class)
        );
    }

    /**
     * Generic method: Extract any claim
     */
    public <T> T extractClaims(
        String token,
        Function<Claims, T> claimsTFunction
    ) {
        final Claims claims = extractAllClaims(token);
        return claimsTFunction.apply(claims);
    }

    /**
     * Extract all claims from token
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
            .verifyWith(getSigningKey())
            .build()
            .parseSignedClaims(token)
            .getPayload();
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private SecretKey getSigningKey() {
        byte[] keyBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public boolean isRefreshToken(String token) {
        try {
            Claims claims = extractAllClaims(token);
            return "REFRESH".equals(claims.get("tokenType", String.class));
        } catch (Exception e){
            log.error("Failed to extract token type: {}",e.getMessage());
            return false;
        }
    }

}
