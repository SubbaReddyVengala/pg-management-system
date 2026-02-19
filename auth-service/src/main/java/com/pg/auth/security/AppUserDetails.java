package com.pg.auth.security;

import com.pg.auth.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;
import java.util.List;

public class AppUserDetails implements UserDetails {

    // Store the full User object — all getters delegate to it
    private final User user;

    public AppUserDetails(User user) {
        this.user = user;
    }

    // ── These are the methods JwtUtils calls ─────────────────

    public Long getId() {
        return user.getId();
    }

    public User.Role getRole() {
        return user.getRole();
    }

    // Needed by AuthService.buildAuthResponse()
    public User getUser() {
        return user;
    }

    // ── UserDetails interface methods ─────────────────────────

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // "ROLE_ADMIN" or "ROLE_TENANT" — Spring Security requires the ROLE_ prefix
        return List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));
    }

    @Override
    public String getPassword() {
        return user.getPasswordHash();
    }

    @Override
    public String getUsername() {
        return user.getEmail();   // we use email as username
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return user.isActive();
    }
}
