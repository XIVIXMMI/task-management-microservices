package com.taskmanagement.userservice.application.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * JwtAuthenticationFilter: Intercept mọi HTTP request
 * - Extract JWT từ Authorization header
 * - Validate JWT
 * - Set authentication for SecurityContext
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain
    ) throws ServletException, IOException {
        try {
            // Extract jwt from request -> getHeader
            String jwt = getJwtFromRequest(request);
            // Validate token
            if (StringUtils.hasText(jwt) && jwtUtil.validateToken(jwt)) {
                String username = jwtUtil.extractUsername(jwt);
                UUID userId = jwtUtil.extractUserId(jwt);
                String email = jwtUtil.extractEmail(jwt);
                List<String> roles = jwtUtil.extractRole(jwt);

                // Build UserDetails from JWT claims
                UserDetails userDetails = CustomUserDetails.fromJwtClaims(
                    userId,
                    email,
                    roles
                );

                // create an authentication object
                UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null, // credentials (password) - no need cause authenticated
                        userDetails.getAuthorities()
                        // -> need to pass Authorities cause userDetails above still not an Authenticated Object
                    );
                // set details for save ip and session-id for audit/tracking
                authentication.setDetails(
                    new WebAuthenticationDetailsSource().buildDetails(request)
                );
                // set authentication vào SecurityContext -> Spring Security knơw that user is authenticated
                log.debug("Set authentication for user: {}", username);
                // must check null to avoid override authenticated
                if (
                    SecurityContextHolder.getContext().getAuthentication() ==
                    null
                ) {
                    SecurityContextHolder.getContext().setAuthentication(
                        authentication
                    );
                }
            }
        } catch (Exception ex) {
            log.debug("Cannot set authentication: {}", ex.getMessage());
        }
        // continue filter chain
        filterChain.doFilter(request, response);
    }

    /**
     * Extract JWT từ Authorization header
     * Format: "Bearer <token>"
     */
    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");

        // Check format: "BearerToken<token>"
        if (
            StringUtils.hasText(bearerToken) &&
            bearerToken.startsWith("Bearer ")
        ) {
            return bearerToken.substring(7); // remove "Bearer " prefix
        }
        return null;
    }
}
