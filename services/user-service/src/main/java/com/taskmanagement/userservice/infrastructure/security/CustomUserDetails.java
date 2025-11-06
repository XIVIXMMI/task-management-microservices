package com.taskmanagement.userservice.infrastructure.security;

import com.taskmanagement.userservice.domain.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.UUID;
import java.util.stream.Collectors;

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
    private final Collection< ? extends GrantedAuthority> authorities;

    private final boolean enabled;
    private final boolean accountNonExpired;
    private final boolean accountNonLocked;
    private final boolean credentialNonExpired;

    /**
     * Factory method: Convert User entity → CustomUserDetails
     * Call by class not instance so this function needs *static*
     */
    public static CustomUserDetails fromUser(User user){

        // Convert Roles/Permission to GrantedAuthority
        Collection<GrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toList());

        // Add role to Authorities ( FORMAT: ROLE_ADMIN, ROLE_USER)
        user.getRoles().forEach( role ->
                authorities.add(new SimpleGrantedAuthority("ROLE_" +role.getName()))
        );

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
