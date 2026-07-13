package com.dantal.security;

import com.dantal.user.entity.RoleName;
import com.dantal.user.entity.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Instant;
import java.util.Collection;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
public class UserPrincipal implements UserDetails {

    private final UUID id;
    private final UUID clinicId;
    private final String email;
    private final String passwordHash;
    private final boolean active;
    private final Instant lockedUntil;
    private final Set<RoleName> roles;
    private final Collection<? extends GrantedAuthority> authorities;

    public UserPrincipal(User user) {
        this.id = user.getId();
        this.clinicId = user.getClinic() != null ? user.getClinic().getId() : null;
        this.email = user.getEmail();
        this.passwordHash = user.getPasswordHash();
        this.active = user.isActive();
        this.lockedUntil = user.getLockedUntil();
        this.roles = user.getRoles().stream()
                .map(r -> r.getName())
                .collect(Collectors.toSet());
        this.authorities = this.roles.stream()
                .map(r -> new SimpleGrantedAuthority("ROLE_" + r.name()))
                .collect(Collectors.toSet());
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return passwordHash;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return lockedUntil == null || lockedUntil.isBefore(Instant.now());
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return active;
    }

    public boolean canAuthenticate() {
        return isEnabled() && isAccountNonExpired() && isAccountNonLocked() && isCredentialsNonExpired();
    }
}
