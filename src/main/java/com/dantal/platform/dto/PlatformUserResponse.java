package com.dantal.platform.dto;

import com.dantal.user.entity.RoleName;
import com.dantal.user.entity.User;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
@Builder
public class PlatformUserResponse {

    private UUID id;
    private String email;
    private String firstName;
    private String lastName;
    private String phone;
    private boolean active;
    private Set<RoleName> roles;
    private Instant createdAt;

    public static PlatformUserResponse from(User user) {
        return PlatformUserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .phone(user.getPhone())
                .active(user.isActive())
                .roles(user.getRoles().stream().map(r -> r.getName()).collect(Collectors.toSet()))
                .createdAt(user.getCreatedAt())
                .build();
    }
}
