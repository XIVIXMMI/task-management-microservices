package com.taskmanagement.userservice.infrastructure.security;

import com.taskmanagement.userservice.domain.entity.Role;
import com.taskmanagement.userservice.domain.entity.RolePermission;
import com.taskmanagement.userservice.domain.entity.User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * CustomUserDetails: Wrapper class for User entity
 * Implement UserDetails for Spring Security can use it
 */
@Getter
@AllArgsConstructor
public class CustomUserDetails implements UserDetails, CredentialsContainer {

    private UUID id;
    private String email;
    private String password;
    private final Collection<? extends GrantedAuthority> authorities;

    private final boolean enabled;
    private final boolean accountNonExpired;
    private final boolean accountNonLocked;
    private final boolean credentialNonExpired;

    /**
     * Factory method: Convert User entity → CustomUserDetails
     * Call by class not instance so this function needs *static*
     */
    public static CustomUserDetails fromUser(User user) {
        /*
            Convert Roles/Permission to GrantedAuthority
            Handle null or empty roles
         */
        Collection<GrantedAuthority> authorities = new ArrayList<>();

        if(user.getRoles() != null && !user.getRoles().isEmpty()){
            for(Role role : user.getRoles()){
                authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getName()));
                if(role.getPermissions() != null && !role.getPermissions().isEmpty()){
                    for(RolePermission permission : role.getPermissions()){
                        String authority = permission.getResource() + ":" + permission.getAction();
                        authorities.add(new SimpleGrantedAuthority(authority));
                    }
                }
            }
        }

        return new CustomUserDetails(
            user.getId(),
            user.getEmail(),
            user.getPassword(),
            authorities,
            true,
            true,
            true,
            true
        );
    }

    /**
     * Factory method: Create CustomUserDetails from JWT claims
     * Use this for authentication without database query
     */
    public static CustomUserDetails fromJwtClaims(
        UUID userId,
        String email,
        Collection<String> roles
    ) {
        // Convert a String role to GrantedAuthority
        Collection<GrantedAuthority> authorities = roles
            .stream()
            .map(SimpleGrantedAuthority::new)
            .collect(Collectors.toList());

        return new CustomUserDetails(
            userId,
            email,
            null, // No password needed for JWT authentication
            authorities,
            true,
            true,
            true,
            true
        );
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return accountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return credentialNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void eraseCredentials() {
        this.password = null; // Securely dereference the password field
    }
}
